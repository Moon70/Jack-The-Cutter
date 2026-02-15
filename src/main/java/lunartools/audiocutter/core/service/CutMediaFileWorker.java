package lunartools.audiocutter.core.service;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;
import lunartools.swing.ProgressProperty;
import lunartools.swing.ProgressStep;

public class CutMediaFileWorker extends SwingWorker<Void, Void> {
	private static Logger logger = LoggerFactory.getLogger(CutMediaFileWorker.class);
	private final AudioCutterModel audioCutterModel;
	private final CutMediaFileService cutMediaFileService;

	public CutMediaFileWorker(AudioCutterModel audioCutterModel,CutMediaFileService cutMediaFileService) {
		this.audioCutterModel=Objects.requireNonNull(audioCutterModel);
		this.cutMediaFileService=Objects.requireNonNull(cutMediaFileService);
	}

	@Override
	public Void doInBackground() {
		logger.debug("cutting media file...");
		//controller.setBusy(true);
		cutMediaFileService.cutMediaFile((step, total, message) -> {
			if(isCancelled()) {
				return;
			}
			ProgressStep progressStep = new ProgressStep(step, total, message);
			firePropertyChange(ProgressProperty.PROGRESS_STEP, null, progressStep);
		});
		return null;
	}

	@Override
	public void done() {
		try {
			get(); //throws ExecutionException if doInBackground failed
			audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.INFO,"Ready"));
			logger.debug("media file was cut");
		} catch (ExecutionException e) {
			//JOptionPane.showMessageDialog(parent, ex.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.ERROR,e.getMessage()));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.INFO,"interrupted"));
		}
	}

}
