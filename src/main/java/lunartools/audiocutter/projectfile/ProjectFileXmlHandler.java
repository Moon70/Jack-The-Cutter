package lunartools.audiocutter.projectfile;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import lunartools.audiocutter.AudioSection;

public class ProjectFileXmlHandler extends DefaultHandler implements ProjectfileTagnames{
	private String audioFilePath;
	private ArrayList<AudioSection> audioSections=new ArrayList<>();
	private AudioSection audioSection;
	private StringBuffer sbCharacters;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		sbCharacters=null;
		if (qName.equals(TAGNAME__AUDIOCUTTER)) {
			audioFilePath=attributes.getValue(ATTRIBUTENAME__AUDIOFILE);
		} else if (qName.equals(TAGNAME__SECTION)) {
			String name=attributes.getValue(ATTRIBUTENAME__NAME);
			String startsample=attributes.getValue(ATTRIBUTENAME__STARTSAMPLE);
			audioSection=new AudioSection(Integer.parseInt(startsample));
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

	public ArrayList<AudioSection> getAudioSections() {
		return audioSections;
	}

}
