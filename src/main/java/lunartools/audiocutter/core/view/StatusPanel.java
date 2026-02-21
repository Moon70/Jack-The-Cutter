package lunartools.audiocutter.core.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Objects;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lunartools.audiocutter.common.model.AudioSectionModel;
import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.common.ui.util.SampleUtils;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.model.StatusMessage;

public class StatusPanel extends JPanel{
	private final AudioCutterModel audioCutterModel;
	private final Dimension STATUSPANEL_DIMENSION=new Dimension(Integer.MAX_VALUE, 50);

	JLabel labelStatus;
	JLabel labelFFmpegVersionLabel;
	JLabel labelFFmpegVersion;
	private JLabel labelCursorTimeIndex;
	private JLabel labelMouseTimeIndex;
	private JLabel labelDistanceTime;

	public StatusPanel(AudioCutterModel audioCutterModel) {
		this.audioCutterModel=Objects.requireNonNull(audioCutterModel);
		setLayout(new GridBagLayout());
		//setPreferredSize(new Dimension(0, 50));
		setMaximumSize(STATUSPANEL_DIMENSION);
		//setAlignmentX(Component.LEFT_ALIGNMENT);

		Font fontBold=getFont().deriveFont(Font.BOLD);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;

		JLabel label=new JLabel("Cursor:");
		label.setFont(fontBold);
		gbc.gridx = 0;
		add(label, gbc);

		labelCursorTimeIndex=new JLabel();
		gbc.gridx = 1;
		add(wrapFixedWidth(labelCursorTimeIndex, 50), gbc);


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


		audioCutterModel.addChangeListener(this::updateModelChanges);
		//setBackground(new Color(0xffcccc));
	}

	private JPanel wrapFixedWidth(JLabel label, int width) {
		JPanel jPanelWrapper = new JPanel(new BorderLayout());
		jPanelWrapper.add(label, BorderLayout.CENTER);
		FontMetrics fontMetrics = label.getFontMetrics(label.getFont());
		int fontHeight = fontMetrics.getHeight();
		Dimension dimensionFixed = new Dimension(width, fontHeight);
		jPanelWrapper.setPreferredSize(dimensionFixed);
		jPanelWrapper.setMinimumSize(dimensionFixed);
		jPanelWrapper.setMaximumSize(dimensionFixed);
		return jPanelWrapper;
	}

	public void updateModelChanges(Object object) {
		if(object==SimpleEvents.MODEL_CURSORCHANGED) {
			int cursorPositionSampleNumber=audioCutterModel.getCursorPositionSampleNumber();
			labelCursorTimeIndex.setText(SampleUtils.convertNumberOfSamplesToHourMinuteSecondString(cursorPositionSampleNumber));
		}else if(object==SimpleEvents.MODEL_MOUSESAMPLENUMBERCHANGED) {
			int mousePositionSampleNumber=audioCutterModel.getMousePositionSampleNumber();
			if(mousePositionSampleNumber==0) {
				labelMouseTimeIndex.setText("");
				labelDistanceTime.setText("");
				return;
			}
			labelMouseTimeIndex.setText(SampleUtils.convertNumberOfSamplesToHourMinuteSecondString(mousePositionSampleNumber));
			ArrayList<AudioSectionModel> audioSections=audioCutterModel.getAudioSections();
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
		}else if(object instanceof StatusMessage) {
			processStatusMessageEvent((StatusMessage)object);
		}
	}

	private void processStatusMessageEvent(StatusMessage statusMessage) {
		if(statusMessage.getType()==StatusMessage.Type.INFO) {
			labelStatus.setText(statusMessage.getMessage());
			labelStatus.setToolTipText(statusMessage.getMessage());
			labelStatus.setForeground(Color.BLUE);
		}else if(statusMessage.getType()==StatusMessage.Type.WARNING) {
			labelStatus.setText(statusMessage.getMessage());
			labelStatus.setToolTipText(statusMessage.getMessage());
			labelStatus.setForeground(Color.MAGENTA);
		}else if(statusMessage.getType()==StatusMessage.Type.ERROR) {
			labelStatus.setText(statusMessage.getMessage());
			labelStatus.setToolTipText(statusMessage.getMessage());
			labelStatus.setForeground(Color.RED);
		}else if(statusMessage.getType()==StatusMessage.Type.FFMPEGVERSION) {
			String message=statusMessage.getMessage();
			labelFFmpegVersionLabel.setVisible(message!=null);
			labelFFmpegVersion.setText(message);
		}
	}

}
