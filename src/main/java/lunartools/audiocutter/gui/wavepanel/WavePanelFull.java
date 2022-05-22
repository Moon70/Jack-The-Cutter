package lunartools.audiocutter.gui.wavepanel;

import java.awt.Graphics;

import lunartools.audiocutter.AudioCutterModel;

public class WavePanelFull extends WavePanel{

	public WavePanelFull(AudioCutterModel audioCutterModel) {
		super(audioCutterModel);
		this.setBounds(0, 0, audioCutterModel.getAudiodataViewWidth(), panelHeight);
		waveController=new WaveControllerFull(audioCutterModel,this);
		addMouseListener(waveController);
		addMouseMotionListener(waveController);
		setBackground(COLOR_BACKGROUND_WAVE_FULL);
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
	public void paint(Graphics g) {
		super.paint(g);
		setBounds(getLocation().x,getLocation().y, model.getAudiodataViewWidth(), panelHeight);
	}

	@Override
	void drawScale(Graphics g) {
		g.setColor(COLOR_BACKGROUND_WAVE_ZOOM);
		final int viewStart=waveController.convertSampleNumberToScreenPosition(this,model.getViewStartInSamples());
		final int viewEnd=waveController.convertSampleNumberToScreenPosition(this,model.getViewEndInSamples());
		g.fillRect(viewStart, panelHeightWithoutScale, viewEnd-viewStart, scaleHeight);
		super.drawScale(g);
	}

}
