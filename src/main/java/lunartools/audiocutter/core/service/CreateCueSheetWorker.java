package lunartools.audiocutter.core.service;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.model.StatusMessage;
import lunartools.swing.ProgressProperty;
import lunartools.swing.ProgressStep;

public class CreateCueSheetWorker extends SwingWorker<Void, Void> {
	private static Logger logger = LoggerFactory.getLogger(CreateCueSheetWorker.class);
	private final AudioCutterModel audioCutterModel;
	private final CreateCueSheetService createCueSheetService;
	private final File fileCue;
	private final File fileWav;

	public CreateCueSheetWorker(AudioCutterModel audioCutterModel,CreateCueSheetService createCueSheetService,File fileCue,File fileWav) {
		this.audioCutterModel=Objects.requireNonNull(audioCutterModel);
		this.createCueSheetService=Objects.requireNonNull(createCueSheetService);
		this.fileCue=Objects.requireNonNull(fileCue);
		this.fileWav=Objects.requireNonNull(fileWav);
	}

	@Override
	public Void doInBackground() {
		logger.debug("create CUE sheet and WAV file...");
		//audioCutterModel.setStatusMessage(null);
		//controller.setBusy(true);
		createCueSheetService.createCueSheet(fileCue,fileWav,(step, total, message) -> {
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
			logger.debug("created CUE sheet");
		} catch (ExecutionException e) {
			//JOptionPane.showMessageDialog(parent, ex.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.ERROR,e.getMessage()));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.INFO,"interrupted"));
		}
	}

}
