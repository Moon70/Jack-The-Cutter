package lunartools.audiocutter.core.view;

import java.awt.GridLayout;

import javax.swing.JPanel;

import lunartools.audiocutter.core.AudioCutterModel;

public class WavePanel extends JPanel{

	public WavePanel(AudioCutterModel audioCutterModel) {
		setLayout(new GridLayout(2, 1));

		WavePanelFull wavePanelFull=new WavePanelFull(audioCutterModel);
		wavePanelFull.setAlignmentX(LEFT_ALIGNMENT);
		add(wavePanelFull);

		WavePanelZoom wavePanelZoom=new WavePanelZoom(audioCutterModel);
		wavePanelZoom.setAlignmentX(LEFT_ALIGNMENT);
		add(wavePanelZoom);
	}

}
