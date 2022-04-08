package lunartools.audiocutter.ffmpeg;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;
import lunartools.exec.Exec;
import lunartools.exec.ExecOutputCallback;

public class CreateSectionsFromMediaService implements ExecOutputCallback{
	private static Logger logger = LoggerFactory.getLogger(CreateSectionsFromMediaService.class);
	private AudioCutterModel model;
	private static final int TIMEOUT_SECONDS=10;

	public void cutMediaFile(AudioCutterModel model,File mediafile,File sectionfile,String startposAsString,String endposAsString) {
		this.model=model;
		String ffmpegExecutable=model.getFFmpegExecutablePath();
		logger.debug("media file: "+mediafile);
		logger.debug("FFmpeg executable: "+ffmpegExecutable);
		String mediaFilePathWithQuotes="\""+mediafile.getAbsolutePath()+"\"";
		String sectionFilepathWithQuotes="\""+sectionfile.getAbsolutePath()+"\"";
		String parameter=String.format("-hide_banner -y -i %s -ss %s -to %s -c copy %s",mediaFilePathWithQuotes,startposAsString,endposAsString,sectionFilepathWithQuotes);
		logger.debug("FFmpeg parameter: "+parameter);

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
	public void execReceivedOutputLine(String line) {}

	@Override
	public void execReceivedErrorLine(String line) {}

	@Override
	public void execReceivedThrowable(Throwable throwable) {
		logger.error("Received throwable from Exec",throwable);
		model.setStatusMessage(new StatusMessage(StatusMessage.Type.ERROR,throwable.getMessage(),throwable));
	}

}
