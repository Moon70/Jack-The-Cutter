package lunartools.audiocutter.worker;

import java.io.File;
import java.util.ArrayList;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterController;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.AudioSection;
import lunartools.audiocutter.Calculator;
import lunartools.audiocutter.ffmpeg.CreateSectionsFromMediaService;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;
import lunartools.progressdialog.ProgressDialog;

public class CutMediaFileWorker extends SwingWorker<Void, Void> {
	private static Logger logger = LoggerFactory.getLogger(CreateTempWavFileWorker.class);
	private AudioCutterModel model;
	private AudioCutterController controller;

	public CutMediaFileWorker(AudioCutterModel model,AudioCutterController controller) {
		this.model=model;
		this.controller=controller;
	}

	@Override
	public Void doInBackground() {
		logger.debug("cutting media file...");
		StatusMessage statusMessage=new StatusMessage(StatusMessage.Type.INFO,"ready");
		controller.setBusy(true);
		try {
			ArrayList<AudioSection> audioSections=model.getAudioSections();
			float progressX=0;
			float progressStep=100.0f/audioSections.size();
			setProgress((int)progressX);
			File mediaFile=model.getMediaFile();
			String mediaFilepath=mediaFile.getAbsolutePath();
			String mediaFileExtension="";
			int p=mediaFilepath.lastIndexOf('.');
			if(p!=-1) {
				mediaFileExtension=mediaFilepath.substring(p);
			}

			File sectionsFolder=model.getSectionsFolder();
			for(int i=0;i<audioSections.size();i++) {
				AudioSection audioSection=audioSections.get(i);
				String sectionFilename=audioSection.getName();
				if(sectionFilename.trim().length()==0) {
					continue;
				}
				File fileCut=new File(sectionsFolder,sectionFilename+mediaFileExtension);

				firePropertyChange(ProgressDialog.PROPERTY_LINE2, null,fileCut.getName());
				setProgress((int)(progressX+=progressStep));

				int start=audioSection.getPosition();
				int end;
				if(i<audioSections.size()-1) {
					end=audioSections.get(i+1).getPosition();
				}else {
					end=model.getAudiodataLengthInSamples();
				}
				String startposAsString=Calculator.convertNumberOfSamplesToSecondsAsString(start);
				String endposAsString=Calculator.convertNumberOfSamplesToSecondsAsString(end);

				new CreateSectionsFromMediaService().cutMediaFile(model,mediaFile,fileCut,startposAsString,endposAsString);
			}

		} catch (Exception e) {
			logger.error("error while cutting media file",e);
			statusMessage=new StatusMessage(StatusMessage.Type.ERROR,e.getMessage());
		}finally {
			controller.setBusy(false);
			model.setStatusMessage(statusMessage);
		}
		return null;
	}

	@Override
	public void done() {
		logger.debug("media file was cut");
	}

}
