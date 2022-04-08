package lunartools.audiocutter.gui.mediainfopanel;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterController;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.SimpleEvents;

public class MediaInfoPanel extends JPanel implements Observer{
	private static Logger logger = LoggerFactory.getLogger(MediaInfoPanel.class);
	private AudioCutterModel model;
	private JLabel labelFile;

	public MediaInfoPanel(AudioCutterModel audioCutterModel,AudioCutterController audioCutterController) {
		this.model=audioCutterModel;
		this.setLayout(null);

		int margin=4;
		int labelX=margin;
		int labelWidth=35;
		int fieldX=labelX+labelWidth+margin;
		//TODO: calculate max fieldWidth
		int fieldWidth=400;
		int y=margin;
		int lineHeight=18;

		JLabel label=new JLabel("File:");
		label.setBounds(labelX,y,labelWidth,lineHeight);
		add(label);

		labelFile=new JLabel();
		labelFile.setBounds(fieldX,y,fieldWidth,lineHeight);
		add(labelFile);

		y+=lineHeight;

		model.addObserver(this);

		//setBackground(new Color(0xff99ff));
	}

	@Override
	public void update(Observable observable, Object object) {
		if(object==SimpleEvents.MODEL_MEDIAFILECHANGED) {
			File mediafile=model.getMediaFile();
			labelFile.setText(mediafile==null?"":mediafile.getName());
		}
	}

}
