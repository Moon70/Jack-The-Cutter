package lunartools.audiocutter.service.autocut;

import java.util.ArrayList;

import lunartools.ByteTools;
import lunartools.audiocutter.AudioCutterController;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.worker.ProgressCallback;

public class AutoCutService {
	private AudioCutterModel model;
	private AudioCutterController controller;
	private ProgressCallback progressCallback;
	private final static int framesizeSilence=4410*1;
	private final static int treshhold=50;
	private final static int distance=44100*15;

	public AutoCutService(AudioCutterModel model,AudioCutterController controller,ProgressCallback progressCallback) {
		this.model=model;
		this.controller=controller;
		this.progressCallback=progressCallback;
	}

	public void autocut() {
		byte[] audiobytes=model.getAudiodata();
		ArrayList<Cutpoint> cutpoints=autocut(audiobytes,framesizeSilence,treshhold);
		for(int i=0;i<cutpoints.size();i++) {
			controller.createCutPointAt(cutpoints.get(i).getPosition());
		}
	}

	private ArrayList<Cutpoint> autocut(byte[] audiobytes, int framesize, int treshhold) {
		ArrayList<Cutpoint> cutpoints=new ArrayList<>();
		int lengthInSamples=audiobytes.length>>2;
		int index=lengthInSamples-1;
		long progress;
		while(true) {
			progress=(long)((lengthInSamples-index))*100/lengthInSamples;
			progressCallback.setProgressCallback((int)(progress));
			index=detectTreshhold(audiobytes,index,framesize,treshhold);
			if(index==0) {
				break;
			}
			Cutpoint cutpoint=new Cutpoint(index);
			cutpoints.add(cutpoint);
			finetune(cutpoint,audiobytes);
			index-=distance;
			if(index<0) {
				break;
			}
		}

		for(int i=1;i<cutpoints.size();i++) {
			Cutpoint previousCutpoint=cutpoints.get(i-1);
			Cutpoint thisCutpoint=cutpoints.get(i);
			if((previousCutpoint.getPosition()-thisCutpoint.getPosition())<distance) {
				previousCutpoint.setPosition(0);
			}
		}

		if(cutpoints.size()>0) {
			int lastIndex=cutpoints.size()-1;
			Cutpoint cutpoint=cutpoints.get(lastIndex);
			if(cutpoint.getPosition()<distance) {
				cutpoint.setPosition(0);
			}
			cutpoint=cutpoints.get(0);
			if(cutpoint.getPosition()>(lengthInSamples-distance)) {
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

	private static int detectTreshhold(byte[] bytes, int seekOffset, int framesize,int treshhold) {
		if(seekOffset<framesize) {
			return 0;
		}
		long sample=0;
		int index=seekOffset;
		int offsetAdd=index<<2;
		int offsetSub=offsetAdd;
		for(;index>seekOffset-framesize;index--,offsetAdd-=4) {
			sample+=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetAdd));
			sample+=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetAdd+2));
		}

		int average;
		for(;index>=0;index--,offsetSub-=4,offsetAdd-=4) {
			average=(int)(sample/framesize/2);
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
			int startPosition=position-44100*10;
			if(startPosition<0) {
				startPosition=0;
			}
			int endPosition=position+44100*5;
			if(endPosition>=audioLengthInsamples) {
				endPosition=audioLengthInsamples-1;
			}

			long sample=0;
			long silenceValue=Long.MAX_VALUE;
			int silenceIndex=0;
			int index=endPosition;
			int offsetAdd=index<<2;
			int offsetSub=offsetAdd;

			final int finetune_framesite=4410*2;

			for(;index>endPosition-finetune_framesite;index--,offsetAdd-=4) {
				sample+=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetAdd));
				sample+=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetAdd+2));
			}
			if(silenceValue>sample) {
				silenceValue=sample;
				silenceIndex=index+(finetune_framesite>>1);
			}

			for(;index>=startPosition;index--,offsetSub-=4,offsetAdd-=4) {
				sample-=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetSub));
				sample-=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetSub+2));
				sample+=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetAdd));
				sample+=Math.abs(ByteTools.lBytearrayToSignedWord(bytes, offsetAdd+2));
				if(silenceValue>sample) {
					silenceValue=sample;
					silenceIndex=index+(finetune_framesite>>1);
				}
			}
			cutpoint.setPosition(silenceIndex);
			cutpoint.setLevel((int)(silenceValue/finetune_framesite));
	}

}
