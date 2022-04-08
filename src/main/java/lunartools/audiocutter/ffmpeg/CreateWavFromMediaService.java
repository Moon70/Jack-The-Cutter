package lunartools.audiocutter.ffmpeg;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.Calculator;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;
import lunartools.audiocutter.worker.CreateTempWavFileWorker;
import lunartools.exec.Exec;
import lunartools.exec.ExecOutputCallback;

public class CreateWavFromMediaService implements ExecOutputCallback{
	private static Logger logger = LoggerFactory.getLogger(CreateWavFromMediaService.class);
	private AudioCutterModel model;
	private Pattern patternDuration;
	private Pattern patternProgress;
	private int mediaDurationInSeconds;
	private CreateTempWavFileWorker worker;
	private static final int TIMEOUT_SECONDS=30;

	public void createWavFileFromMediaFile(AudioCutterModel model,CreateTempWavFileWorker worker,File mediafile,File wavfile) {
		this.model=model;
		this.worker=worker;
		logger.debug("media file: "+mediafile);
		logger.debug("wav file: "+wavfile);
		String ffmpegExecutable=model.getFFmpegExecutablePath();
		logger.debug("FFmpeg executable: "+ffmpegExecutable);
		String mediaFilePathWithQuotes="\""+mediafile.getAbsolutePath()+"\"";
		String wavFilePathWithQuotes="\""+wavfile.getAbsolutePath()+"\"";
		String parameter=String.format("-hide_banner -y -i %s -ar 44100 %s",mediaFilePathWithQuotes,wavFilePathWithQuotes);
		logger.debug("FFmpeg parameter: "+parameter);

		patternDuration=Pattern.compile(".*Duration: (\\d\\d):(\\d\\d):(\\d\\d)\\.\\d\\d,.*");
		patternProgress=Pattern.compile("size=.*time=(\\d\\d):(\\d\\d):(\\d\\d)\\.\\d\\d.*");

		try {
			Exec se=new Exec(ffmpegExecutable,parameter,null,this);
			se.start();
			se.join(TIMEOUT_SECONDS*1000);
			if(se.isAlive()) {
				model.setStatusMessage(new StatusMessage(StatusMessage.Type.ERROR,"there´s something wrong, timeout while talking to FFmpeg"));
			}

			if(se.isError()) {
				Exception exception=se.getException();
				model.setStatusMessage(new StatusMessage(StatusMessage.Type.ERROR,exception.getMessage(),exception));
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
		model.setStatusMessage(new StatusMessage(StatusMessage.Type.ERROR,throwable.getMessage(),throwable));
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
