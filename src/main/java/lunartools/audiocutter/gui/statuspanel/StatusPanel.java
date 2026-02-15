package lunartools.audiocutter.gui.statuspanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
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
//		setLayout(null);
		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(0, 50));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		setAlignmentX(Component.LEFT_ALIGNMENT);

		Font fontBold=getFont().deriveFont(Font.BOLD);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		setAlignmentX(Component.LEFT_ALIGNMENT);


		/* Row 0: labels 1–6 (three pairs) */
		JLabel label=new JLabel("Cursor:");
		label.setFont(fontBold);
		gbc.gridx = 0;
		add(label, gbc);
		
		labelCursorTimeIndex=new JLabel();
		gbc.gridx = 1;
		add(wrapFixedWidth(labelCursorTimeIndex, 50), gbc);
		labelCursorTimeIndex.setText("xx:yy");

		
		label=new JLabel("Mouse:");
		label.setFont(fontBold);
		gbc.gridx = 2;
		add(label, gbc);
		
		labelMouseTimeIndex=new JLabel();
		gbc.gridx = 3;
		add(wrapFixedWidth(labelMouseTimeIndex, 50), gbc);

		
		label=new JLabel("Distance:");
		label.setFont(fontBold);
		label.setToolTipText("distance to lefthand cutpoint");
		gbc.gridx = 4;
		add(label, gbc);

		labelDistanceTime=new JLabel();
		gbc.gridx = 5;
		add(wrapFixedWidth(labelDistanceTime, 50), gbc);

		// Optional spacer column G (can be left empty)
		gbc.gridx = 6;
		add(Box.createHorizontalGlue(), gbc);
		
		
		/* Row 1: labels 7–8 */
		label=new JLabel("Status:");
		gbc.gridy = 1;
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		add(label, gbc);
		
		labelStatus=new JLabel();
		gbc.gridx = 1;
		gbc.gridwidth=6;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(labelStatus, gbc);

		
		/* Row 2: labels 9–10 */
		labelFFmpegVersionLabel=new JLabel("Using:");
		labelFFmpegVersionLabel.setForeground(Color.DARK_GRAY);
		labelFFmpegVersionLabel.setVisible(false);
		gbc.gridy = 2;
		gbc.gridwidth=1;
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		add(labelFFmpegVersionLabel, gbc);

		labelFFmpegVersion=new JLabel();
		labelFFmpegVersion.setForeground(Color.DARK_GRAY);
		gbc.gridx = 1;
		gbc.gridwidth=6;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(labelFFmpegVersion, gbc);

		
		model.addChangeListener(this::updateModelChanges);
		//setBackground(new Color(0xffcccc));
	}

	private JPanel wrapFixedWidth(JLabel label, int width) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(label, BorderLayout.CENTER);
//        Dimension fixed = new Dimension(width, label.getPreferredSize().height);
        FontMetrics fm = label.getFontMetrics(label.getFont());
        int height = fm.getHeight();
        Dimension fixed = new Dimension(width, height);
        wrapper.setPreferredSize(fixed);
        wrapper.setMinimumSize(fixed);
        wrapper.setMaximumSize(fixed);
        return wrapper;
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
