package lunartools.audiocutter.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.FileTools;
import lunartools.ImageTools;
import lunartools.audiocutter.AudioCutterModel;

public class About {
	private static Logger logger = LoggerFactory.getLogger(About.class);
	private static final String RESOURCEPATH_HTML = "/About_AudioCutter.html";
	private static final String RESOURCEPATH_ICON = "/icons/ProgramIcon90x90.png";

	public static void showAboutDialog(JFrame jframe) {
		try {
			InputStream inputStream = About.class.getResourceAsStream(RESOURCEPATH_HTML);
			StringBuffer html=FileTools.getStringBufferFromInputStream(inputStream, StandardCharsets.UTF_8.name());
			JEditorPane editorPane = new JEditorPane("text/html", html.toString());
			editorPane.addHyperlinkListener(new HyperlinkListener(){
				@Override
				public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent){
					if (hyperlinkEvent.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
						try {
							Desktop.getDesktop().browse(hyperlinkEvent.getURL().toURI());
						} catch (IOException | URISyntaxException e) {
							logger.error("Error when opening link from About dialogue",e);
						}
					}
				}
			});
			editorPane.setEditable(false);
			editorPane.setBackground(jframe.getBackground());

			JOptionPane.showMessageDialog(jframe, editorPane, "About "+AudioCutterModel.PROGRAMNAME,JOptionPane.INFORMATION_MESSAGE,ImageTools.createImageIcon(RESOURCEPATH_ICON));
		} catch (Exception e) {
			logger.error("Error when opening About dialogue",e);
		}
	}
}
