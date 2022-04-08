package lunartools.audiocutter;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainAudioCutter {
	private static Logger logger = LoggerFactory.getLogger(MainAudioCutter.class);

	static {
		org.apache.log4j.ConsoleAppender console = new org.apache.log4j.ConsoleAppender();
		console.setLayout(new org.apache.log4j.PatternLayout("[%-5p] %c - %m%n")); 
		console.setThreshold(org.apache.log4j.Level.INFO);
		console.activateOptions();
		org.apache.log4j.Logger.getRootLogger().addAppender(console);
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.ALL);
	}

	public static void main(String[] args) {
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new AudioCutterController().openGUI();
		}catch(Throwable throwable){
			if(logger.isErrorEnabled()) {
				logger.error("Unexpected error",throwable);
			}else {
				throwable.printStackTrace();
			}
		}
	}

}
