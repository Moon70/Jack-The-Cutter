package lunartools.audiocutter.core.exec;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.common.ui.util.SampleUtils;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.infrastructure.config.AudioCutterSettings;
import lunartools.cli.Exec;
import lunartools.cli.ExecHelper;
import lunartools.cli.ExecOutputCallback;
import lunartools.swing.ProgressStepListener;

public class CreateWavFromMediaExecutor implements ExecOutputCallback{
	private static Logger logger = LoggerFactory.getLogger(CreateWavFromMediaExecutor.class);
	private ProgressStepListener progressStepListener;
	private Pattern patternDuration;
	private Pattern patternProgress;
	private int mediaLengthInSeconds;
	private volatile Throwable throwableFromExec;
	private final int totalProgressSteps=100;

	public void createWavFileFromMediaFile(
			AudioCutterModel audioCutterModel,
			File mediafile,
			File wavfile,
			ProgressStepListener progressStepListener
			) {
		this.progressStepListener=progressStepListener;
		progressStepListener.stepDone(0, totalProgressSteps, null);
		logger.debug("media file: "+mediafile);
		logger.debug("wav file: "+wavfile);
		String ffmpegExecutable=audioCutterModel.getFFmpegExecutablePath();
		logger.debug("FFmpeg executable: "+ffmpegExecutable);
		String ffmpegParameter=AudioCutterSettings.getInstance().getStringNotNull(AudioCutterSettings.FFMPEG_CREATETEMPWAV_PARAMETER);

		String durationPattern=AudioCutterSettings.getInstance().getStringNotNull(AudioCutterSettings.FFMPEG_CREATETEMPWAV_PATTERN_DURATION);
		patternDuration=Pattern.compile(durationPattern);
		String progressPattern=AudioCutterSettings.getInstance().getStringNotNull(AudioCutterSettings.FFMPEG_CREATETEMPWAV_PATTERN_PROGRESS);
		patternProgress=Pattern.compile(progressPattern);

		String[] commandArray=ExecHelper.createCmdArray(ffmpegExecutable, ffmpegParameter, mediafile.getAbsolutePath(),wavfile.getAbsolutePath());
		Exec exec=new Exec(commandArray,this);
		int createWavTimeout=AudioCutterSettings.getInstance().getInt(AudioCutterSettings.FFMPEG_CREATETEMPWAV_TIMEOUT);
		exec.start();
		try {
			exec.join(createWavTimeout*1000);
		} catch (InterruptedException e) {
			logger.error("Executor interrupted");
			Thread.currentThread().interrupt();
			throw new CreateWavFromMediaException("Executor interrupted",e);
		}
		if(exec.isAlive()) {
			throw new CreateWavFromMediaException("there´s something wrong, timeout while talking to FFmpeg");
		}

		if(exec.isError()) {
			Exception exception=exec.getException();
			throw new CreateWavFromMediaException("Error while creating temporary WAV file: "+exception.getMessage(),exception);
		}
		if(throwableFromExec!=null) {
			throw new CreateWavFromMediaException("Error while creating temporary WAV file: "+throwableFromExec.getMessage());
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
			Matcher matcher;
			if(mediaLengthInSeconds==0) {
				matcher=patternDuration.matcher(line);
				if(matcher.matches()) {
					this.mediaLengthInSeconds=SampleUtils.getSecondsFromTimestamp(matcher.group(1),matcher.group(2),matcher.group(3));
				}
			}else {
				matcher=patternProgress.matcher(line);
				if(matcher.matches()) {
					int currentSeconds=SampleUtils.getSecondsFromTimestamp(matcher.group(1),matcher.group(2),matcher.group(3));
					int currentProgressStepPercent=totalProgressSteps*currentSeconds/mediaLengthInSeconds;
					progressStepListener.stepDone(currentProgressStepPercent, totalProgressSteps, null);
				}
			}
		}
	}

}
