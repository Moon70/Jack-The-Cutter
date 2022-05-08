package lunartools.audiocutter.projectfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.FileTools;
import lunartools.audiocutter.AudioCutterController;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.AudioSection;

public class ProjectXmlService implements ProjectfileTagnames{
	private static Logger logger = LoggerFactory.getLogger(ProjectXmlService.class);

	public void saveProject(AudioCutterModel model, AudioCutterController controller,File projectFile) throws Exception{
		StringWriter stringWriter = new StringWriter();
		XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xmlStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);
		xmlStreamWriter.writeStartDocument();
		xmlStreamWriter.writeComment(AudioCutterModel.PROGRAMNAME+" "+AudioCutterModel.determineProgramVersion());
		xmlStreamWriter.writeStartElement(ProjectfileTagnames.TAGNAME__AUDIOCUTTER);
		xmlStreamWriter.writeAttribute(ATTRIBUTENAME__AUDIOFILE, StringEscapeUtils.escapeXml10(model.getMediaFile().getAbsolutePath()));

		ArrayList<AudioSection> audioSections=model.getAudioSections();
		for(int i=0;i<audioSections.size();i++) {
			AudioSection audioSection=audioSections.get(i);
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
		FileTools.writeFile(projectFile, stringWriter.getBuffer(), false, "UTF-8");
		model.setProjectIsDirty(false);
	}

	public void loadProject(AudioCutterModel model, AudioCutterController controller,File projectFile) throws Exception{
		logger.info("read project file: "+projectFile);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		ProjectFileXmlHandler projectfileXmlHandler = new ProjectFileXmlHandler();
		saxParser.parse(projectFile, projectfileXmlHandler);  

		String pathMediafile=projectfileXmlHandler.getMediaFilePath();
		File fileMediafile=new File(pathMediafile);
		if(!fileMediafile.exists()) {
			throw new FileNotFoundException("file >"+fileMediafile.getAbsolutePath()+"< does not exist");
		}
		ArrayList<AudioSection> audioSections=projectfileXmlHandler.getAudioSections();
		model.setProjectFile(projectFile);
		model.setAudioSections(audioSections);
		model.setMediaFile(fileMediafile);
		model.setProjectIsDirty(false);
	}

}
