package lunartools.audiocutter.ffmpeg;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.AudioCutterSettings;
import lunartools.audiocutter.Calculator;
import lunartools.audiocutter.worker.CreateTempWavFileWorker;
import lunartools.exec.Exec;
import lunartools.exec.ExecOutputCallback;

public class CreateWavFromMediaService implements ExecOutputCallback{
	private static Logger logger = LoggerFactory.getLogger(CreateWavFromMediaService.class);
	private Pattern patternDuration;
	private Pattern patternProgress;
	private int mediaDurationInSeconds;
	private CreateTempWavFileWorker worker;
	private volatile Throwable receivedThrowableFromExec;

	public void createWavFileFromMediaFile(AudioCutterModel model,CreateTempWavFileWorker worker,File mediafile,File wavfile) {
		this.worker=worker;
		logger.debug("media file: "+mediafile);
		logger.debug("wav file: "+wavfile);
		String ffmpegExecutable=model.getFFmpegExecutablePath();
		logger.debug("FFmpeg executable: "+ffmpegExecutable);
		String mediaFilePathWithQuotes="\""+mediafile.getAbsolutePath()+"\"";
		String wavFilePathWithQuotes="\""+wavfile.getAbsolutePath()+"\"";
		String parameter=AudioCutterSettings.getSettings().getStringNotNull(AudioCutterSettings.FFMPEG_CREATETEMPWAV_PARAMETER);
		parameter=String.format(parameter,mediaFilePathWithQuotes,wavFilePathWithQuotes);
		logger.debug("FFmpeg parameter: "+parameter);

		String sPatternDuration=AudioCutterSettings.getSettings().getStringNotNull(AudioCutterSettings.FFMPEG_CREATETEMPWAV_PATTERN_DURATION);
		patternDuration=Pattern.compile(sPatternDuration);
		String sPatternProgress=AudioCutterSettings.getSettings().getStringNotNull(AudioCutterSettings.FFMPEG_CREATETEMPWAV_PATTERN_PROGRESS);
		patternProgress=Pattern.compile(sPatternProgress);

		try {
			Exec exec=new Exec(ffmpegExecutable,parameter,null,this);
			int createWavTimeout=AudioCutterSettings.getSettings().getInt(AudioCutterSettings.FFMPEG_CREATETEMPWAV_TIMEOUT);
			exec.start();
			exec.join(createWavTimeout*1000);
			if(exec.isAlive()) {
				throw new RuntimeException("there´s something wrong, timeout while talking to FFmpeg");
			}

			if(exec.isError()) {
				Exception exception=exec.getException();
				throw new RuntimeException("Error while creating temporary WAV file: "+exception.getMessage(),exception);
			}
			if(receivedThrowableFromExec!=null) {
				throw new RuntimeException("Error while creating temporary WAV file: "+receivedThrowableFromExec.getMessage(),receivedThrowableFromExec);
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
			Matcher matcher;
			if(mediaDurationInSeconds==0) {
				matcher=patternDuration.matcher(line);
				if(matcher.matches()) {
					this.mediaDurationInSeconds=Calculator.getSecondsFromTimestamp(matcher.group(1),matcher.group(2),matcher.group(3));
				}
			}else {
				matcher=patternProgress.matcher(line);
				if(matcher.matches()) {
					int currentSeconds=Calculator.getSecondsFromTimestamp(matcher.group(1),matcher.group(2),matcher.group(3));
					int percent=100*currentSeconds/mediaDurationInSeconds;
					worker.progressBarCallback(percent);
				}
			}
		}
	}

}
