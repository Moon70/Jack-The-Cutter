package lunartools.audiocutter.gui.statuspanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterModel;

public class StatusPanel extends JPanel implements Observer{
	private static Logger logger = LoggerFactory.getLogger(StatusPanel.class);
	private AudioCutterModel model;

	JLabel labelStatus;
	JLabel labelFFmpegVersionLabel;
	JLabel labelFFmpegVersion;

	public StatusPanel(AudioCutterModel model) {
		this.model=model;
		this.setLayout(null);

		int margin=4;
		int parentWidth=model.getAudiodataViewWidth();
		int statusLabelX=margin;
		int statusLabelWidth=50;
		int statusInfoX=statusLabelX+statusLabelWidth+margin;
		int statusInfoWidth=parentWidth-statusInfoX-margin;
		int statusLineHeight=20;
		int y=0;

		JLabel label=new JLabel("Status:");
		label.setBounds(statusLabelX,y,statusLabelWidth,statusLineHeight);
		add(label);

		labelStatus=new JLabel();
		labelStatus.setBounds(statusInfoX,y,statusInfoWidth,statusLineHeight);
		add(labelStatus);

		y+=statusLineHeight;

		labelFFmpegVersionLabel=new JLabel("Using:");
		labelFFmpegVersionLabel.setBounds(statusLabelX,y,statusLabelWidth,statusLineHeight);
		labelFFmpegVersionLabel.setForeground(Color.DARK_GRAY);
		labelFFmpegVersionLabel.setVisible(false);
		add(labelFFmpegVersionLabel);

		labelFFmpegVersion=new JLabel();
		labelFFmpegVersion.setBounds(statusInfoX,y,statusInfoWidth,statusLineHeight);
		labelFFmpegVersion.setForeground(Color.DARK_GRAY);
		add(labelFFmpegVersion);

		y+=statusLineHeight;

		setBounds(0, 0, parentWidth, y);
		setMinimumSize(new Dimension(parentWidth,y));
		setMaximumSize(new Dimension(parentWidth,y));
		setPreferredSize(new Dimension(parentWidth,y));

		//setBackground(new Color(0xffcccc));
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		setSize(model.getAudiodataViewWidth(),getHeight());
	}

}
