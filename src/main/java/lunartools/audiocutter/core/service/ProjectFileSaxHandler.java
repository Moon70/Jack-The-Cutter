package lunartools.audiocutter.core.service;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import lunartools.audiocutter.common.model.AudioSectionModel;

public class ProjectFileSaxHandler extends DefaultHandler implements ProjectfileTagnames{
	private String audioFilePath;
	private ArrayList<AudioSectionModel> audioSections=new ArrayList<>();
	private AudioSectionModel audioSection;
	private StringBuffer sbCharacters;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		sbCharacters=null;
		if (qName.equals(TAGNAME__AUDIOCUTTER)) {
			audioFilePath=attributes.getValue(ATTRIBUTENAME__AUDIOFILE);
		} else if (qName.equals(TAGNAME__SECTION)) {
			String name=attributes.getValue(ATTRIBUTENAME__NAME);
			String startsample=attributes.getValue(ATTRIBUTENAME__STARTSAMPLE);
			audioSection=new AudioSectionModel(Integer.parseInt(startsample));
			audioSection.setName(name);
		}else {
			throw new RuntimeException("unknown element: "+qName);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase(TAGNAME__SECTION)) {
			audioSections.add(audioSection);
			audioSection=null;
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		if(sbCharacters==null) {
			sbCharacters=new StringBuffer();
		}
		sbCharacters.append(ch, start, length);
	}

	public String getMediaFilePath() {
		return audioFilePath;
	}

	public ArrayList<AudioSectionModel> getAudioSections() {
		return audioSections;
	}

}
