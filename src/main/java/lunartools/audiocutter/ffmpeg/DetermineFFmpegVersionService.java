package lunartools.audiocutter.ffmpeg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;
import lunartools.audiocutter.worker.DetermineFFmpegVersionWorker;
import lunartools.exec.Exec;
import lunartools.exec.ExecOutputCallback;

public class DetermineFFmpegVersionService implements ExecOutputCallback{
	private static Logger logger = LoggerFactory.getLogger(DetermineFFmpegVersionService.class);
	private AudioCutterModel model;
	private Pattern patternVersion;
	private String version;
	private static final int TIMEOUT_SECONDS=5;

	public void determineFFmpegVersion(AudioCutterModel model,DetermineFFmpegVersionWorker worker) {
		this.model=model;
		String ffmpegExecutable=model.getFFmpegExecutablePath();
		logger.debug("FFmpeg executable: "+ffmpegExecutable);
		String parameter="-hide_banner -version";
		logger.debug("FFmpeg parameter: "+parameter);

		patternVersion=Pattern.compile("(ffmpeg version.*)");

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
		model.setStatusMessage(new StatusMessage(StatusMessage.Type.ERROR,throwable.getMessage(),throwable));
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
