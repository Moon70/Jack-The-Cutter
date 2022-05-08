package lunartools.audiocutter.worker;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.FileTools;
import lunartools.audiocutter.AudioCutterController;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.AudioSection;
import lunartools.audiocutter.WavTools;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;

public class CreateCueSheetWorker extends SwingWorker<Void, Void> {
	private static Logger logger = LoggerFactory.getLogger(CreateCueSheetWorker.class);
	private AudioCutterModel model;
	private AudioCutterController controller;
	private File fileCue;
	private File fileWav;

	public CreateCueSheetWorker(AudioCutterModel model,AudioCutterController controller,File fileCue,File fileWav) {
		this.model=model;
		this.controller=controller;
		this.fileCue=fileCue;
		this.fileWav=fileWav;
	}

	@Override
	public Void doInBackground() {
		logger.debug("create CUE sheet and WAV file...");
		model.setStatusMessage(null);
		StatusMessage statusMessage=new StatusMessage(StatusMessage.Type.INFO,"ready");
		controller.setBusy(true);
		try {
			createCueSheet();
		} catch (Exception e) {
			logger.error("error while creating CUE sheet",e);
			statusMessage=new StatusMessage(StatusMessage.Type.ERROR,e.getMessage());
		}finally {
			controller.setBusy(false);
			model.setStatusMessage(statusMessage);
		}
		return null;
	}

	private void createCueSheet() throws IOException {
		byte[] audiodata=model.getAudiodata();
		ArrayList<AudioSection> audiosections=model.getAudioSections();

		int numberOfSamples=0;
		for(int i=0;i<audiosections.size();i++) {
			AudioSection audioSection=model.getAudioSection(i);
			AudioSection audioSectionNext=model.getAudioSection(i+1);
			String sectionName=audioSection.getName();
			if(sectionName!=null && sectionName.length()>0) {
				int endpos=audioSectionNext!=null?audioSectionNext.getPosition():model.getAudiodataLengthInSamples();
				numberOfSamples+=(endpos-audioSection.getPosition());
			}
		}

		StringWriter stringWriter=new StringWriter();
		stringWriter.append("REM COMMENT \"Created with "+AudioCutterModel.PROGRAMNAME+" "+AudioCutterModel.determineProgramVersion()+"\"\n");
		stringWriter.append("FILE \""+fileWav.getName()+"\" WAVE\n");
		byte[] header=WavTools.createWavHeader(numberOfSamples<<2);
		FileTools.writeFile(fileWav, header, false);
		int totalSamples=0;
		String pauseIndex=null;
		int progress;
		int index=1;
		for(int i=0;i<audiosections.size();i++) {
			progress=i*100/audiosections.size();
			setProgress((int)(progress));
			AudioSection audioSection=model.getAudioSection(i);
			AudioSection audioSectionNext=model.getAudioSection(i+1);
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
				int endSampleIndex=audioSectionNext!=null?audioSectionNext.getPosition():model.getAudiodataLengthInSamples();
				totalSamples+=(endSampleIndex-startSampleIndex);
				int startByteIndex=startSampleIndex<<2;
				int endByteIndex=endSampleIndex<<2;
				FileTools.writeFile(fileWav, Arrays.copyOfRange(audiodata, startByteIndex, endByteIndex), true);
			}
		}
		FileTools.writeFile(fileCue, stringWriter.toString(),false,"UTF-8");
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
