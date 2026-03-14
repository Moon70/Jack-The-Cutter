package lunartools.audiocutter.core.controller;

import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.view.AWavePanel;

public class WaveControllerFull extends WaveController {

	public WaveControllerFull(AudioCutterModel model,AWavePanel view) {
		super(model,view);
	}

	@Override
	public int getViewStartInSamples() {
		return 0;
	}

	@Override
	public int getViewEndInSamples() {
		return audioCutterModel.getAudiodataLengthInSamples()-1;
	}

	@Override
	public void resetSectionMarks() {}

	@Override
	public void addSectionMark(int i, int playPosPixel) {}

	@Override
	protected int getSelectedSection(int sectionPosPixel) {
		return -1;
	}

}
