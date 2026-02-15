package lunartools.audiocutter.gui.mediainfopanel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.core.AudioCutterController;
import lunartools.audiocutter.core.AudioCutterModel;

public class MediaInfoPanel extends JPanel{
	private static Logger logger = LoggerFactory.getLogger(MediaInfoPanel.class);
	private AudioCutterModel model;
	private JLabel labelFile;

	public MediaInfoPanel(AudioCutterModel audioCutterModel) {
		this.model=audioCutterModel;
		//setLayout(null);
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;

		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		JLabel label=new JLabel("File:");
		add(label, gbc);

		/* Label 2: stretches to fill remaining width */
		gbc.gridx = 1;
		gbc.weightx = 1;          // receives extra horizontal space
		gbc.fill = GridBagConstraints.HORIZONTAL;
		labelFile=new JLabel();
		add(labelFile, gbc);

		model.addChangeListener(this::updateModelChanges);

		//setBackground(new Color(0xff99ff));
	}

	public void updateModelChanges(Object object) {
		if(object==SimpleEvents.MODEL_MEDIAFILECHANGED) {
			File mediafile=model.getMediaFile();
			labelFile.setText(mediafile==null?"":mediafile.getName());
		}
	}

}
