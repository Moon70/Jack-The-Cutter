package lunartools.audiocutter.common.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.ImageTools;
import lunartools.audiocutter.common.service.AudioPlayer;
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

	public Action createPlayCursorAction() {
		return new AbstractAction(null,ImageTools.createImageIcon("/icons/Button_playCursor.png")) {
			
			{
		        putValue(Action.SHORT_DESCRIPTION, "play from cursor position");
		    }
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AudioPlayer.getInstance().action_playFromCursorPosition();
			}
		};
	}

	public Action createPlaySelectionAction() {
		return new AbstractAction(null,ImageTools.createImageIcon("/icons/Button_playSelection.png")) {
			
			{
		        putValue(Action.SHORT_DESCRIPTION, "play selection");
		    }
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AudioPlayer.getInstance().action_playSelection();
			}
		};
	}

	public Action createPauseAction() {
		return new AbstractAction(null,ImageTools.createImageIcon("/icons/Button_pause.png")) {
			
			{
		        putValue(Action.SHORT_DESCRIPTION, "pause");
		    }
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AudioPlayer.getInstance().action_pause();
			}
		};
	}

	public Action createStopAction() {
		return new AbstractAction(null,ImageTools.createImageIcon("/icons/Button_stop.png")) {
			
			{
		        putValue(Action.SHORT_DESCRIPTION, "stop");
		    }
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AudioPlayer.getInstance().action_stop();
			}
		};
	}

	public Action createPreviousAction() {
		return new AbstractAction(null,ImageTools.createImageIcon("/icons/Button_previousSection.png")) {
			
			{
		        putValue(Action.SHORT_DESCRIPTION, "goto previous section");
		    }
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AudioPlayer.getInstance().action_PrevSection();
			}
		};
	}

	public Action createNextAction() {
		return new AbstractAction(null,ImageTools.createImageIcon("/icons/Button_nextSection.png")) {
			
			{
		        putValue(Action.SHORT_DESCRIPTION, "goto next section");
		    }
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AudioPlayer.getInstance().action_NextSection();
			}
		};
	}

	public Action createZoomInAction() {
		return new AbstractAction(null,ImageTools.createImageIcon("/icons/Button_zoomIn.png")) {
			
			{
		        putValue(Action.SHORT_DESCRIPTION, "zoom in");
		    }
			
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.getModel().setZoom(controller.getModel().getZoom()+1);
			}
		};
	}

	public Action createZoomOutAction() {
		return new AbstractAction(null,ImageTools.createImageIcon("/icons/Button_zoomOut.png")) {
			
			{
		        putValue(Action.SHORT_DESCRIPTION, "zoom out");
		    }
			
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.getModel().setZoom(controller.getModel().getZoom()-1);
			}
		};
	}

	public Action createZoomSelectionAction() {
		return new AbstractAction(null,ImageTools.createImageIcon("/icons/Button_zoomSelection.png")) {
			
			{
		        putValue(Action.SHORT_DESCRIPTION, "zoom selection");
		    }
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AudioCutterModel model=controller.getModel();
				model.setViewRangeInSamples(model.getSelectionStartInSamples(),model.getSelectionEndInSamples());
			}
		};
	}

	public Action createFitProjectAction() {
		return new AbstractAction(null,ImageTools.createImageIcon("/icons/Button_fitProject.png")) {
			
			{
		        putValue(Action.SHORT_DESCRIPTION, "fit project");
		    }
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AudioCutterModel model=controller.getModel();
				model.setViewRangeInSamples(0,model.getAudiodataLengthInSamples());
				model.setZoom(0);
			}
		};
	}

	public Action createAmplitudeZoomAction() {
		return new AbstractAction(null,ImageTools.createImageIcon("/icons/Button_AmplitudeZoom.png")) {
			
			{
		        putValue(Action.SHORT_DESCRIPTION, "zoom amplitude");
		    }
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AudioCutterModel model=controller.getModel();
				model.setAmplitudeZoom(!model.isAmplitudeZoom());
			}
		};
	}

	public Action createCutAction() {
		return new AbstractAction(null,ImageTools.createImageIcon("/icons/ProgramIcon24.png")) {
			
			{
		        putValue(Action.SHORT_DESCRIPTION, "cut at cursor position");
		    }
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaController.createCutPointAtCursorPosition();
			}
		};
	}
	
	
}
