package lunartools.audiocutter.core.view;

import java.awt.Graphics;

import lunartools.audiocutter.common.ui.ColorManager;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.controller.WaveControllerFull;

public class WavePanelFull extends AWavePanel{

	public WavePanelFull(AudioCutterModel audioCutterModel) {
		super(audioCutterModel);
		setLocation(0, 0);
		waveController=new WaveControllerFull(audioCutterModel,this);
		addMouseListener(waveController);
		addMouseMotionListener(waveController);
		setBackground(ColorManager.backgroundWaveFull());
	}

	@Override
	public void drawScale(Graphics g) {
		g.setColor(ColorManager.backgroundWaveZoom());
		final int viewStart=waveController.convertSampleNumberToScreenPosition(audioCutterModel.getViewStartInSamples());
		final int viewEnd=waveController.convertSampleNumberToScreenPosition(audioCutterModel.getViewEndInSamples());
		g.fillRect(viewStart, zoomMarkOffset, viewEnd-viewStart, zoomMarkHeight);
		super.drawScale(g);
	}

}
