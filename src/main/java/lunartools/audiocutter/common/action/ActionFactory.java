package lunartools.audiocutter.common.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;

import lunartools.audiocutter.common.model.AudioSectionModel;
import lunartools.audiocutter.common.service.AudioPlayer;
import lunartools.audiocutter.common.ui.IconProvider;
import lunartools.audiocutter.common.ui.Icons;
import lunartools.audiocutter.core.AudioCutterController;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.controller.MediaController;
import lunartools.audiocutter.core.controller.ProjectController;
import lunartools.audiocutter.core.controller.ProjectController.OpenProjectResult;
import lunartools.audiocutter.gui.preferencespanel.PreferencesController;

public class ActionFactory {
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
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.RECENT_PROJECT_FILE,AbstractAction.class)) {

			{
				putValue(NAME, file.getName());
				putValue(SHORT_DESCRIPTION, file.getAbsolutePath());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if(projectController.openProjectWithConfirmation(file)==OpenProjectResult.SUCCESS) {
					mediaController.openMediaFile(controller.getModel().getMediaFile());
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
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.RECENT_MEDIA_FILE,AbstractAction.class)) {

			{
				putValue(NAME, file.getName());
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
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.PLAY_CURSOR,AbstractAction.class)) {

			{
				putValue(Action.SHORT_DESCRIPTION, "play from cursor position");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				AudioPlayer.getInstance().playFromCursorPosition();
			}
		};
	}

	public Action createPlaySelectionAction() {
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.PLAY_SELECTION,AbstractAction.class)) {

			{
				putValue(Action.SHORT_DESCRIPTION, "play selection");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				AudioPlayer.getInstance().playSelection();
			}
		};
	}

	public Action createPauseAction() {
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.PAUSE,AbstractAction.class)) {

			{
				putValue(Action.SHORT_DESCRIPTION, "pause");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				AudioPlayer.getInstance().pauseAudioPlayback();
			}
		};
	}

	public Action createStopAction() {
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.STOP,AbstractAction.class)) {

			{
				putValue(Action.SHORT_DESCRIPTION, "stop");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				AudioPlayer.getInstance().stopAudioPlayback();
			}
		};
	}

	public Action createPreviousAction() {
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.PREV_SECTION,AbstractAction.class)) {

			{
				putValue(Action.SHORT_DESCRIPTION, "goto previous section");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				AudioPlayer.getInstance().playPrevSection();
			}
		};
	}

	public Action createNextAction() {
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.NEXT_SECTION,AbstractAction.class)) {

			{
				putValue(Action.SHORT_DESCRIPTION, "goto next section");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				AudioPlayer.getInstance().playNextSection();
			}
		};
	}

	public Action createZoomInAction() {
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.ZOOM_IN,AbstractAction.class)) {

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
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.ZOOM_OUT,AbstractAction.class)) {

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
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.ZOOM_SELECTION,AbstractAction.class)) {

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
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.FIT_TO_WINDOW,AbstractAction.class)) {

			{
				putValue(Action.SHORT_DESCRIPTION, "fit to window");
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
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.AMPLITUDE_ZOOM,AbstractAction.class)) {

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
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.ADD_CUT_POINT,AbstractAction.class)) {

			{
				putValue(Action.SHORT_DESCRIPTION, "cut at cursor position");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				mediaController.createCutPointAtCursorPosition();
			}
		};
	}

	public Action createPopupPlayAction(JTable jTable) {
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.PLAY_CURSOR,AbstractAction.class)) {

			{
				putValue(Action.NAME, "play section");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				int contextRow = jTable.getSelectedRow();
				if (contextRow < 0) return;

				//int modelRow = table.convertRowIndexToModel(contextRow);
				AudioPlayer.getInstance().playSection(contextRow);
			}
		};
	}

	public Action createPopupZoomSectionAction(JTable jTable) {
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.ZOOM_SELECTION,AbstractAction.class)) {

			{
				putValue(Action.NAME, "zoom section");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				int contextRow = jTable.getSelectedRow();
				if (contextRow < 0) return;

				//int modelRow = table.convertRowIndexToModel(contextRow);
				AudioCutterModel audioCutterModel=controller.getModel();
				AudioSectionModel audioSection=audioCutterModel.getAudioSection(contextRow);
				AudioSectionModel audioSectionNext=audioCutterModel.getAudioSection(contextRow+1);
				if(audioSectionNext==null) {
					audioCutterModel.setViewRangeInSamples(audioSection.getPosition(),audioCutterModel.getAudiodataLengthInSamples());
				}else {
					audioCutterModel.setViewRangeInSamples(audioSection.getPosition(),audioSectionNext.getPosition());
				}
			}
		};
	}

	public Action createPopupEditStartPositionAction(JTable jTable) {
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.EDIT_START,AbstractAction.class)) {

			{
				putValue(Action.NAME, "edit start position");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				int contextRow = jTable.getSelectedRow();
				if (contextRow < 0) return;

				//int modelRow = table.convertRowIndexToModel(contextRow);
				AudioCutterModel audioCutterModel=controller.getModel();
				ArrayList<AudioSectionModel> audioSections=audioCutterModel.getAudioSections();
				AudioSectionModel audioSection=audioSections.get(contextRow);
				int startpositionOfSelectedSection=audioSection.getPosition();
				int startSelection=startpositionOfSelectedSection-50000;
				if(startSelection<0) {
					startSelection=0;
				}
				int endSelection=startpositionOfSelectedSection+50000;
				if(endSelection>audioCutterModel.getAudiodataLengthInSamples()) {
					endSelection=audioCutterModel.getAudiodataLengthInSamples();
				}
				audioCutterModel.setSelectionRangeInSamples(startSelection,endSelection);
				audioCutterModel.setViewRangeInSamples(startSelection,endSelection);
			}
		};
	}

	public Action createPopupEditEndPositionAction(JTable jTable) {
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.EDIT_END,AbstractAction.class)) {

			{
				putValue(Action.NAME, "edit end position");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				int contextRow = jTable.getSelectedRow();
				if (contextRow < 0) return;

				//int modelRow = table.convertRowIndexToModel(contextRow);
				AudioCutterModel audioCutterModel=controller.getModel();
				ArrayList<AudioSectionModel> audioSections=audioCutterModel.getAudioSections();
				AudioSectionModel audioSection=audioSections.get(contextRow+1);
				int startpositionOfSelectedSection=audioSection.getPosition();
				int startSelection=startpositionOfSelectedSection-50000;
				if(startSelection<0) {
					startSelection=0;
				}
				int endSelection=startpositionOfSelectedSection+50000;
				if(endSelection>audioCutterModel.getAudiodataLengthInSamples()) {
					endSelection=audioCutterModel.getAudiodataLengthInSamples();
				}
				audioCutterModel.setSelectionRangeInSamples(startSelection,endSelection);
				audioCutterModel.setViewRangeInSamples(startSelection,endSelection);
			}
		};
	}

	public Action createPopupDeleteLeftCutpointAction(JTable jTable) {
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.DELETE_LEFT_CUTPOINT,AbstractAction.class)) {

			{
				putValue(Action.NAME, "delete left cutpoint");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				int contextRow = jTable.getSelectedRow();
				if (contextRow < 0) return;

				//int modelRow = table.convertRowIndexToModel(contextRow);
				AudioCutterModel audioCutterModel=controller.getModel();
				ArrayList<AudioSectionModel> audioSections=audioCutterModel.getAudioSections();
				audioSections.remove(contextRow);
				audioCutterModel.setAudioSections(audioSections);
				contextRow=jTable.convertRowIndexToView(contextRow-1);
				jTable.setRowSelectionInterval(contextRow, contextRow);;
			}
		};
	}

	public Action createPopupDeleteRightCutpointAction(JTable jTable) {
		return new AbstractAction(null,IconProvider.getFlatSvgIcon(Icons.DELETE_RIGHT_CUTPOINT,AbstractAction.class)) {

			{
				putValue(Action.NAME, "delete right cutpoint");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				int contextRow = jTable.getSelectedRow();
				if (contextRow < 0) return;

				//int modelRow = table.convertRowIndexToModel(contextRow);
				AudioCutterModel audioCutterModel=controller.getModel();
				AudioSectionModel selectedAudioSection=audioCutterModel.getAudioSection(contextRow);
				AudioSectionModel nextAudioSection=audioCutterModel.getAudioSection(contextRow+1);
				ArrayList<AudioSectionModel> audioSections=audioCutterModel.getAudioSections();
				nextAudioSection.setPosition(selectedAudioSection.getPosition());
				audioSections.remove(contextRow);
				audioCutterModel.setAudioSections(audioSections);
			}
		};
	}


}
