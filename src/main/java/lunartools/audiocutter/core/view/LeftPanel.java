package lunartools.audiocutter.core.view;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.gui.wavepanel.WavePanelFull;
import lunartools.audiocutter.gui.wavepanel.WavePanelZoom;

public class LeftPanel extends JPanel{
	private ButtonPanel buttonPanel;
	private WavePanelFull wavePanelFull;
	private WavePanelZoom wavePanelZoom;
	private ScrollbarsPanel scrollbarsPanel;

	private StatusPanel panelStatus;

	public LeftPanel(AudioCutterModel model) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//		model.addChangeListener(this::updateModelChanges);

		buttonPanel=new ButtonPanel(model);
		buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
		add(buttonPanel);

		wavePanelFull=new WavePanelFull(model);
		wavePanelFull.setAlignmentX(LEFT_ALIGNMENT);
		add(wavePanelFull);

		wavePanelZoom=new WavePanelZoom(model);
		wavePanelZoom.setAlignmentX(LEFT_ALIGNMENT);
		add(wavePanelZoom);

		scrollbarsPanel=new ScrollbarsPanel(model);
		scrollbarsPanel.setAlignmentX(LEFT_ALIGNMENT);
		add(scrollbarsPanel);

		panelStatus=new StatusPanel(model);
		panelStatus.setAlignmentX(LEFT_ALIGNMENT);
		add(panelStatus);

		//setBackground(new Color(0xccffff));
	}

	public void updateModelChanges(Object object) {
		if(object==SimpleEvents.MODEL_FRAMESIZECHANGED) {
		}
	}

	public ButtonPanel getButtonPanel() {
		return buttonPanel;
	}

	public StatusPanel getPanelStatus() {
		return panelStatus;
	}

}
