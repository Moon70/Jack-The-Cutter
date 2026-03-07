package lunartools.audiocutter.common.service;

import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.common.model.AudioSectionModel;
import lunartools.audiocutter.core.AudioCutterController;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.model.StatusMessage;

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
	private final Object playLock = new Object();

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
		setPriority(Thread.MAX_PRIORITY);
	}

	public void action_playFromCursorPosition() {
		synchronized (playLock) {
			audiodata=model.getAudiodata();
			position=model.getCursorPositionSampleNumber()<<2;
			control_lastByte=audiodata.length-1;
			control_play=true;
			isPause=false;
			playLock.notifyAll();
		}
	}

	public void action_playSelection() {
		synchronized (playLock) {
			audiodata=model.getAudiodata();
			position=model.getSelectionStartInSamples()<<2;
			control_lastByte=model.getSelectionEndInSamples()<<2;
			control_play=true;
			isPause=false;
			playLock.notifyAll();
		}
	}

	public void playSection(int audiosectionIndex) {
		synchronized (playLock) {
			audiodata=model.getAudiodata();
			AudioSectionModel audioSection=model.getAudioSection(audiosectionIndex);
			position=audioSection.getPosition()<<2;
			control_lastByte=model.getAudiodataLengthInSamples()<<2;
			control_play=true;
			isPause=false;
			playLock.notifyAll();
		}
	}

	public void action_pause() {
		synchronized (playLock) {
			isPause=!isPause;
			playLock.notifyAll();
		}
	}

	public void action_stop() {
		synchronized (playLock) {
			control_play=false;
			isPause=false;
			model.setPlayPositionSampleNumber(0);
			playLock.notifyAll();
		}
	}

	public void action_PrevSection() {
		synchronized (playLock) {
			ArrayList<AudioSectionModel> audioSections=model.getAudioSections();
			for(int i=audioSections.size()-1;i>0;i--) {
				AudioSectionModel audioSection=audioSections.get(i);
				if((position>>2)>audioSection.getPosition()) {
					int cursor=position=audioSections.get(i-1).getPosition();
					model.setCursorPositionSampleNumber(cursor);
					position=cursor<<2;
					break;
				}
			}
			playLock.notifyAll();
		}
	}

	public void action_NextSection() {
		synchronized (playLock) {
			ArrayList<AudioSectionModel> audioSections=model.getAudioSections();
			for(int i=0;i<audioSections.size();i++) {
				AudioSectionModel audioSection=audioSections.get(i);
				if((position>>2)<audioSection.getPosition()) {
					int cursor=audioSection.getPosition();
					position=cursor<<2;
					model.setCursorPositionSampleNumber(cursor);
					break;
				}
			}
			playLock.notifyAll();
		}
	}

	@Override
	public void run() {
		final int bufferSize=16384;
		AudioFormat format=new AudioFormat(44100.0f,16,2,true,false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine sourceDataLineOutput;
		try {
			sourceDataLineOutput = (SourceDataLine) AudioSystem.getLine(info);
			sourceDataLineOutput.open(format,65536);
			sourceDataLineOutput.start();
		} catch (LineUnavailableException e) {
			String message="error creating audio player";
			logger.error(message,e);
			model.setStatusMessage(new StatusMessage(StatusMessage.Type.ERROR,message+": "+e.getMessage()));
			return;
		}

		while(!controller.isShutdownInProgress()) {
			synchronized (playLock) {
				while ((!control_play || isPause || audiodata == null) && !controller.isShutdownInProgress()) {
					try {
						playLock.wait();
					} catch (InterruptedException e) {
						return;
					}
				}
			}

			if(control_play && !isPause && audiodata!=null) {
				if(!isPlaying) {
					sourceDataLineOutput.start();
					isPlaying=true;
				}
				model.setPlayPositionSampleNumber(position>>2);
				try {
					int endRange=position+bufferSize;
					if(endRange>control_lastByte) {
						endRange=control_lastByte & 0xfffffffc;
						control_play=false;
					}
					if(position<endRange) {
						int len=endRange-position;
						sourceDataLineOutput.write(audiodata, position, len);
						position+=len;
					}
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
				//				try {
				//					sleep(200);
				//				} catch (InterruptedException e) {
				//					return;
				//				}
			}
		}
	}

}
