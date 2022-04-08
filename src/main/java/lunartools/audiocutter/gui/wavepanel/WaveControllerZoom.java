package lunartools.audiocutter.gui.wavepanel;

import java.util.HashMap;

import lunartools.audiocutter.AudioCutterModel;

public class WaveControllerZoom extends WaveController {
	private HashMap<Integer, Integer> hashmapSectionmarks;

	public WaveControllerZoom(AudioCutterModel model,WavePanel view) {
		super(model,view);
	}

	@Override
	int getViewStartInSamples() {
		return model.getViewStartInSamples();
	}

	@Override
	int getViewEndInSamples() {
		return model.getViewEndInSamples();
	}

	@Override
	protected void resetSectionMarks() {
		hashmapSectionmarks=new HashMap<Integer, Integer>();
	}

	@Override
	protected void addSectionMark(int audioSection, int sectionPosPixel) {
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
