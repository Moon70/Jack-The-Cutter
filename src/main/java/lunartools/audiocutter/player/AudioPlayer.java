package lunartools.audiocutter.player;

import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterController;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.AudioSection;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;

public class AudioPlayer extends Thread{
	private static Logger logger = LoggerFactory.getLogger(AudioPlayer.class);
	private static AudioPlayer instance;
	private final AudioCutterModel model;
	private final AudioCutterController controller;
	private byte[] audiodata;
	private int control_lastByte;

	private volatile boolean control_play;
	private volatile int position;
	private boolean isPlaying;
	private boolean isPause;

	public static AudioPlayer getInstance(AudioCutterModel model,AudioCutterController controller) {
		if(instance==null) {
			instance=new AudioPlayer(model,controller);
		}
		return instance;
	}

	public static AudioPlayer getInstance() {
		if(instance==null) {
			throw new RuntimeException("Singleton not initialized yet");
		}
		return instance;
	}

	private AudioPlayer(AudioCutterModel model,AudioCutterController controller) {
		this.model=model;
		this.controller=controller;
	}

	public void action_playFromCursorPosition() {
		audiodata=model.getAudiodata();
		position=model.getCursorPositionSampleNumber()<<2;
		control_lastByte=audiodata.length-1;
		control_play=true;
		isPause=false;
	}

	public void action_playSelection() {
		audiodata=model.getAudiodata();
		position=model.getSelectionStartInSamples()<<2;
		control_lastByte=model.getSelectionEndInSamples()<<2;
		control_play=true;
		isPause=false;
	}

	public void playSection(int audiosectionIndex) {
		audiodata=model.getAudiodata();
		AudioSection audioSection=model.getAudioSection(audiosectionIndex);
		position=audioSection.getPosition()<<2;
		control_lastByte=model.getAudiodataLengthInSamples()<<2;
		control_play=true;
		isPause=false;
	}

	public void action_pause() {
		isPause=!isPause;
	}

	public void action_stop() {
		control_play=false;
		isPause=false;
		model.setPlayPositionSampleNumber(0);
	}

	public void action_PrevSection() {
		ArrayList<AudioSection> audioSections=model.getAudioSections();
		for(int i=audioSections.size()-1;i>0;i--) {
			AudioSection audioSection=audioSections.get(i);
			if((position>>2)>audioSection.getPosition()) {
				int cursor=position=audioSections.get(i-1).getPosition();
				model.setCursorPositionSampleNumber(cursor);
				position=cursor<<2;
				break;
			}
		}
	}

	public void action_NextSection() {
		ArrayList<AudioSection> audioSections=model.getAudioSections();
		for(int i=0;i<audioSections.size();i++) {
			AudioSection audioSection=audioSections.get(i);
			if((position>>2)<audioSection.getPosition()) {
				int cursor=audioSection.getPosition();
				position=cursor<<2;
				model.setCursorPositionSampleNumber(cursor);
				break;
			}
		}
	}

	@Override
	public void run() {
		super.run();
		final int framesize=4096;
		AudioFormat format=new AudioFormat((float) 44100.0,16,2,true,false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine sourceDataLineOutput;
		try {
			sourceDataLineOutput = (SourceDataLine) AudioSystem.getLine(info);
			final int bufferSize=model.getAudioPlayerBufferSize();
			sourceDataLineOutput.open(format,bufferSize);
			sourceDataLineOutput.start();
		} catch (LineUnavailableException e) {
			String message="error creating audio player";
			logger.error(message,e);
			model.setStatusMessage(new StatusMessage(StatusMessage.Type.ERROR,message+": "+e.getMessage()));
			return;
		}
		byte[] buffer;
		while(!controller.isShutdownInProgress()) {
			if(control_play && !isPause && audiodata!=null) {
				if(!isPlaying) {
					sourceDataLineOutput.start();
					isPlaying=true;
				}
				model.setPlayPositionSampleNumber(position>>2);
				try {
					int endRange=position+framesize;
					if(endRange>control_lastByte) {
						endRange=control_lastByte & 0xfffffffc;
						control_play=false;
					}
					if(position<endRange) {
						buffer=Arrays.copyOfRange(audiodata, position, endRange);
						sourceDataLineOutput.write(buffer, 0, buffer.length);
					}
					position+=framesize;
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
					action_stop();
				}
			}else{
				if(isPlaying && !isPause) {
					isPlaying=false;
					sourceDataLineOutput.drain();
					sourceDataLineOutput.stop();
					audiodata=null;
				}
				try {
					sleep(200);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}

}
