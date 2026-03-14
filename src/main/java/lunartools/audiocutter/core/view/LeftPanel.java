package lunartools.audiocutter.core.view;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import lunartools.audiocutter.core.AudioCutterModel;

public class LeftPanel extends JPanel{
	private ButtonPanel buttonPanel;
	private WavePanel wavPanel;
	private ScrollbarsPanel scrollbarsPanel;
	private StatusPanel panelStatus;

	public LeftPanel(AudioCutterModel model) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		buttonPanel=new ButtonPanel(model);
		buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
		add(buttonPanel);

		wavPanel=new WavePanel(model);
		wavPanel.setAlignmentX(LEFT_ALIGNMENT);
		add(wavPanel);

		scrollbarsPanel=new ScrollbarsPanel(model);
		scrollbarsPanel.setAlignmentX(LEFT_ALIGNMENT);
		add(scrollbarsPanel);

		panelStatus=new StatusPanel(model);
		panelStatus.setAlignmentX(LEFT_ALIGNMENT);
		add(panelStatus);
	}

	public ButtonPanel getButtonPanel() {
		return buttonPanel;
	}

	public StatusPanel getPanelStatus() {
		return panelStatus;
	}

	public WavePanel getWavePanel() {
		return wavPanel;
	}

}
