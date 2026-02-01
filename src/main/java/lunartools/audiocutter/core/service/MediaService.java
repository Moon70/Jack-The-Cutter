package lunartools.audiocutter.core.service;

import java.io.File;
import java.util.Objects;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.exec.CreateWavFromMediaExecutor;
import lunartools.audiocutter.infrastructure.config.AudioCutterSettings;
import lunartools.swing.ProgressStepListener;

public class MediaService {
	private static Logger logger = LoggerFactory.getLogger(MediaService.class);
	private final AudioCutterModel audioCutterModel;

	public MediaService(AudioCutterModel audioCutterModel) {
		this.audioCutterModel=Objects.requireNonNull(audioCutterModel);
	}

	public void closeProject() {
		audioCutterModel.closeProject();
	}

	public void openMedia(File mediaFile,ProgressStepListener progressStepListener) {
		try {
			File wavfile=File.createTempFile(AudioCutterModel.PROGRAM_NAME,".wav");
			logger.debug("creating temporary wav file: "+wavfile);
			wavfile.deleteOnExit();

			new CreateWavFromMediaExecutor().createWavFileFromMediaFile(audioCutterModel,mediaFile,wavfile,progressStepListener);

			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavfile);
			byte[] wavBytes = new byte[audioInputStream.available()];
			audioInputStream.read(wavBytes, 0, wavBytes.length);
			audioCutterModel.setAudiodata(wavBytes);
		} catch (Exception e) {
			logger.error("error while creating temporary wav file",e);
			throw new MediaException(e);
		}
		audioCutterModel.setMediaFile(mediaFile);
	}

	public boolean isProjectDirty() {
		return audioCutterModel.isProjectDirty();
	}

	public File getCurrentProjectDirectory() {
		File file=audioCutterModel.getMediaFile();
		if(file!=null) {
			return file.getParentFile();
		}
		if(AudioCutterSettings.getInstance().containsKey(AudioCutterSettings.AUDIOFILE_PATH)){
			file=new File(AudioCutterSettings.getInstance().getString(AudioCutterSettings.AUDIOFILE_PATH));
		}
		if(file!=null) {
			return file.getParentFile();
		}
		file=audioCutterModel.getProjectFile();
		if(file!=null) {
			return file.getParentFile();
		}
		return null;
	}

	public boolean hasAudioSections() {
		return audioCutterModel.hasAudioSections();
	}

}
