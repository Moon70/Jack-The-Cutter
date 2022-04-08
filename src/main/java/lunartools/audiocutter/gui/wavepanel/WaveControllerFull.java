package lunartools.audiocutter.gui.wavepanel;

import lunartools.audiocutter.AudioCutterModel;

public class WaveControllerFull extends WaveController {

	public WaveControllerFull(AudioCutterModel model,WavePanel view) {
		super(model,view);
	}

	@Override
	int getViewStartInSamples() {
		return 0;
	}

	@Override
	int getViewEndInSamples() {
		return model.getAudiodataLengthInSamples()-1;
	}

	@Override
	protected void resetSectionMarks() {}

	@Override
	protected void addSectionMark(int i, int playPosPixel) {}

	@Override
	protected int getSelectedSection(int sectionPosPixel) {
		return -1;
	}

}
