package lunartools.audiocutter.worker;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterController;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;
import lunartools.audiocutter.service.autocut.AutoCutService;

public class AutoCutWorker extends SwingWorker<Void, Void> implements ProgressCallback{
	private static Logger logger = LoggerFactory.getLogger(AutoCutWorker.class);
	private AudioCutterModel model;
	private AudioCutterController controller;

	public AutoCutWorker(AudioCutterModel model,AudioCutterController controller) {
		this.model=model;
		this.controller=controller;
	}

	@Override
	public Void doInBackground() {
		logger.debug("autocutting...");
		model.setStatusMessage(null);
		StatusMessage statusMessage=new StatusMessage(StatusMessage.Type.INFO,"ready");
		controller.setBusy(true);
		try {
			new AutoCutService(model,controller,this).autocut();
		} catch (Exception e) {
			logger.error("error while autocutting",e);
			statusMessage=new StatusMessage(StatusMessage.Type.ERROR,e.getMessage());
		}finally {
			controller.setBusy(false);
			model.setStatusMessage(statusMessage);
		}
		return null;
	}

	@Override
	public void setProgressCallback(int progress) {
		setProgress(progress);
	}

}
