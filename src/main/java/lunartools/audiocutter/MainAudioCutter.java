package lunartools.audiocutter;

import java.util.Objects;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.bootstrap.AudioCutterBootstrap;
import lunartools.audiocutter.common.ui.Dialogs;

public class MainAudioCutter {
	private static Logger logger = LoggerFactory.getLogger(MainAudioCutter.class);

	public static void main(String[] args) {
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			AudioCutterBootstrap.start();
		}catch(Exception e){
			logger.error("Unexpected error during application startup",e);
			Dialogs.showErrorMessage("Application failed to start:\n"+Objects.toString(e.getMessage(), e.getClass().getName()));
			System.exit(1);
		}
	}

}
