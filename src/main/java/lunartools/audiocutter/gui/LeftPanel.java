package lunartools.audiocutter.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterController;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.SimpleEvents;
import lunartools.audiocutter.gui.buttonpanel.ButtonPanel;
import lunartools.audiocutter.gui.scrollbarspanel.ScrollbarsPanel;
import lunartools.audiocutter.gui.wavepanel.WavePanelFull;
import lunartools.audiocutter.gui.wavepanel.WavePanelZoom;

public class LeftPanel extends JPanel implements Observer{
	private static Logger logger = LoggerFactory.getLogger(LeftPanel.class);

	private ButtonPanel buttonPanel;
	private WavePanelFull wavePanelFull;
	private WavePanelZoom wavePanelZoom;
	private ScrollbarsPanel scrollbarsPanel;
	
	private JPanel panelStatus;
	
	private int marginY=16;
	private int column1X=10;

	public LeftPanel(AudioCutterModel model,AudioCutterController controller) {
		this.setLayout(null);
		model.addObserver(this);

		int y=0;
		
		buttonPanel=new ButtonPanel(model,controller);
		buttonPanel.setLocation(column1X, y);
		add(buttonPanel);

		y+=buttonPanel.getHeight();

		wavePanelFull=new WavePanelFull(model);
		add(wavePanelFull);
		wavePanelFull.setLocation(column1X, y);

		y+=wavePanelFull.getHeight()+marginY;

		wavePanelZoom=new WavePanelZoom(model);
		add(wavePanelZoom);
		wavePanelZoom.setLocation(column1X, y);

		y+=wavePanelZoom.getHeight()+marginY;

		scrollbarsPanel=new ScrollbarsPanel(model);
		add(scrollbarsPanel);
		scrollbarsPanel.setLocation(column1X, y);

		y+=scrollbarsPanel.getHeight()+marginY;

		panelStatus=controller.getStatusController().getStatusPanel();
		panelStatus.setLocation(column1X, y);
		add(panelStatus);

		y+=panelStatus.getHeight()+marginY;

		Dimension size=new Dimension(model.getAudiodataViewWidth(),y);
		setSize(size);

		//setBackground(new Color(0xccffff));
	}

	public void refreshLayout() {
		int y=0;
		buttonPanel.setLocation(column1X, y);
		y+=buttonPanel.getHeight();
		
		wavePanelFull.setLocation(column1X, y);
		y+=wavePanelFull.getHeight()+marginY;

		wavePanelZoom.setLocation(column1X, y);
		y+=wavePanelZoom.getHeight()+marginY;

		scrollbarsPanel.setLocation(column1X, y);
		y+=scrollbarsPanel.getHeight()+marginY;
		
		panelStatus.setLocation(column1X, y);
	}
	
	@Override
	public void update(Observable observable, Object object) {
		if(object==SimpleEvents.MODEL_FRAMESIZECHANGED) {
			refreshLayout();
		}
	}

	@Override
	public void paint(Graphics g) {
		refreshLayout();
		super.paint(g);
	}
}
