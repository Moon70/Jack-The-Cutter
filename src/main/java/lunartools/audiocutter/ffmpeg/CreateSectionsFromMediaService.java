package lunartools.audiocutter.ffmpeg;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;
import lunartools.exec.Exec;
import lunartools.exec.ExecOutputCallback;

public class CreateSectionsFromMediaService implements ExecOutputCallback{
	private static Logger logger = LoggerFactory.getLogger(CreateSectionsFromMediaService.class);
	private AudioCutterModel model;
	private Pattern patternOutputFormatError;
	private static final int TIMEOUT_SECONDS=10;
	private String error;

	public void cutMediaFile(AudioCutterModel model,File mediafile,File sectionfile,String startposAsString,String endposAsString) {
		this.model=model;
		String ffmpegExecutable=model.getFFmpegExecutablePath();
		logger.debug("media file: "+mediafile);
		logger.debug("FFmpeg executable: "+ffmpegExecutable);
		String mediaFilePathWithQuotes="\""+mediafile.getAbsolutePath()+"\"";
		String sectionFilepathWithQuotes="\""+sectionfile.getAbsolutePath()+"\"";
		String parameter=String.format("-hide_banner -y -i %s -ss %s -to %s -c copy %s",mediaFilePathWithQuotes,startposAsString,endposAsString,sectionFilepathWithQuotes);
		logger.debug("FFmpeg parameter: "+parameter);

		patternOutputFormatError=Pattern.compile(".*(Unable to find a suitable output format.*)");

		try {
			Exec se=new Exec(ffmpegExecutable,parameter,this);
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
