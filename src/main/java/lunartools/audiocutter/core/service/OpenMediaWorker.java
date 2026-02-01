package lunartools.audiocutter.core.service;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;
import lunartools.swing.ProgressProperty;
import lunartools.swing.ProgressStep;

public class OpenMediaWorker extends SwingWorker<Void, Void> {
	private static Logger logger = LoggerFactory.getLogger(OpenMediaWorker.class);
	private final AudioCutterModel audioCutterModel;
	private final MediaService mediaService;
	private final File mediaFile;

	public OpenMediaWorker(AudioCutterModel audioCutterModel, MediaService mediaService, File mediaFile) {
		this.audioCutterModel=Objects.requireNonNull(audioCutterModel);
		this.mediaService = Objects.requireNonNull(mediaService);
		this.mediaFile=Objects.requireNonNull(mediaFile);
	}

	@Override
	protected Void doInBackground() {
		logger.debug("creating temporary wav file...");
		//controller.setBusy(true);
		mediaService.openMedia(mediaFile,(step, total, message) -> {
			if(isCancelled()) {
				return;
			}
			ProgressStep progressStep = new ProgressStep(step, total, message);
			firePropertyChange(ProgressProperty.PROGRESS_STEP, null, progressStep);
		});
		return null;
	}

	@Override
	protected void done() {
		try {
			get(); //throws ExecutionException if doInBackground failed
			audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.INFO,"Ready"));
		} catch (ExecutionException ex) {
			//JOptionPane.showMessageDialog(parent, ex.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.ERROR,ex.getMessage()));
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.INFO,"interrupted"));
		}
	}
	
}
