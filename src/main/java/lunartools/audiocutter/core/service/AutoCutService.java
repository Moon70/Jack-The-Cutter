package lunartools.audiocutter.core.service;

import java.util.ArrayList;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.ByteTools;
import lunartools.audiocutter.common.model.AudioSectionModel;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.infrastructure.config.AudioCutterSettings;
import lunartools.swing.ProgressStepListener;

public class AutoCutService {
	private static Logger logger = LoggerFactory.getLogger(AutoCutService.class);
	private static final int FRAMESPERSECOND=44100;
	private final AudioCutterModel audioCutterModel;
	private int silenceLengthInFrames;
	private int treshhold;
	private int distanceInFrames;
	private int finetuneLeftLengthInFrames;
	private int finetuneRightLengthInFrames;
	private int finetuneSilenceLengthInFrames;
	private final int totalProgressSteps=100;

	public AutoCutService(AudioCutterModel audioCutterModel) {
		this.audioCutterModel=Objects.requireNonNull(audioCutterModel);
	}

	public void autocut(ProgressStepListener progressStepListener) {
		try {
			byte[] audiobytes=audioCutterModel.getAudiodata();
			AudioCutterSettings settings=AudioCutterSettings.getInstance();
			int silenceLengthInTenthOfSeconds=settings.getInt(AudioCutterSettings.AUTOCUT_SILENCE_LENGTH);
			silenceLengthInFrames=silenceLengthInTenthOfSeconds*4410;
			treshhold=settings.getInt(AudioCutterSettings.AUTOCUT_TRESHHOLD);
			int distanceInSeconds=settings.getInt(AudioCutterSettings.AUTOCUT_DISTANCE);
			distanceInFrames=distanceInSeconds*FRAMESPERSECOND;

			int finetuneLeftLengthInSeconds=settings.getInt(AudioCutterSettings.AUTOCUT_FINETUNE_LEFT);
			finetuneLeftLengthInFrames=finetuneLeftLengthInSeconds*FRAMESPERSECOND;
			int finetuneRightLengthInSeconds=settings.getInt(AudioCutterSettings.AUTOCUT_FINETUNE_RIGHT);
			finetuneRightLengthInFrames=finetuneRightLengthInSeconds*FRAMESPERSECOND;
			int finetuneSilenceLengthInTenthOfSeconds=settings.getInt(AudioCutterSettings.AUTOCUT_FINETUNE_SILENCE_LENGTH);
			finetuneSilenceLengthInFrames=finetuneSilenceLengthInTenthOfSeconds*4410;

			ArrayList<Cutpoint> cutpoints=autocut(audiobytes,progressStepListener);
			for(int i=0;i<cutpoints.size();i++) {
				createCutPointAt(cutpoints.get(i).getPosition());
			}

		} catch (Exception e) {
			logger.error("error while autocutting",e);
			throw new AutoCutException(e);
		}
	}

	private ArrayList<Cutpoint> autocut(byte[] audiobytes,ProgressStepListener progressStepListener) {
		ArrayList<Cutpoint> cutpoints=new ArrayList<>();
		int lengthInSamples=audiobytes.length>>2;
			int index=lengthInSamples-1;
			while(true) {
				index=detectTreshhold(audiobytes,index,progressStepListener);
				if(index==0) {
					break;
				}
				Cutpoint cutpoint=new Cutpoint(index);
				cutpoints.add(cutpoint);
				finetune(cutpoint,audiobytes);
				index-=distanceInFrames;
				if(index<0) {
					break;
				}
			}

			for(int i=1;i<cutpoints.size();i++) {
				Cutpoint previousCutpoint=cutpoints.get(i-1);
				Cutpoint thisCutpoint=cutpoints.get(i);
				if((previousCutpoint.getPosition()-thisCutpoint.getPosition())<distanceInFrames) {
					previousCutpoint.setPosition(0);
				}
			}

			if(cutpoints.size()>0) {
				int lastIndex=cutpoints.size()-1;
				Cutpoint cutpoint=cutpoints.get(lastIndex);
				if(cutpoint.getPosition()<distanceInFrames) {
					cutpoint.setPosition(0);
				}
				cutpoint=cutpoints.get(0);
				if(cutpoint.getPosition()>(lengthInSamples-distanceInFrames)) {
					cutpoint.setPosition(0);
				}
			}

			for(int i=cutpoints.size()-1;i>=0;i--) {
				if(cutpoints.get(i).getPosition()==0) {
					cutpoints.remove(i);
				}
			}
			return cutpoints;
	}

