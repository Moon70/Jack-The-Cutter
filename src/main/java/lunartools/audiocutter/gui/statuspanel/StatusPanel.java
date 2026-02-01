package lunartools.audiocutter.gui.statuspanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.common.model.AudioSectionModel;
import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.common.ui.util.SampleUtils;
import lunartools.audiocutter.core.AudioCutterModel;

public class StatusPanel extends JPanel{
	private static Logger logger = LoggerFactory.getLogger(StatusPanel.class);
	private AudioCutterModel model;

	JLabel labelStatus;
	JLabel labelFFmpegVersionLabel;
	JLabel labelFFmpegVersion;
	JLabel labelCursorTimeIndex;
	JLabel labelMouseTimeIndex;
	JLabel labelDistanceTime;

	public StatusPanel(AudioCutterModel model) {
		this.model=model;
		this.setLayout(null);

		Font fontBold=getFont().deriveFont(Font.BOLD);

		int margin=4;
		int parentWidth=model.getAudiodataViewWidth();
		int statusLabelX=margin;
		int statusLabelWidth=60;
		int statusInfoX=statusLabelX+statusLabelWidth+margin;
		int statusInfoWidth=parentWidth-statusInfoX-margin;
		int statusLineHeight=20;
		int y=0;

		int offsetX=0;
		JLabel label=new JLabel("Cursor:");
		label.setFont(fontBold);
		label.setBounds(statusLabelX+offsetX,y,statusLabelWidth,statusLineHeight);
		add(label);

		labelCursorTimeIndex=new JLabel();
		labelCursorTimeIndex.setBounds(statusInfoX+offsetX,y,statusInfoWidth,statusLineHeight);
		add(labelCursorTimeIndex);

		offsetX+=150;
		label=new JLabel("Mouse:");
		label.setFont(fontBold);
		label.setBounds(statusLabelX+offsetX,y,statusLabelWidth,statusLineHeight);
		add(label);

		labelMouseTimeIndex=new JLabel();
		labelMouseTimeIndex.setBounds(statusInfoX+offsetX,y,statusInfoWidth,statusLineHeight);
		add(labelMouseTimeIndex);

		offsetX+=150;
		label=new JLabel("Distance:");
		label.setFont(fontBold);
		label.setBounds(statusLabelX+offsetX,y,statusLabelWidth,statusLineHeight);
		label.setToolTipText("distance to lefthand cutpoint");
		add(label);

		labelDistanceTime=new JLabel();
		labelDistanceTime.setBounds(statusInfoX+offsetX,y,statusInfoWidth,statusLineHeight);
		add(labelDistanceTime);


		y+=statusLineHeight;

		label=new JLabel("Status:");
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

		model.addChangeListener(this::updateModelChanges);
		//setBackground(new Color(0xffcccc));
	}

	public void updateModelChanges(Object object) {
		if(object==SimpleEvents.MODEL_CURSORCHANGED) {
			int cursorPositionSampleNumber=model.getCursorPositionSampleNumber();
			labelCursorTimeIndex.setText(SampleUtils.convertNumberOfSamplesToHourMinuteSecondString(cursorPositionSampleNumber));
		}else if(object==SimpleEvents.MODEL_MOUSESAMPLENUMBERCHANGED) {
			int mousePositionSampleNumber=model.getMousePositionSampleNumber();
			if(mousePositionSampleNumber==0) {
				labelMouseTimeIndex.setText("");
				labelDistanceTime.setText("");
				return;
			}
			labelMouseTimeIndex.setText(SampleUtils.convertNumberOfSamplesToHourMinuteSecondString(mousePositionSampleNumber));
			ArrayList<AudioSectionModel> audioSections=model.getAudioSections();
			labelDistanceTime.setText("");
			for(int i=audioSections.size()-1;i>=0;i--) {
				AudioSectionModel audioSection=audioSections.get(i);
				int sectionStart=audioSection.getPosition();
				if(sectionStart<mousePositionSampleNumber) {
					int delta=mousePositionSampleNumber-sectionStart;
					labelDistanceTime.setText(SampleUtils.convertNumberOfSamplesToHourMinuteSecondString(delta));
					break;
				}
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		setSize(model.getAudiodataViewWidth(),getHeight());
	}

}
