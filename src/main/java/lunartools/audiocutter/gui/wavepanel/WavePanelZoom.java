package lunartools.audiocutter.gui.wavepanel;

import java.awt.Color;
import java.awt.Graphics;

import lunartools.audiocutter.AudioCutterModel;

public class WavePanelZoom extends WavePanel{

	public WavePanelZoom(AudioCutterModel audioCutterModel) {
		super(audioCutterModel);
		this.setBounds(0, 0, audioCutterModel.getAudiodataViewWidth(), panelHeight);
		waveController=new WaveControllerZoom(audioCutterModel,this);
		addMouseListener(waveController);
		addMouseMotionListener(waveController);
		setBackground(COLOR_BACKGROUND_WAVE_ZOOM);
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
	public void paint(Graphics g) {
		super.paint(g);
		setBounds(getLocation().x,getLocation().y, model.getAudiodataViewWidth(), panelHeight);
	}

	@Override
	void drawAmplitudeZoom(Graphics g,int index,int sample1,int sample2, int previousSample1,int previousSample2) {
		if(model.isAmplitudeZoom()) {
			g.setColor(COLOR_EXTRA_ZOOM);

			int sample1Z=(sample1*(channelHeight>>1))>>10;
			if(sample1Z>channelHeightHalve) {
				sample1Z=channelHeightHalve-1;
			}else if(sample1Z<-channelHeightHalve) {
				sample1Z=-channelHeightHalve+1;
			}
			int sample2Z=(sample2*(channelHeight>>1))>>10;
			if(sample2Z>channelHeightHalve) {
				sample2Z=channelHeightHalve-1;
			}else if(sample2Z<-channelHeightHalve) {
				sample2Z=-channelHeightHalve+1;
			}

			g.drawLine(channelOffsetX+index-1, channel1CenterY-previousSample1, channelOffsetX+index, channel1CenterY-sample1Z);
			g.drawLine(channelOffsetX+index-1, channel2CenterY-previousSample2, channelOffsetX+index, channel2CenterY-sample2Z);

			g.setColor(Color.BLACK);
		}

	}
}
