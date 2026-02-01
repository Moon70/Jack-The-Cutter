package lunartools.audiocutter.core.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.common.model.AudioSectionModel;
import lunartools.audiocutter.common.ui.util.SampleUtils;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.exec.CutMediaFileExecutor;
import lunartools.swing.ProgressStepListener;

public class CutMediaFileService {
	private static Logger logger = LoggerFactory.getLogger(CutMediaFileService.class);
	private final AudioCutterModel audioCutterModel;

	public CutMediaFileService(AudioCutterModel audioCutterModel) {
		this.audioCutterModel=Objects.requireNonNull(audioCutterModel);
	}

	public void cutMediaFile(ProgressStepListener progressStepListener) {
		try {
			ArrayList<AudioSectionModel> audioSections=audioCutterModel.getAudioSections();
			File mediaFile=audioCutterModel.getMediaFile();
			String mediaFilePath=mediaFile.getAbsolutePath();
			String mediaFileExtension="";
			int p=mediaFilePath.lastIndexOf('.');
			if(p!=-1) {
				mediaFileExtension=mediaFilePath.substring(p);
			}

			File sectionsFolder=audioCutterModel.getSectionsFolder();
			final int totalProgressSteps=audioSections.size();
			for(int i=0;i<audioSections.size();i++) {
				int currentProgressStep=i;
				AudioSectionModel audioSection=audioSections.get(i);
				String sectionFilename=audioSection.getName();
				if(sectionFilename==null || sectionFilename.trim().length()==0) {
					continue;
				}
				File fileCut=new File(sectionsFolder,sectionFilename+mediaFileExtension);
				progressStepListener.stepDone(currentProgressStep, totalProgressSteps, fileCut.getName());

				int start=audioSection.getPosition();
				int end;
				if(i<audioSections.size()-1) {
					end=audioSections.get(i+1).getPosition();
				}else {
					end=audioCutterModel.getAudiodataLengthInSamples();
				}
				String startposAsString=SampleUtils.convertNumberOfSamplesToSecondsAsString(start);
				String endposAsString=SampleUtils.convertNumberOfSamplesToSecondsAsString(end);

				new CutMediaFileExecutor().cutMediaFile(audioCutterModel,mediaFile,fileCut,startposAsString,endposAsString);

			}

		} catch (Exception e) {
			logger.error("error cutting media file",e);
			throw new CutMediaFileException(e);
		}
	}

}