	private int detectTreshhold(byte[] bytes, int seekOffset,ProgressStepListener progressStepListener) {
		if(seekOffset<silenceLengthInFrames) {
			return 0;
		}
		long sample=0;
		int index=seekOffset;
		int offsetAdd=index<<2;
		int offsetSub=offsetAdd;
		for(;index>seekOffset-silenceLengthInFrames;index--,offsetAdd-=4) {
			sample+=Math.abs(ByteTools.littleEndianBytesToSignedWord(bytes, offsetAdd));
			sample+=Math.abs(ByteTools.littleEndianBytesToSignedWord(bytes, offsetAdd+2));
		}

		int average;
		long currentProgressStep=0;
		int totalSamples=bytes.length/4;
		for(;index>=0;index--,offsetSub-=4,offsetAdd-=4) {
			if((index & 0x7fffff)==0) {
				currentProgressStep=((long)(totalSamples-index)*totalProgressSteps)/totalSamples;
				System.out.println(index+"\t"+currentProgressStep+"\t"+(index*totalProgressSteps)+"\t"+(bytes.length/4));
				progressStepListener.stepDone((int)currentProgressStep, totalProgressSteps, null);
			}
			average=(int)(sample/silenceLengthInFrames/2);
			if(average<=treshhold) {
				return index;
			}
			sample-=Math.abs(ByteTools.littleEndianBytesToSignedWord(bytes, offsetSub));
			sample-=Math.abs(ByteTools.littleEndianBytesToSignedWord(bytes, offsetSub+2));
			sample+=Math.abs(ByteTools.littleEndianBytesToSignedWord(bytes, offsetAdd));
			sample+=Math.abs(ByteTools.littleEndianBytesToSignedWord(bytes, offsetAdd+2));
		}
		return 0;
	}

	private void finetune(Cutpoint cutpoint,byte[] bytes) {
		int audioLengthInsamples=bytes.length>>2;
			int position=cutpoint.getPosition();
			int startPosition=position-finetuneLeftLengthInFrames;
			if(startPosition<0) {
				startPosition=0;
			}
			int endPosition=position+finetuneRightLengthInFrames;
			if(endPosition>=audioLengthInsamples) {
				endPosition=audioLengthInsamples-1;
			}

			long sample=0;
			long silenceValue=Long.MAX_VALUE;
			int silenceIndex=0;
			int index=endPosition;
			int offsetAdd=index<<2;
			int offsetSub=offsetAdd;

			for(;index>endPosition-finetuneSilenceLengthInFrames;index--,offsetAdd-=4) {
				sample+=Math.abs(ByteTools.littleEndianBytesToSignedWord(bytes, offsetAdd));
				sample+=Math.abs(ByteTools.littleEndianBytesToSignedWord(bytes, offsetAdd+2));
			}
			if(silenceValue>sample) {
				silenceValue=sample;
				silenceIndex=index+(finetuneSilenceLengthInFrames>>1);
			}

			for(;index>=startPosition;index--,offsetSub-=4,offsetAdd-=4) {
				sample-=Math.abs(ByteTools.littleEndianBytesToSignedWord(bytes, offsetSub));
				sample-=Math.abs(ByteTools.littleEndianBytesToSignedWord(bytes, offsetSub+2));
				sample+=Math.abs(ByteTools.littleEndianBytesToSignedWord(bytes, offsetAdd));
				sample+=Math.abs(ByteTools.littleEndianBytesToSignedWord(bytes, offsetAdd+2));
				if(silenceValue>sample) {
					silenceValue=sample;
					silenceIndex=index+(finetuneSilenceLengthInFrames>>1);
				}
			}
			cutpoint.setPosition(silenceIndex);
			cutpoint.setLevel((int)(silenceValue/finetuneSilenceLengthInFrames));
	}

	public void createCutPointAt(int sampleNumber) {
		ArrayList<AudioSectionModel> audioSections=audioCutterModel.getAudioSections();
		if(audioSections.size()==0) {
			audioSections.add(new AudioSectionModel(0));
			audioSections.add(new AudioSectionModel(sampleNumber));
			audioCutterModel.setAudioSections(audioSections);
			return;
		}else {
			for(int i=0;i<audioSections.size();i++) {
				AudioSectionModel audioSection=audioSections.get(i);
				if(audioSection.getPosition()==sampleNumber) {
					return;
				}
				if(audioSection.getPosition()>sampleNumber) {
					audioSections.add(i, new AudioSectionModel(sampleNumber));
					audioCutterModel.setAudioSections(audioSections);
					return;
				}
			}
			audioSections.add(new AudioSectionModel(sampleNumber));
			audioCutterModel.setAudioSections(audioSections);
		}
	}

}
