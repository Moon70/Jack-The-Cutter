package lunartools.audiocutter.common.service;

import java.util.ArrayList;
import java.util.Objects;

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
	private final AudioCutterModel audioCutterModel;
	private final AudioCutterController audioCutterController;
	private final int lineBufferSize=32768;
	//private final int lineBufferSize=4096*6;
	//private final int lineBufferSize=8192;
	private final int writeBufferSize=4096;

	private volatile byte[] audiodata;
	private volatile SourceDataLine sourceDataLineOutput;
	private volatile boolean isPlaying;
	private volatile boolean isPause;
	private volatile int stopOnFrame;
	private volatile int position;
	private final Object playLock = new Object();
	private byte[] buffer=new byte[writeBufferSize];

	private AudioPlayer(AudioCutterModel audioCutterModel,AudioCutterController audioCutterController) {
		this.audioCutterModel=audioCutterModel;
		this.audioCutterController=audioCutterController;
		setPriority(Thread.MAX_PRIORITY);
		setDaemon(true);
		start();
	}

	public static void initialize(AudioCutterModel model,AudioCutterController controller) {
		if(instance==null) {
			instance=new AudioPlayer(Objects.requireNonNull(model),Objects.requireNonNull(controller));
		}else {
			throw new IllegalStateException("AudioPlayer already initialized");
		}
	}

	public static AudioPlayer getInstance() {
		if(instance==null) {
			throw new IllegalStateException("AudioPlayer not initialized");
		}
		return instance;
	}

	@Override
	public void run() {
		AudioFormat format=new AudioFormat(44100.0f,16,2,true,false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		try {
			sourceDataLineOutput = (SourceDataLine) AudioSystem.getLine(info);
			sourceDataLineOutput.open(format,lineBufferSize);
			sourceDataLineOutput.start();
		} catch (LineUnavailableException e) {
			String message="error creating audio player";
			logger.error(message,e);
			audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.ERROR,message+": "+e.getMessage()));
			return;
		}
		while(!audioCutterController.isShutdownInProgress()) {
			synchronized (playLock) {
				while ((!isPlaying || isPause || audiodata == null) && !audioCutterController.isShutdownInProgress()) {
					try {
						playLock.wait();
					} catch (InterruptedException e) {
						return;
					}
				}
			}
			if(position==stopOnFrame) {
				if(sourceDataLineOutput.available()==sourceDataLineOutput.getBufferSize()) {
					stopAudio();
				}
			}else {
				//int bytesToWrite=position+writeBufferSize<=stopOnFrame?writeBufferSize:stopOnFrame-position;
				//int bytesWritten=sourceDataLineOutput.write(audiodata, position, bytesToWrite);

				//buffer=Arrays.copyOfRange(audiodata, position, position+bytesToWrite);
				//int bytesWritten=sourceDataLineOutput.write(buffer, 0, buffer.length);

				int bytesToWrite = Math.min(writeBufferSize, stopOnFrame - position);
				System.arraycopy(audiodata, position, buffer, 0, bytesToWrite);
				int bytesWritten = sourceDataLineOutput.write(buffer, 0, bytesToWrite);

				position+=bytesWritten;
				audioCutterModel.setPlayPositionSampleNumber(position>>2);

			}
		}
	}

	private void stopAudio() {
		isPlaying=false;
		isPause=false;
		audioCutterModel.setPlayPositionSampleNumber(0);
		sourceDataLineOutput.stop();
		sourceDataLineOutput.flush();
	}

	public void stopAudioPlayback() {
		synchronized (playLock) {
			stopAudio();
			playLock.notifyAll();
		}
	}

	public void playFromCursorPosition() {
		synchronized (playLock) {
			if(isPlaying) {
				stopAudio();
			}
			audiodata=audioCutterModel.getAudiodata();
			int sampleNumber=audioCutterModel.getCursorPositionSampleNumber();
			position=sampleNumber<<2;
			audioCutterModel.setPlayPositionSampleNumber(sampleNumber);
			stopOnFrame=audiodata.length-1;
			isPlaying=true;
			isPause=false;
			sourceDataLineOutput.start();
			playLock.notifyAll();
		}
	}

	public void playSelection() {
		synchronized (playLock) {
			if(isPlaying) {
				stopAudio();
			}
			audiodata=audioCutterModel.getAudiodata();
			int sampleNumber=audioCutterModel.getSelectionStartInSamples();
			position=sampleNumber<<2;
			audioCutterModel.setPlayPositionSampleNumber(sampleNumber);
			stopOnFrame=audioCutterModel.getSelectionEndInSamples()<<2;
			isPlaying=true;
			isPause=false;
			sourceDataLineOutput.start();
			playLock.notifyAll();
		}
	}

	public void playSection(int audiosectionIndex) {
		synchronized (playLock) {
			if(isPlaying) {
				stopAudio();
			}
			audiodata=audioCutterModel.getAudiodata();
			AudioSectionModel audioSection=audioCutterModel.getAudioSection(audiosectionIndex);
			int sampleNumber=audioSection.getPosition();
			position=sampleNumber<<2;
			audioCutterModel.setPlayPositionSampleNumber(sampleNumber);
			stopOnFrame=audioCutterModel.getAudiodataLengthInSamples()<<2;
			isPlaying=true;
			isPause=false;
			sourceDataLineOutput.start();
			playLock.notifyAll();
		}
	}

	public void playPrevSection() {
		synchronized (playLock) {
			if(isPlaying) {
				stopAudio();
			}
			ArrayList<AudioSectionModel> audioSections=audioCutterModel.getAudioSections();
			for(int i=audioSections.size()-1;i>0;i--) {
				AudioSectionModel audioSection=audioSections.get(i);
				if((position>>2)>audioSection.getPosition()) {
					int cursor=position=audioSections.get(i-1).getPosition();
					audioCutterModel.setCursorPositionSampleNumber(cursor);
					position=cursor<<2;
					break;
				}
			}
			isPlaying=true;
			isPause=false;
			sourceDataLineOutput.start();
			playLock.notifyAll();
		}
	}

	public void playNextSection() {
		synchronized (playLock) {
			if(isPlaying) {
				stopAudio();
			}
			ArrayList<AudioSectionModel> audioSections=audioCutterModel.getAudioSections();
			for(int i=0;i<audioSections.size();i++) {
				AudioSectionModel audioSection=audioSections.get(i);
				if((position>>2)<audioSection.getPosition()) {
					int cursor=audioSection.getPosition();
					position=cursor<<2;
					audioCutterModel.setCursorPositionSampleNumber(cursor);
					break;
				}
			}
			isPlaying=true;
			isPause=false;
			sourceDataLineOutput.start();
			playLock.notifyAll();
		}
	}

	public void pauseAudioPlayback() {
		if(isPause) {
			sourceDataLineOutput.start();
		}else {
			sourceDataLineOutput.stop();
		}
		synchronized (playLock) {
			isPause = !isPause;
			playLock.notifyAll();
		}
	}

}
