package lunartools.audiocutter.worker;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterController;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.ffmpeg.CreateWavFromMediaService;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;

public class CreateTempWavFileWorker extends SwingWorker<Void, Void> {
	private static Logger logger = LoggerFactory.getLogger(CreateTempWavFileWorker.class);
	private AudioCutterModel model;
	private AudioCutterController controller;

	public CreateTempWavFileWorker(AudioCutterModel model,AudioCutterController controller) {
		this.model=model;
		this.controller=controller;
	}

	@Override
	public Void doInBackground() {
		logger.debug("creating temp wav file...");
		model.setStatusMessage(null);
		StatusMessage statusMessage=new StatusMessage(StatusMessage.Type.INFO,"ready");
		controller.setBusy(true);
		try {
			setProgress(0);
			File mediafile=model.getMediaFile();
			File wavfile=File.createTempFile(AudioCutterModel.PROGRAMNAME,".wav");
			logger.debug("creating temporary wav file: "+wavfile);
			wavfile.deleteOnExit();

			new CreateWavFromMediaService().createWavFileFromMediaFile(model,this,mediafile,wavfile);

			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavfile);
			byte[] wavBytes = new byte[audioInputStream.available()];
			audioInputStream.read(wavBytes, 0, wavBytes.length);
			model.setAudiodata(wavBytes);

		} catch (Exception e) {
			logger.error("error while creating temporary wav file",e);
			statusMessage=new StatusMessage(StatusMessage.Type.ERROR,e.getMessage());
		}finally {
			controller.setBusy(false);
			if(model.getStatusMessage()==null) {
				model.setStatusMessage(statusMessage);
			}
		}
		return null;
	}

	public void progressBarCallback(int progress) {
		setProgress(progress);
	}

	@Override
	public void done() {
		logger.debug("temporary wav file was created");
	}

}
