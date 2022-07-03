package lunartools.audiocutter.ffmpeg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.AudioCutterSettings;
import lunartools.audiocutter.worker.DetermineFFmpegVersionWorker;
import lunartools.exec.Exec;
import lunartools.exec.ExecOutputCallback;

public class DetermineFFmpegVersionService implements ExecOutputCallback{
	private static Logger logger = LoggerFactory.getLogger(DetermineFFmpegVersionService.class);
	private AudioCutterModel model;
	private Pattern patternVersion;
	private String version;
	private volatile Throwable receivedThrowableFromExec;

	public void determineFFmpegVersion(AudioCutterModel model,DetermineFFmpegVersionWorker worker) {
		this.model=model;
		String ffmpegExecutable=model.getFFmpegExecutablePath();
		logger.debug("FFmpeg executable: "+ffmpegExecutable);
		String parameter=AudioCutterSettings.getSettings().getStringNotNull(AudioCutterSettings.FFMPEG_DETERMINEVERSION_PARAMETER);
		logger.debug("FFmpeg parameter: "+parameter);

		String pattern=AudioCutterSettings.getSettings().getStringNotNull(AudioCutterSettings.FFMPEG_DETERMINEVERSION_PATTERN);
		patternVersion=Pattern.compile(pattern);

		try {
			model.setFFmpegVersion(null);
			Exec exec=new Exec(ffmpegExecutable,parameter,null,this);
			int determineVersionTimeout=AudioCutterSettings.getSettings().getInt(AudioCutterSettings.FFMPEG_DETERMINEVERSION_TIMEOUT);
			exec.start();
			exec.join(determineVersionTimeout*1000);
			if(exec.isAlive()) {
				throw new RuntimeException("there´s something wrong, timeout while talking to FFmpeg");
			}
			if(exec.isError()) {
				Exception exception=exec.getException();
				throw new RuntimeException("Error while determining FFmpeg version: "+exception.getMessage(),exception);
			}
			if(!model.isFFmpegAvailable()) {
				throw new RuntimeException("Could not determine FFmpeg version");
			}
			if(receivedThrowableFromExec!=null) {
				throw new RuntimeException("Error while determining FFmpeg version: "+receivedThrowableFromExec.getMessage(),receivedThrowableFromExec);
			}
		} catch (InterruptedException e) {
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
			Matcher matcher;
			matcher=patternVersion.matcher(line);
			if(matcher.matches()) {
				this.version=matcher.group(1);
				model.setFFmpegVersion(version);
			}
		}
	}

}
