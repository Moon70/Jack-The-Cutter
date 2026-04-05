package lunartools.audiocutter.core;

import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.SwingTools;
import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.common.service.AudioPlayer;
import lunartools.audiocutter.common.ui.Dialogs;
import lunartools.audiocutter.core.controller.MediaController;
import lunartools.audiocutter.core.controller.ProjectController;
import lunartools.audiocutter.core.model.StatusMessage;
import lunartools.audiocutter.core.service.DetermineFFmpegVersionWorker;
import lunartools.audiocutter.core.view.FileDropHandler;
import lunartools.audiocutter.core.view.ScrollbarsPanel;
import lunartools.audiocutter.infrastructure.config.AudioCutterSettings;
import lunartools.swing.HasParentFrame;

public class AudioCutterController implements HasParentFrame,FileDropHandler{
	private static Logger logger = LoggerFactory.getLogger(AudioCutterController.class);
	private AudioCutterModel model;
	private AudioCutterView view;
	private ProjectController projectController;
	private MediaController mediaController;
	private AudioPlayer audioPlayer;
	private volatile boolean shutdownInProgress;
	private volatile int busyCount;
	private boolean disableScrollbarMoveEvent;
	private boolean disableScrollbarZoomEvent;

	public AudioCutterController(
			AudioCutterModel model,
			AudioCutterView view,
			ProjectController projectController,
			MediaController mediaController
			) {
		this.model=Objects.requireNonNull(model);
		this.view=Objects.requireNonNull(view);
		this.projectController=Objects.requireNonNull(projectController);
		this.mediaController=Objects.requireNonNull(mediaController);

		AudioCutterSettings settings=AudioCutterSettings.getInstance();
		this.model.addChangeListener(this::updateModelChanges);
		int sectionTableWidth=settings.getInt(AudioCutterSettings.VIEW_SECTIONTABLE_WIDTH);
		model.setSectionTableWidth(sectionTableWidth);
		Rectangle frameBounds=SwingTools.fixScreenBounds(settings.getRectangle(AudioCutterSettings.VIEW_BOUNDS, AudioCutterModel.getDefaultFrameBounds()),AudioCutterModel.getDefaultFrameSize());
		int horizontalDividerPosition=frameBounds.width-sectionTableWidth;
		model.setAudiodataViewWidth(horizontalDividerPosition);
		model.setFrameBounds(frameBounds);
		view.getJFrame().setBounds(frameBounds);
		model.setFFmpegExecutablePath(settings.getString(AudioCutterSettings.FFMPEG_PATH,null));
		model.setRecentMediaFilePaths(settings.getStringlist(AudioCutterSettings.RECENT_MEDIA_PATHS));
		model.setRecentProjectFilePaths(settings.getStringlist(AudioCutterSettings.RECENT_PROJECT_PATHS));

		view.getJFrame().addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent event){
				exit();
			}
		});

		ScrollbarsPanel scrollbarsPanel=view.getPanelLeft().getScrollbarsPanel();
		scrollbarsPanel.scrollbarMoveAddAdjustementistener(e -> {
			if(disableScrollbarMoveEvent) {
				return;
			}
			int value=e.getValue();
			int viewStartInSamples=model.getViewStartInSamples();
			int viewEndInSamples=model.getViewEndInSamples();
			int delta=viewEndInSamples-viewStartInSamples;
			model.setViewRangeInSamples(value, value+delta);
		});

		scrollbarsPanel.scrollbarZoomAddAdjustementistener(e -> {
			if(disableScrollbarZoomEvent) {
				return;
			}
			model.setZoom(e.getValue());
		});

	}

	@Override
	public void processDroppedFile(File file) {
		if(file.getName().toLowerCase().endsWith(".project")) {
			projectController.openProjectWithConfirmation(file);
		}else {
			mediaController.openMediaFileWithConfirmation(file);
		}
	}


	public void openGUI() {
		view.getJFrame().setVisible(true);
		if(model.getStatusMessage()==null) {
			model.setStatusMessage(new StatusMessage(StatusMessage.Type.INFO,"ready"));
		}
	}

	public void updateModelChanges(Object object) {
		if(object==SimpleEvents.EXIT) {
			exit();
		}else if(object==SimpleEvents.MODEL_MEDIAFILECHANGED) {
			//			action_ReadMediaFile();
		}else if(object==SimpleEvents.MODEL_AUDIODATACHANGED) {
			//			updateScrollbarMoveValues();
			view.getPanelLeft().getScrollbarsPanel().updateEnabledState();
		}else if(object==SimpleEvents.MODEL_FFMPEGEXECUTABLESELECTED) {
			DetermineFFmpegVersionWorker worker=new DetermineFFmpegVersionWorker(model,this);
			worker.execute();
		}else if(object==SimpleEvents.MODEL_ZOOMRANGECHANGED) {
			ScrollbarsPanel scrollbarsPanel=view.getPanelLeft().getScrollbarsPanel();
			scrollbarsPanel.updateEnabledState();

//			JScrollBar scrollbarMove=scrollbarsPanel.getScrollbarMove();
			int zoomViewStartSample=model.getViewStartInSamples();
			int zoomViewEndSample=model.getViewEndInSamples();
			int value=zoomViewStartSample;
			int extent=zoomViewEndSample-zoomViewStartSample;
			int min=0;
			int max=model.getAudiodataLengthInSamples();

			try {
				disableScrollbarMoveEvent=true;
				scrollbarsPanel.setScrollbarMoveValues(value, extent, min, max);
			} finally {
				disableScrollbarMoveEvent=false;
			}
		}else if(object==SimpleEvents.MODEL_ZOOMFACTORCHANGED){
			ScrollbarsPanel scrollbarsPanel=view.getPanelLeft().getScrollbarsPanel();
			try {
				disableScrollbarZoomEvent=true;
				scrollbarsPanel.setScrollbarZoomValue(model.getZoom());
			} finally {
				disableScrollbarZoomEvent=false;
			}
		}else if(object==SimpleEvents.MODEL_PROJECTDIRTCHANGED){
			view.refreshGui();
		}
	}

	public void exit() {
		if(model.isProjectDirty() && Dialogs.userCanceledUnsavedChangesDialogue()){
			return;
		}
		shutdownInProgress=true;
		AudioCutterSettings settings=AudioCutterSettings.getInstance();
		settings.setRectangle(AudioCutterSettings.VIEW_BOUNDS, view.getJFrame().getBounds());
		settings.setInt(AudioCutterSettings.VIEW_SECTIONTABLE_WIDTH, model.getSectionTableWidth());
		if(model.hasAudiodata()) {
			settings.setString(AudioCutterSettings.AUDIOFILE_PATH, model.getMediaFile().getAbsolutePath());
		}
		File fileProject=model.getProjectFile();
		if(fileProject!=null){
			settings.setString(AudioCutterSettings.PROJECTFILE_PATH, fileProject.getAbsolutePath());
		}
		String ffmpegExecutable=model.getFFmpegExecutablePath();
		if(ffmpegExecutable!=null) {
			settings.setString(AudioCutterSettings.FFMPEG_PATH, ffmpegExecutable);
		}
		settings.setStringlist(AudioCutterSettings.RECENT_MEDIA_PATHS, model.getRecentMediaFilePaths());
		settings.setStringlist(AudioCutterSettings.RECENT_PROJECT_PATHS, model.getRecentProjectFilePaths());
		try {
			settings.saveSettings();
		} catch (IOException e) {
			logger.error("error while saving settings",e);
		}
		view.getJFrame().setVisible(false);
		view.getJFrame().dispose();
	}

	public boolean isShutdownInProgress() {
		return shutdownInProgress;
	}

	public void action_OpenRecentMediaFile(String path) {
		File mediafile=new File(path);
		if(!mediafile.exists()) {
			String message="Recent media file does not exist: "+mediafile;
			logger.info(message);
			model.setStatusMessage(new StatusMessage(StatusMessage.Type.INFO,message));
			return;
		}
		if(model.isProjectDirty() && Dialogs.userCanceledUnsavedChangesDialogue()){
			return;
		}
		AudioPlayer.getInstance().stopAudioPlayback();
		model.closeProject();
		model.setMediaFile(mediafile);
	}

	public void setBusy(boolean busy) {
		if(busy) {
			busyCount++;
		}else {
			busyCount--;
		}
		if(busyCount==0) {
			view.getJFrame().getRootPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}else {
			view.getJFrame().getRootPane().setCursor(new Cursor(Cursor.WAIT_CURSOR));
		}
	}

	public AudioCutterModel getModel() {
		return model;
	}

	public JFrame getJFrame() {
		return view.getJFrame();
	}

	public AudioPlayer getAudioPlayer() {
		return audioPlayer;
	}

	public void openAboutDialogue() {
		Dialogs.showAboutDialog(view.getJFrame());		
	}
}
