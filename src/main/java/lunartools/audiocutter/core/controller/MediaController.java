package lunartools.audiocutter.core.controller;

import java.awt.Dimension;
import java.io.File;
import java.util.Objects;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.SwingTools;
import lunartools.audiocutter.common.service.AudioPlayer;
import lunartools.audiocutter.common.ui.CueSheetFileFilter;
import lunartools.audiocutter.common.ui.Dialogs;
import lunartools.audiocutter.common.util.ProjectFileFilter;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.model.StatusMessage;
import lunartools.audiocutter.core.service.AutoCutService;
import lunartools.audiocutter.core.service.AutoCutWorker;
import lunartools.audiocutter.core.service.CreateCueSheetService;
import lunartools.audiocutter.core.service.CreateCueSheetWorker;
import lunartools.audiocutter.core.service.CutMediaFileService;
import lunartools.audiocutter.core.service.CutMediaFileWorker;
import lunartools.audiocutter.core.service.MediaService;
import lunartools.audiocutter.core.service.OpenMediaWorker;
import lunartools.swing.HasParentFrame;
import lunartools.swing.ProgressDialog;

public class MediaController {
	private static Logger logger = LoggerFactory.getLogger(MediaController.class);
	private final AudioCutterModel audioCutterModel;
	private final HasParentFrame hasParentFrame;
	private final MediaService mediaService;
	private final AutoCutService autoCutService;
	private final CutMediaFileService cutMediaFileService;
	private final CreateCueSheetService createCueSheetService;

	public MediaController(
			AudioCutterModel audioCutterModel,
			HasParentFrame hasParentFrame,
			MediaService projectService,
			AutoCutService autoCutService,
			CutMediaFileService cutMediaFileService,
			CreateCueSheetService createCueSheetService
			) {
		this.audioCutterModel=Objects.requireNonNull(audioCutterModel);
		this.hasParentFrame=Objects.requireNonNull(hasParentFrame);
		this.mediaService=Objects.requireNonNull(projectService);
		this.autoCutService=Objects.requireNonNull(autoCutService);
		this.cutMediaFileService=Objects.requireNonNull(cutMediaFileService);
		this.createCueSheetService=Objects.requireNonNull(createCueSheetService);
	}

	public void openMediaFileWithConfirmation() {
		if (mediaService.isProjectDirty() && Dialogs.userCanceledUnsavedChangesDialogue()) {
			return;
		}
		final JFileChooser fileChooser=new JFileChooser() {
			public void updateUI() {
				putClientProperty("FileChooser.useShellFolder", Boolean.FALSE);
				super.updateUI();
			}
		};
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(true);

		fileChooser.setCurrentDirectory(mediaService.getCurrentProjectDirectory());
		fileChooser.setDialogTitle("Select media file to load");
		fileChooser.setPreferredSize(new Dimension(800,(int)(800/SwingTools.SECTIOAUREA)));
		if(fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
			mediaService.closeProject();
			openMediaFile(fileChooser.getSelectedFile());
		}
	}

	public void openMediaFileWithConfirmation(File file) {
		if (mediaService.isProjectDirty() && Dialogs.userCanceledUnsavedChangesDialogue()) {
			return;
		}
		mediaService.closeProject();
		openMediaFile(file);
	}

	public void openMediaFile(File file) {
		if(isOpenMediaFileValid(file)) {
			AudioPlayer.getInstance().action_stop();
			OpenMediaWorker openMediaWorker=new OpenMediaWorker(audioCutterModel, mediaService, file);
			ProgressDialog.executeWithProgressDialog(hasParentFrame.getJFrame(),AudioCutterModel.PROGRAM_NAME,"Processing media file",openMediaWorker);
		}
	}

