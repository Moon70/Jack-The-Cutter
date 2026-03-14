package lunartools.audiocutter.core.controller;

import java.util.HashMap;

import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.view.AWavePanel;

public class WaveControllerZoom extends WaveController {
	private HashMap<Integer, Integer> hashmapSectionmarks;

	public WaveControllerZoom(AudioCutterModel model,AWavePanel view) {
		super(model,view);
	}

	@Override
	public int getViewStartInSamples() {
		return audioCutterModel.getViewStartInSamples();
	}

	@Override
	public int getViewEndInSamples() {
		return audioCutterModel.getViewEndInSamples();
	}

	@Override
	public void resetSectionMarks() {
		hashmapSectionmarks=new HashMap<Integer, Integer>();
	}

	@Override
	public void addSectionMark(int audioSection, int sectionPosPixel) {
		hashmapSectionmarks.put(sectionPosPixel-2,audioSection);
		hashmapSectionmarks.put(sectionPosPixel-1,audioSection);
		hashmapSectionmarks.put(sectionPosPixel,audioSection);
		hashmapSectionmarks.put(sectionPosPixel+1,audioSection);
		hashmapSectionmarks.put(sectionPosPixel+2,audioSection);
	}

	@Override
	protected int getSelectedSection(int sectionPosPixel) {
		if(hashmapSectionmarks!=null && hashmapSectionmarks.containsKey(sectionPosPixel)) {
			return hashmapSectionmarks.get(sectionPosPixel);
		}
		return -1;
	}

}
