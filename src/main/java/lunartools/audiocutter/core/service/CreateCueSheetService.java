package lunartools.audiocutter.core.service;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.FileTools;
import lunartools.audiocutter.common.model.AudioSectionModel;
import lunartools.audiocutter.common.ui.util.WavTools;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.swing.ProgressStepListener;

public class CreateCueSheetService {
	private static Logger logger = LoggerFactory.getLogger(CreateCueSheetService.class);
	private final AudioCutterModel audioCutterModel;

	public CreateCueSheetService(AudioCutterModel audioCutterModel) {
		this.audioCutterModel=Objects.requireNonNull(audioCutterModel);
	}

	public void createCueSheet(File fileCue,File fileWav,ProgressStepListener progressStepListener) {
		try {
			byte[] audiodata=audioCutterModel.getAudiodata();
			ArrayList<AudioSectionModel> audiosections=audioCutterModel.getAudioSections();

			int numberOfSamples=0;
			for(int i=0;i<audiosections.size();i++) {
				AudioSectionModel audioSection=audioCutterModel.getAudioSection(i);
				AudioSectionModel audioSectionNext=audioCutterModel.getAudioSection(i+1);
				String sectionName=audioSection.getName();
				if(sectionName!=null && sectionName.length()>0) {
					int endpos=audioSectionNext!=null?audioSectionNext.getPosition():audioCutterModel.getAudiodataLengthInSamples();
					numberOfSamples+=(endpos-audioSection.getPosition());
				}
			}

			StringWriter stringWriter=new StringWriter();
			stringWriter.append("REM COMMENT \"Created with "+AudioCutterModel.getProgramNameAndVersion()+"\"\n");
			stringWriter.append("FILE \""+fileWav.getName()+"\" WAVE\n");
			byte[] header=WavTools.createWavHeader(numberOfSamples<<2);
			FileTools.writeByteArrayToFile(fileWav, header, false);
			int totalSamples=0;
			String pauseIndex=null;
			int index=1;
			final int totalProgressSteps=100;
			for(int i=0;i<audiosections.size();i++) {
				int currentProgressStep=i*totalProgressSteps/audiosections.size();
				progressStepListener.stepDone(currentProgressStep, totalProgressSteps,null);
				AudioSectionModel audioSection=audioCutterModel.getAudioSection(i);
				AudioSectionModel audioSectionNext=audioCutterModel.getAudioSection(i+1);
				String name=audioSection.getName();
				if(name!=null && name.length()>0) {
					String indexAsString=(index<10?"0":"")+index;
					if(name.trim().equalsIgnoreCase("%pause%")) {
						if(pauseIndex!=null) {
							throw new RuntimeException("two %pause% in a row");
						}
						pauseIndex="    INDEX 00 "+createCueIndex(totalSamples)+"\n";
					}else {
						stringWriter.append("  TRACK "+indexAsString+" AUDIO\n");
						stringWriter.append("    TITLE \""+name+"\"\n");
						if(pauseIndex!=null) {
							stringWriter.append(pauseIndex);
							pauseIndex=null;
						}
						stringWriter.append("    INDEX 01 "+createCueIndex(totalSamples)+"\n");
						index++;
					}
					int startSampleIndex=audioSection.getPosition();
					int endSampleIndex=audioSectionNext!=null?audioSectionNext.getPosition():audioCutterModel.getAudiodataLengthInSamples();
					totalSamples+=(endSampleIndex-startSampleIndex);
					int startByteIndex=startSampleIndex<<2;
					int endByteIndex=endSampleIndex<<2;
					FileTools.writeByteArrayToFile(fileWav, Arrays.copyOfRange(audiodata, startByteIndex, endByteIndex), true);
				}
			}
			FileTools.writeStringToFile(fileCue, stringWriter.toString(),false,"UTF-8");

		} catch (Exception e) {
			logger.error("error creating CUE sheet",e);
			throw new CreateCueSheetException(e);
		}
	}

	private static String createCueIndex(int samples) {
		double seconds=samples/44100.0;
		int minutes=(int)(seconds/60.0);
		seconds-=minutes*60;
		int frames=(int)((seconds-(int)seconds)*75.0/100.0);
		seconds=(int)seconds;
		return String.format(Locale.ENGLISH,"%02d:%02d:%02d",minutes,(int)seconds,frames);
	}

}
