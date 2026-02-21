package lunartools.audiocutter.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.core.AudioCutterController;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.view.ButtonPanel;
import lunartools.audiocutter.core.view.ScrollbarsPanel;
import lunartools.audiocutter.core.view.StatusPanel;
import lunartools.audiocutter.gui.wavepanel.WavePanelFull;
import lunartools.audiocutter.gui.wavepanel.WavePanelZoom;

public class LeftPanel extends JPanel{
	private static Logger logger = LoggerFactory.getLogger(LeftPanel.class);

	private ButtonPanel buttonPanel;
	private WavePanelFull wavePanelFull;
	private WavePanelZoom wavePanelZoom;
	private ScrollbarsPanel scrollbarsPanel;

	private StatusPanel panelStatus;

	public LeftPanel(AudioCutterModel model,AudioCutterController controller) {
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
