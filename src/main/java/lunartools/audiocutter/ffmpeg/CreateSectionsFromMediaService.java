package lunartools.audiocutter.ffmpeg;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.Settings;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.AudioCutterSettings;
import lunartools.exec.Exec;
import lunartools.exec.ExecOutputCallback;

public class CreateSectionsFromMediaService implements ExecOutputCallback{
	private static Logger logger = LoggerFactory.getLogger(CreateSectionsFromMediaService.class);
	private Pattern patternOutputFormatError;
	private String error;
	private volatile Throwable receivedThrowableFromExec;

	public void cutMediaFile(AudioCutterModel model,File mediafile,File sectionfile,String startposAsString,String endposAsString) {
		String ffmpegExecutable=model.getFFmpegExecutablePath();
		logger.debug("media file: "+mediafile);
		logger.debug("FFmpeg executable: "+ffmpegExecutable);
		String mediaFilePathWithQuotes="\""+mediafile.getAbsolutePath()+"\"";
		String sectionFilepathWithQuotes="\""+sectionfile.getAbsolutePath()+"\"";
		Settings settings=AudioCutterSettings.getSettings();
		String parameter=settings.getStringNotNull(AudioCutterSettings.FFMPEG_CREATESECTIONS_PARAMETER);
		parameter=String.format(parameter,mediaFilePathWithQuotes,startposAsString,endposAsString,sectionFilepathWithQuotes);
		logger.debug("FFmpeg parameter: "+parameter);

		String pattern=settings.getStringNotNull(AudioCutterSettings.FFMPEG_CREATESECTIONS_PATTERN_OUTPUTFORMATERROR);
		patternOutputFormatError=Pattern.compile(pattern);

		try {
			Exec exec=new Exec(ffmpegExecutable,parameter,this);
			int createSectionsTimeout=settings.getInt(AudioCutterSettings.FFMPEG_CREATESECTIONS_TIMEOUT);
			exec.start();
			exec.join(createSectionsTimeout*1000);
			if(exec.isAlive()) {
				throw new RuntimeException("there´s something wrong, timeout while talking to FFmpeg");
			}
			if(exec.isError()) {
				Exception exception=exec.getException();
				throw new RuntimeException("Error while creating sections: "+exception.getMessage(),exception);
			}
			if(receivedThrowableFromExec!=null) {
				throw new RuntimeException("Error while creating sections: "+receivedThrowableFromExec.getMessage(),receivedThrowableFromExec);
			}
		} catch (Exception e) {
			logger.warn("received InterruptedException");
		}
	}

	@Override
	public void execReceivedOutputLine(String line) {
		processOutputFromFFmpeg(line);
	}

	@Override
	public void execReceivedErrorLine(String line) {
		processOutputFromFFmpeg(line);
	}

	@Override
	public void execReceivedThrowable(Throwable throwable) {
		logger.error("Received throwable from Exec",throwable);
		receivedThrowableFromExec=throwable;
	}

	private void processOutputFromFFmpeg(String line) {
		if(line.length()>0) {
			Matcher matcher=patternOutputFormatError.matcher(line);
			if(matcher.matches()) {
				error="FFmpeg: "+matcher.group(1);
			}
		}
	}

	public String getError() {
		return error;
	}

}