	private boolean isOpenMediaFileValid(File file) {
		if(!audioCutterModel.isFFmpegAvailable()) {
			audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.WARNING,"please set path to FFmpeg executable"));
			return false;
		}
		if(!file.exists()) {
			audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.WARNING,"file not found: "+file.getAbsolutePath()));

			//TODO: move to dialogs, trigger by event
			JOptionPane.showMessageDialog(
					hasParentFrame.getJFrame(),
					"File does not exist\n"+file,
					AudioCutterModel.PROGRAM_NAME,
					JOptionPane.ERROR_MESSAGE
					);
			return false;
		}
		return true;
	}

	public void cutMediaFile() {
		if(!audioCutterModel.isFFmpegAvailable()) {
			audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.WARNING,"please set path to FFmpeg executable"));
			return;
		}

		final JFileChooser fileChooser=new JFileChooser() {
			public void updateUI() {
				putClientProperty("FileChooser.useShellFolder", Boolean.FALSE);
				super.updateUI();
			}
		};
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		File currentMediaFile=audioCutterModel.getMediaFile();
		File lastSectionsFolder=audioCutterModel.getSectionsFolder();

		if(lastSectionsFolder!=null) {
			fileChooser.setCurrentDirectory(lastSectionsFolder.getParentFile());
			fileChooser.setSelectedFile(lastSectionsFolder);
		}else {
			fileChooser.setCurrentDirectory(currentMediaFile.getParentFile());
			File projectFile=audioCutterModel.getProjectFile();
			if(projectFile!=null) {
				String projectFilename=projectFile.getName();
				File suggestedFile=new File(currentMediaFile.getParentFile(),projectFilename.substring(0,projectFilename.length()-ProjectFileFilter.FILEEXTENSION.length()));
				fileChooser.setSelectedFile(suggestedFile);
			}
		}
		File choosenSectionFolder=null;
		fileChooser.setDialogTitle("Select folder to save sections");
		fileChooser.setPreferredSize(new Dimension(800,(int)(800/SwingTools.SECTIOAUREA)));
		if(fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
			choosenSectionFolder=fileChooser.getSelectedFile();
		}else if(logger.isTraceEnabled()){
			final String message="Select section folder dialogue canceled";
			logger.debug(message);
			audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.INFO,message));
			return;
		}
		if(choosenSectionFolder.exists()) {
			if(choosenSectionFolder.list().length>0) {
				if(Dialogs.userCanceledFolderNotEmptyDialogue()) {
					final String message="save media sections canceled";
					logger.debug(message);
					audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.INFO,message));
					return;
				}
			}
		}else {
			choosenSectionFolder.mkdirs();
		}
		audioCutterModel.setSectionsFolder(choosenSectionFolder);

		CutMediaFileWorker curMediaFileWorker=new CutMediaFileWorker(audioCutterModel,cutMediaFileService);
		ProgressDialog.executeWithProgressDialog(hasParentFrame.getJFrame(),AudioCutterModel.PROGRAM_NAME, "Cut media file",curMediaFileWorker);
	}

	public void autoCutWithConfirmation() {
		if(mediaService.hasAudioSections() && Dialogs.userCanceledAutocutDialogue()) {
			return;
		}
		AutoCutWorker autoCutWorker=new AutoCutWorker(audioCutterModel,autoCutService);
		ProgressDialog.executeWithProgressDialog(hasParentFrame.getJFrame(),AudioCutterModel.PROGRAM_NAME,"Autocut",autoCutWorker);
	}

	public void createCuesheet() {
		final JFileChooser fileChooser=new JFileChooser() {
			public void updateUI() {
				putClientProperty("FileChooser.useShellFolder", Boolean.FALSE);
				super.updateUI();
			}
		};
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new CueSheetFileFilter());
		File currentCueSheetFile=audioCutterModel.getCueSheetFile();
		if(currentCueSheetFile!=null) {
			fileChooser.setCurrentDirectory(currentCueSheetFile.getParentFile());
			fileChooser.setSelectedFile(currentCueSheetFile);
		}else {
			File currentMediaFile=audioCutterModel.getMediaFile();
			if(currentMediaFile!=null) {
				fileChooser.setCurrentDirectory(currentMediaFile.getParentFile());
				String mediaFileName=currentMediaFile.getName();
				int p=mediaFileName.lastIndexOf('.');
				if(p!=-1) {
					File suggesterCueSheetFile=new File(currentMediaFile.getParentFile(),mediaFileName.substring(0, p)+CueSheetFileFilter.FILEEXTENSION);
					fileChooser.setSelectedFile(suggesterCueSheetFile);
				}
			}
		}
		fileChooser.setDialogTitle("Select CUE sheet to save");
		fileChooser.setPreferredSize(new Dimension(800,(int)(800/SwingTools.SECTIOAUREA)));
		if(fileChooser.showSaveDialog(null)!=JFileChooser.APPROVE_OPTION) {
			return;
		}

		File fileCue=fileChooser.getSelectedFile();
		String filename=fileCue.getName();
		if(!filename.toLowerCase().endsWith(CueSheetFileFilter.FILEEXTENSION)) {
			fileCue=new File(fileCue.getParentFile(),filename+CueSheetFileFilter.FILEEXTENSION);
		}
		if(fileCue.exists() && Dialogs.userCanceledCueSheetFileExistsDialogue(fileCue)) {
			return;
		}
		audioCutterModel.setCueSheetFile(fileCue);
		String nameCue=fileCue.getName();
		File fileWav=new File(fileCue.getParentFile(),nameCue.substring(0, nameCue.length()-CueSheetFileFilter.FILEEXTENSION.length())+".wav");
		if(fileWav.exists() && Dialogs.userCanceledWavFileExistsDialogue(fileWav)) {
			return;
		}

		CreateCueSheetWorker createCueSheetWorker=new CreateCueSheetWorker(audioCutterModel,createCueSheetService,fileCue,fileWav);
		ProgressDialog.executeWithProgressDialog(hasParentFrame.getJFrame(),AudioCutterModel.PROGRAM_NAME, "Create CUE sheet",createCueSheetWorker);
	}

	public void createCutPointAtCursorPosition() {
		autoCutService.createCutPointAt(audioCutterModel.getCursorPositionSampleNumber());
	}
	
}
