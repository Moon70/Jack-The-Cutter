package lunartools.audiocutter.common.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.core.AudioCutterController;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.controller.MediaController;
import lunartools.audiocutter.core.controller.ProjectController;
import lunartools.audiocutter.core.controller.ProjectController.OpenProjectResult;
import lunartools.audiocutter.gui.preferencespanel.PreferencesController;

public class ActionFactory {
	private static Logger logger = LoggerFactory.getLogger(ActionFactory.class);
	private final AudioCutterController controller;
	private final ProjectController projectController;
	private final MediaController mediaController;
	
	public ActionFactory(
			AudioCutterController audioCutterController,
			ProjectController projectController,
			MediaController mediaController
			) {
		this.controller=Objects.requireNonNull(audioCutterController);
		this.projectController=Objects.requireNonNull(projectController);
		this.mediaController=Objects.requireNonNull(mediaController);
	}

	public Action createOpenProjectAction() {
		return new AbstractAction("Open project") {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(projectController.openProjectWithConfirmation()==OpenProjectResult.SUCCESS) {
					mediaController.openMediaFile(controller.getModel().getMediaFile());
				}
			}
		};
	}

	public Action createOpenRecentProjectFileAction(File file) {
		return new AbstractAction(file.getName()) {
			
			{
				putValue(SHORT_DESCRIPTION, file.getAbsolutePath());
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(projectController.openProjectWithConfirmation(file)==OpenProjectResult.SUCCESS) {
					mediaController.openMediaFileWithConfirmation(controller.getModel().getMediaFile());
				}
			}
		};
	}

	public Action createCloseProjectAction() {
		return new AbstractAction("Close project") {
			@Override
			public void actionPerformed(ActionEvent e) {
				projectController.closeProjectWithConfirmation();
			}
		};
	}

	public Action createSaveProjectAsAction() {
		return new AbstractAction("Save project as") {
			@Override
			public void actionPerformed(ActionEvent e) {
				projectController.saveProjectAsWithConfirmation();
			}
		};
	}

	public Action createSaveProjectAction() {
		return new AbstractAction("Save project") {
			@Override
			public void actionPerformed(ActionEvent e) {
				projectController.saveProject();
			}
		};
	}

	public Action createOpenMediaFileAction() {
		return new AbstractAction("Open media file") {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaController.openMediaFileWithConfirmation();
			}
		};
	}

	public Action createOpenRecentMediaFileAction(File file) {
		return new AbstractAction(file.getName()) {
			
			{
				putValue(SHORT_DESCRIPTION, file.getAbsolutePath());
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaController.openMediaFileWithConfirmation(file);
			}
		};
	}

	public Action createCutMediaFileAction() {
		return new AbstractAction("Cut media file") {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaController.cutMediaFile();
			}
		};
	}

	public Action createCreateCuesheetAction() {
		return new AbstractAction("Create CUE Sheet") {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaController.createCuesheet();
			}
		};
	}
	
	public Action createAutoCutAction() {
		return new AbstractAction("Auto cut") {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaController.autoCutWithConfirmation();
			}
		};
	}
	
	public Action createOpenPreferencesAction() {
		return new AbstractAction("Preferences") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new PreferencesController(controller.getModel()).editPreferences(controller.getJFrame());
			}
		};
	}

	public Action createExitProgramAction() {
		return new AbstractAction("Exit") {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.exit();
			}
		};
	}

	public Action createAboutAction() {
		return new AbstractAction("About "+AudioCutterModel.PROGRAM_NAME) {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.openAboutDialogue();
			}
		};
	}
}
