package lunartools.audiocutter.worker;

import java.io.File;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterController;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.ffmpeg.DetermineFFmpegVersionService;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;

public class DetermineFFmpegVersionWorker extends SwingWorker<Void, Void> {
	private static Logger logger = LoggerFactory.getLogger(DetermineFFmpegVersionWorker.class);
	private AudioCutterModel model;
	private AudioCutterController controller;

	public DetermineFFmpegVersionWorker(AudioCutterModel model,AudioCutterController controller) {
		this.model=model;
		this.controller=controller;
	}

	@Override
	public Void doInBackground() {
		logger.debug("determining FFmpeg version...");
		StatusMessage statusMessage=new StatusMessage(StatusMessage.Type.INFO,"ready");
		controller.setBusy(true);
		try {
			String ffmpegExecutablePath=model.getFFmpegExecutablePath();
			if(ffmpegExecutablePath==null || ffmpegExecutablePath.length()==0) {
				statusMessage=new StatusMessage(StatusMessage.Type.ERROR,AudioCutterModel.PROGRAMNAME+" needs FFmpeg to process media files, please open preferences and specify 'FFmpeg executable'");
				return null;
			}
			File ffmpegExecutable=new File(ffmpegExecutablePath);
			if(!ffmpegExecutable.exists()) {
				statusMessage=new StatusMessage(StatusMessage.Type.ERROR,"FFmpeg not found: "+ffmpegExecutable.getAbsolutePath());
				return null;
			}

			new DetermineFFmpegVersionService().determineFFmpegVersion(model,this);

		} catch (Exception e) {
			logger.error("error while determining FFmpeg version",e);
			statusMessage=new StatusMessage(StatusMessage.Type.ERROR,e.getMessage());
		}finally {
			controller.setBusy(false);
			model.setStatusMessage(statusMessage);
		}
		return null;
	}

}
