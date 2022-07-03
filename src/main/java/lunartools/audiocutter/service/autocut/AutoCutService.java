package lunartools.audiocutter.service.autocut;

import java.util.ArrayList;

import lunartools.ByteTools;
import lunartools.Settings;
import lunartools.audiocutter.AudioCutterController;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.AudioCutterSettings;
import lunartools.audiocutter.worker.ProgressCallback;

public class AutoCutService {
	private AudioCutterModel model;
	private AudioCutterController controller;
	private ProgressCallback progressCallback;
	private static final int FRAMESPERSECOND=44100;
	private int silenceLengthInFrames;
	private int treshhold;
	private int distanceInFrames;
	private int finetuneLeftLengthInFrames;
	private int finetuneRightLengthInFrames;
	private int finetuneSilenceLengthInFrames;


	public AutoCutService(AudioCutterModel model,AudioCutterController controller,ProgressCallback progressCallback) {
		this.model=model;
		this.controller=controller;
		this.progressCallback=progressCallback;
	}

	public void autocut() {
		byte[] audiobytes=model.getAudiodata();
		Settings settings=AudioCutterSettings.getSettings();
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

		ArrayList<Cutpoint> cutpoints=autocut(audiobytes);
		for(int i=0;i<cutpoints.size();i++) {
			controller.createCutPointAt(cutpoints.get(i).getPosition());
		}
	}

	private ArrayList<Cutpoint> autocut(byte[] audiobytes) {
		ArrayList<Cutpoint> cutpoints=new ArrayList<>();
		int lengthInSamples=audiobytes.length>>2;
		int index=lengthInSamples-1;
		long progress;
		while(true) {
			progress=(long)((lengthInSamples-index))*100/lengthInSamples;
			progressCallback.setProgressCallback((int)(progress));
			index=detectTreshhold(audiobytes,index);
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

	private int detectTreshhold(byte[] bytes, int seekOffset) {
		if(seekOffset<silenceLengthInFrames) {
			return 0;
		}
		long sample=0;
		int index=seekOffset;
		int offsetAdd=index<<2;
		int offsetSub=offsetAdd;
		for(;index>seekOffset-silenceLengthInFrames;index--,offsetAdd-=4) {
			sample+=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetAdd));
			sample+=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetAdd+2));
		}

		int average;
		for(;index>=0;index--,offsetSub-=4,offsetAdd-=4) {
			average=(int)(sample/silenceLengthInFrames/2);
			if(average<=treshhold) {
				return index;
			}
			sample-=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetSub));
			sample-=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetSub+2));
			sample+=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetAdd));
			sample+=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetAdd+2));
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
			sample+=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetAdd));
			sample+=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetAdd+2));
		}
		if(silenceValue>sample) {
			silenceValue=sample;
			silenceIndex=index+(finetuneSilenceLengthInFrames>>1);
		}

		for(;index>=startPosition;index--,offsetSub-=4,offsetAdd-=4) {
			sample-=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetSub));
			sample-=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetSub+2));
			sample+=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetAdd));
			sample+=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetAdd+2));
			if(silenceValue>sample) {
				silenceValue=sample;
				silenceIndex=index+(finetuneSilenceLengthInFrames>>1);
			}
		}
		cutpoint.setPosition(silenceIndex);
		cutpoint.setLevel((int)(silenceValue/finetuneSilenceLengthInFrames));
	}

}
