package lunartools.audiocutter.core.controller;

import java.io.File;
import java.util.Objects;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.formdev.flatlaf.util.SystemFileChooser;

import lunartools.audiocutter.common.service.AudioPlayer;
import lunartools.audiocutter.common.ui.Dialogs;
import lunartools.audiocutter.common.util.ProjectFileFilter;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.model.StatusMessage;
import lunartools.audiocutter.core.service.ProjectException;
import lunartools.audiocutter.core.service.ProjectService;

public class ProjectController {
	private final AudioCutterModel audioCutterModel;
	private final ProjectService projectService;

	public enum OpenProjectResult {
		SUCCESS,
		ABORTED,
		FAILURE
	}

	public ProjectController(AudioCutterModel audioCutterModel,ProjectService projectService) {
		this.audioCutterModel=Objects.requireNonNull(audioCutterModel);
		this.projectService=Objects.requireNonNull(projectService);
	}

	public OpenProjectResult openProjectWithConfirmation() {
		if(projectService.isProjectDirty() && Dialogs.userCanceledUnsavedChangesDialogue()){
			return OpenProjectResult.ABORTED;
		}
		final SystemFileChooser fileChooser=new SystemFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new SystemFileChooser.FileNameExtensionFilter(ProjectFileFilter.DESCRIPTION,ProjectFileFilter.FILE_EXTENSION));

		fileChooser.setCurrentDirectory(projectService.getCurrentProjectDirectory());
		fileChooser.setSelectedFile(projectService.getProjectFile());
		fileChooser.setDialogTitle("Select project file to load");
		//fileChooser.setPreferredSize(new Dimension(800,(int)(800/SwingTools.SECTIOAUREA)));
		if(fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
			File choosenProjectFile=fileChooser.getSelectedFile();
			String filename=choosenProjectFile.getName();
			if(!filename.toLowerCase().endsWith(ProjectFileFilter.FILE_EXTENSION)) {
				choosenProjectFile=new File(choosenProjectFile.getParentFile(),filename+ProjectFileFilter.FILE_EXTENSION);
			}
			if(!choosenProjectFile.exists()) {
				JOptionPane.showMessageDialog(
						null,
						"File does not exist\n"+choosenProjectFile,
						AudioCutterModel.PROGRAM_NAME,
						JOptionPane.ERROR_MESSAGE
						);
				return OpenProjectResult.FAILURE;
			}
			AudioPlayer.getInstance().stopAudioPlayback();
			projectService.closeProject();
			try {
				projectService.openProject(choosenProjectFile);
			} catch (ProjectException e) {
				audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.ERROR,"error loading project: "+e.getMessage()));
				return OpenProjectResult.FAILURE;
			}
		}else {
			return OpenProjectResult.ABORTED;
		}
		return OpenProjectResult.SUCCESS;
	}

	public OpenProjectResult openProjectWithConfirmation(File file) {
		if(projectService.isProjectDirty() && Dialogs.userCanceledUnsavedChangesDialogue()){
			return OpenProjectResult.ABORTED;
		}
		AudioPlayer.getInstance().stopAudioPlayback();
		projectService.closeProject();
		try {
			projectService.openProject(file);
		} catch (ProjectException e) {
			audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.ERROR,"error loading project: "+e.getMessage()));
			return OpenProjectResult.FAILURE;
		}
		return OpenProjectResult.SUCCESS;
	}

	public void closeProjectWithConfirmation() {
		if(projectService.isProjectDirty() && Dialogs.userCanceledUnsavedChangesDialogue()){
			return;
		}
		projectService.closeProject();
	}

	public void saveProjectAsWithConfirmation() {
		final SystemFileChooser fileChooser=new SystemFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new SystemFileChooser.FileNameExtensionFilter(ProjectFileFilter.DESCRIPTION,ProjectFileFilter.FILE_EXTENSION));
		fileChooser.setCurrentDirectory(projectService.getCurrentProjectDirectory());
		fileChooser.setSelectedFile(projectService.determineProjectFile());
		fileChooser.setDialogTitle("Select project file to save");
		//fileChooser.setPreferredSize(new Dimension(800,(int)(800/SwingTools.SECTIOAUREA)));
		if(fileChooser.showSaveDialog(null)==SystemFileChooser.APPROVE_OPTION) {
			File choosenProjectFile=fileChooser.getSelectedFile();
			String filename=choosenProjectFile.getName();
			if(!filename.toLowerCase().endsWith(ProjectFileFilter.FILE_EXTENSION)) {
				choosenProjectFile=new File(choosenProjectFile.getParentFile(),filename+ProjectFileFilter.FILE_EXTENSION);
			}
			if(choosenProjectFile.exists() && Dialogs.userCanceledProjectFileExistsDialogue(choosenProjectFile)) {
				return;
			}
			try {
				projectService.saveProject(choosenProjectFile);
			} catch (ProjectException e) {
				audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.ERROR,"error saving project: "+e.getMessage()));
			}
		}
	}

	public void saveProject() {
		try {
			projectService.saveProject();
		} catch (ProjectException e) {
			audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.ERROR,"error saving project: "+e.getMessage()));
		}
	}
}
