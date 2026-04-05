package lunartools.audiocutter.core.view;

import java.awt.Graphics;

import lunartools.audiocutter.common.ui.ColorManager;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.controller.WaveControllerZoom;

public class WavePanelZoom extends AWavePanel{

	public WavePanelZoom(AudioCutterModel audioCutterModel) {
		super(audioCutterModel);
		setLocation(0, 0);
		waveController=new WaveControllerZoom(audioCutterModel,this);
		addMouseListener(waveController);
		addMouseMotionListener(waveController);
		setBackground(ColorManager.backgroundWaveZoom());
	}

	@Override
	public void drawAmplitudeZoom(Graphics g,int index,int sample1,int sample2, int previousSample1,int previousSample2) {
		if(audioCutterModel.isAmplitudeZoom()) {
			g.setColor(ColorManager.extraZoom());

			int sample1Z=(sample1*(channelHeightHalve))>>10;
			if(sample1Z>channelHeightHalve) {
				sample1Z=channelHeightHalve-1;
			}else if(sample1Z<-channelHeightHalve) {
				sample1Z=-channelHeightHalve+1;
			}
			int sample2Z=(sample2*(channelHeightHalve))>>10;
			if(sample2Z>channelHeightHalve) {
				sample2Z=channelHeightHalve-1;
			}else if(sample2Z<-channelHeightHalve) {
				sample2Z=-channelHeightHalve+1;
			}

			g.drawLine(index-1, channel1CenterY-previousSample1, index, channel1CenterY-sample1Z);
			g.drawLine(index-1, channel2CenterY-previousSample2, index, channel2CenterY-sample2Z);

			g.setColor(ColorManager.wave());
		}
	}
}
