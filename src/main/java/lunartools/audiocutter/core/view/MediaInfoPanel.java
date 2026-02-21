package lunartools.audiocutter.core.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JPanel;

import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.core.AudioCutterModel;

public class MediaInfoPanel extends JPanel{
	private final AudioCutterModel audioCutterModel;
	private final JLabel labelFile;

	public MediaInfoPanel(AudioCutterModel audioCutterModel) {
		this.audioCutterModel=Objects.requireNonNull(audioCutterModel);
		setLayout(new GridBagLayout());

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;

		gridBagConstraints.gridx = 0;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		JLabel label=new JLabel("File:");
		add(label, gridBagConstraints);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		labelFile=new JLabel();
		add(labelFile, gridBagConstraints);

		audioCutterModel.addChangeListener(this::updateModelChanges);

		//setBackground(new Color(0xff99ff));
	}

	public void updateModelChanges(Object object) {
		if(object==SimpleEvents.MODEL_MEDIAFILECHANGED) {
			File mediafile=audioCutterModel.getMediaFile();
			labelFile.setText(mediafile==null?"":mediafile.getName());
		}
	}

}
