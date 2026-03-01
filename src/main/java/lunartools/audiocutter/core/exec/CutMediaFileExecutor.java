package lunartools.audiocutter.core.exec;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.infrastructure.config.AudioCutterSettings;
import lunartools.cli.Exec;
import lunartools.cli.ExecHelper;
import lunartools.cli.ExecOutputCallback;

public class CutMediaFileExecutor implements ExecOutputCallback{
	private static Logger logger = LoggerFactory.getLogger(CutMediaFileExecutor.class);
	private Pattern patternOutputFormatError;
	private String error;
	private volatile Throwable throwableFromExec;

	public void cutMediaFile(
			AudioCutterModel audioCutterModel,
			File mediafile,
			File sectionfile,
			String startposAsString,
			String endposAsString
			) {
		logger.debug("media file: "+mediafile);
		String ffmpegExecutable=audioCutterModel.getFFmpegExecutablePath();
		logger.debug("FFmpeg executable: "+ffmpegExecutable);
		AudioCutterSettings settings=AudioCutterSettings.getInstance();
		String ffmpegParameter=settings.getStringNotNull(AudioCutterSettings.FFMPEG_CREATESECTIONS_PARAMETER);

		String pattern=settings.getStringNotNull(AudioCutterSettings.FFMPEG_CREATESECTIONS_PATTERN_OUTPUTFORMATERROR);
		patternOutputFormatError=Pattern.compile(pattern);

		String[] commandArray=ExecHelper.createCmdArray(ffmpegExecutable,ffmpegParameter, mediafile.getAbsolutePath(),startposAsString,endposAsString,sectionfile.getAbsolutePath());
		Exec exec=new Exec(commandArray,this);
		int createSectionsTimeout=settings.getInt(AudioCutterSettings.FFMPEG_CREATESECTIONS_TIMEOUT);
		exec.start();
		try {
			exec.join(createSectionsTimeout*1000);
		} catch (InterruptedException e) {
			logger.error("Executor interrupted");
			Thread.currentThread().interrupt();
			throw new CutMediaFileException("Executor interrupted",e);
		}
		if(exec.isAlive()) {
			throw new CutMediaFileException("Timeout waiting for FFmpeg");
		}
		if(exec.isError()) {
			Exception exception=exec.getException();
			throw new CutMediaFileException("Error while creating sections: "+exception.getMessage(),exception);
		}
		if(throwableFromExec!=null) {
			throw new CutMediaFileException("Error while creating sections: "+throwableFromExec.getMessage());
		}
		if(this.error!=null) {
			throw new CutMediaFileException(this.error);
		}
	}

	@Override
	public void execReceivedOutputLine(String line) {
		processFFmpegOutput(line);
	}

	@Override
	public void execReceivedErrorLine(String line) {
		processFFmpegOutput(line);
	}

	@Override
	public void execReceivedThrowable(Throwable throwable) {
		logger.error("Received throwable from Exec",throwable);
		throwableFromExec=throwable;
	}

	private void processFFmpegOutput(String line) {
		if(line.length()>0) {
			Matcher matcher=patternOutputFormatError.matcher(line);
			if(matcher.matches()) {
				error="FFmpeg: "+matcher.group(1);
			}
		}
	}

}
