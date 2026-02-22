package lunartools.audiocutter.core.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Objects;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.FileTools;
import lunartools.audiocutter.common.model.AudioSectionModel;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.infrastructure.config.AudioCutterSettings;

public class ProjectService implements ProjectfileTagnames{
	private static Logger logger = LoggerFactory.getLogger(ProjectService.class);
	private final AudioCutterModel audioCutterModel;

	public ProjectService(AudioCutterModel audioCutterModel) {
		this.audioCutterModel=Objects.requireNonNull(audioCutterModel);
	}

	public void closeProject() {
		audioCutterModel.closeProject();
	}

	public void openProject(File projectFile){
		try {
			logger.info("read project file: "+projectFile);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			ProjectFileSaxHandler projectfileXmlHandler = new ProjectFileSaxHandler();
			saxParser.parse(projectFile, projectfileXmlHandler);  

			String pathMediafile=projectfileXmlHandler.getMediaFilePath();
			File fileMediafile=new File(pathMediafile);
			if(!fileMediafile.exists()) {
				throw new FileNotFoundException("file >"+fileMediafile.getAbsolutePath()+"< does not exist");
			}
			ArrayList<AudioSectionModel> audioSections=projectfileXmlHandler.getAudioSections();
			audioCutterModel.setProjectFile(projectFile);
			audioCutterModel.setAudioSections(audioSections);
			audioCutterModel.setMediaFile(fileMediafile);
			audioCutterModel.setProjectDirty(false);
		} catch (Exception e) {
			logger.error("error loading project",e);
			throw new ProjectException(e);
		}
	}

	public void saveProject(File projectFile){
		audioCutterModel.setProjectFile(projectFile);
		try {
			StringWriter stringWriter = new StringWriter();
			XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xmlStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);
			xmlStreamWriter.writeStartDocument();
			xmlStreamWriter.writeComment(AudioCutterModel.getProgramNameAndVersion());
			xmlStreamWriter.writeStartElement(ProjectfileTagnames.TAGNAME__AUDIOCUTTER);
			xmlStreamWriter.writeAttribute(ATTRIBUTENAME__AUDIOFILE, StringEscapeUtils.escapeXml10(audioCutterModel.getMediaFile().getAbsolutePath()));

			ArrayList<AudioSectionModel> audioSections=audioCutterModel.getAudioSections();
			for(int i=0;i<audioSections.size();i++) {
				AudioSectionModel audioSection=audioSections.get(i);
				xmlStreamWriter.writeStartElement(ProjectfileTagnames.TAGNAME__SECTION);
				String name=audioSection.getName();
				name=name==null?"":name.trim();
				xmlStreamWriter.writeAttribute(ProjectfileTagnames.ATTRIBUTENAME__NAME, StringEscapeUtils.escapeXml10(name));
				xmlStreamWriter.writeAttribute(ProjectfileTagnames.ATTRIBUTENAME__STARTSAMPLE, StringEscapeUtils.escapeXml10(String.valueOf(audioSection.getPosition())) );
				xmlStreamWriter.writeEndElement();
			}
			xmlStreamWriter.writeEndDocument();
			xmlStreamWriter.flush();
			xmlStreamWriter.close();
			stringWriter.close();
			logger.info("write project file: "+projectFile);
			FileTools.writeStringBufferToFile(projectFile, stringWriter.getBuffer(), false, "UTF-8");
			audioCutterModel.setProjectDirty(false);
		} catch (Exception e) {
			logger.error("error saving project",e);
			throw new ProjectException(e);
		}
	}

	public void saveProject() {
		File fileProject=Objects.requireNonNull(audioCutterModel.getProjectFile());
		saveProject(fileProject);
	}

	public boolean isProjectDirty() {
		return audioCutterModel.isProjectDirty();
	}

	public File getCurrentProjectDirectory() {
		File file=audioCutterModel.getProjectFile();
		if(file!=null) {
			return file.getParentFile();
		}
		if(AudioCutterSettings.getInstance().containsKey(AudioCutterSettings.PROJECTFILE_PATH)){
			file=new File(AudioCutterSettings.getInstance().getString(AudioCutterSettings.PROJECTFILE_PATH));
		}
		if(file!=null) {
			return file.getParentFile();
		}
		file=audioCutterModel.getMediaFile();
		if(file!=null) {
			return file.getParentFile();
		}
		return null;
	}

	public File getProjectFile() {
		return audioCutterModel.getProjectFile();
	}

	public File determineProjectFile() {
		File fileProject=audioCutterModel.getProjectFile();
		if(fileProject!=null) {
			return fileProject;
		}
		File fileCurrentDirectory=getCurrentProjectDirectory();
		File fileMedia=audioCutterModel.getMediaFile();
		if(fileCurrentDirectory==null || fileMedia==null) {
			return null;
		}
		String mediaFileName=fileMedia.getName();
		int p=mediaFileName.lastIndexOf('.');
		if(p!=-1) {
			return new File(fileCurrentDirectory,mediaFileName.substring(0, p));
		}
		return null;
	}

}
