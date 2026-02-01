package lunartools.audiocutter.gui.statuspanel;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import lunartools.audiocutter.core.AudioCutterModel;

public class StatusController{
	private StatusPanel statusPanel;

	public StatusController(AudioCutterModel model) {
		statusPanel=new StatusPanel(model);
		model.addChangeListener(this::updateModelChanges);
	}

	public StatusPanel getStatusPanel() {
		return statusPanel;
	}

	public void updateModelChanges(Object object) {
		if(object instanceof StatusMessage) {
			StatusMessage statusMessage=(StatusMessage)object;
			if(statusMessage.getType()==StatusMessage.Type.INFO) {
				statusPanel.labelStatus.setText(statusMessage.getMessage());
				statusPanel.labelStatus.setToolTipText(statusMessage.getMessage());
				statusPanel.labelStatus.setForeground(Color.BLUE);
			}else if(statusMessage.getType()==StatusMessage.Type.WARNING) {
				statusPanel.labelStatus.setText(statusMessage.getMessage());
				statusPanel.labelStatus.setToolTipText(statusMessage.getMessage());
				statusPanel.labelStatus.setForeground(Color.MAGENTA);
			}else if(statusMessage.getType()==StatusMessage.Type.ERROR) {
				statusPanel.labelStatus.setText(statusMessage.getMessage());
				statusPanel.labelStatus.setToolTipText(statusMessage.getMessage());
				statusPanel.labelStatus.setForeground(Color.RED);
			}else if(statusMessage.getType()==StatusMessage.Type.FFMPEGVERSION) {
				String message=statusMessage.getMessage();
				statusPanel.labelFFmpegVersionLabel.setVisible(message!=null);
				statusPanel.labelFFmpegVersion.setText(message);
			}
		}
	}

}
