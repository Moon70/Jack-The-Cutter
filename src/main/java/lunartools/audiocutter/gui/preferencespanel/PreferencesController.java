package lunartools.audiocutter.gui.preferencespanel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterModel;

public class PreferencesController implements ActionListener{
	private static Logger logger = LoggerFactory.getLogger(PreferencesController.class);
	private AudioCutterModel model;
	private PreferencesDialog preferencesDialog;

	public PreferencesController(AudioCutterModel model) {
		this.model=model;
	}

	public void editPreferences(JFrame parentJframe) {
		preferencesDialog = new PreferencesDialog(model,this,parentJframe);
		preferencesDialog.setVisible(true);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		if(logger.isTraceEnabled()) {
			logger.trace(actionEvent.toString());
		}
		Object source=actionEvent.getSource();
		if(source==preferencesDialog.buttonOK) {
			actionOk();
		}else if(source==preferencesDialog.buttonCancel) {
			actionCancel();
		}else if(source==preferencesDialog.buttonSelectFfmpegExe) {
			selectFile();
		}
	}

	void actionOk() {
		preferencesDialog.setVisible(false);
		preferencesDialog.dispose();
		String ffmpegExecutable=preferencesDialog.textfield.getText();
		if(ffmpegExecutable!=null && ffmpegExecutable.length()!=0) {
			model.setFFmpegExecutablePath(ffmpegExecutable);
		}
	}

	void actionCancel(){
		preferencesDialog.setVisible(false);
		preferencesDialog.dispose();
	}

	private void selectFile() {
		final JFileChooser fileChooser= new JFileChooser() {
			public void updateUI() {
				putClientProperty("FileChooser.useShellFolder", Boolean.FALSE);
				super.updateUI();
			}
		};
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new FFmpegFileFilter());
		String ffmpegExecutable=model.getFFmpegExecutablePath();
		if(ffmpegExecutable!=null) {
			fileChooser.setCurrentDirectory(new File(ffmpegExecutable).getParentFile());
		}
		fileChooser.setDialogTitle("Select FFmpeg executable");
		fileChooser.setPreferredSize(new Dimension(800,(int)(800/AudioCutterModel.SECTIOAUREA)));
		if(fileChooser.showOpenDialog(preferencesDialog)==JFileChooser.APPROVE_OPTION) {
			File file=fileChooser.getSelectedFile();
			preferencesDialog.textfield.setText(file.getAbsolutePath());
		}
	}

}
