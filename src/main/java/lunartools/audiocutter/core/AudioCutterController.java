package lunartools.audiocutter.core;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.SwingTools;
import lunartools.audiocutter.common.action.ActionFactory;
import lunartools.audiocutter.common.model.AudioSectionModel;
import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.common.service.AudioPlayer;
import lunartools.audiocutter.common.ui.Dialogs;
import lunartools.audiocutter.core.controller.MediaController;
import lunartools.audiocutter.core.controller.ProjectController;
import lunartools.audiocutter.core.model.StatusMessage;
import lunartools.audiocutter.core.service.AutoCutWorker;
import lunartools.audiocutter.core.service.CreateCueSheetWorker;
import lunartools.audiocutter.core.service.CutMediaFileWorker;
import lunartools.audiocutter.core.service.DetermineFFmpegVersionWorker;
import lunartools.audiocutter.core.service.ProjectService;
import lunartools.audiocutter.core.view.FileDropHandler;
import lunartools.audiocutter.core.view.StatusPanel;
import lunartools.audiocutter.infrastructure.config.AudioCutterSettings;
import lunartools.audiocutter.projectfile.ProjectFileFilter;
import lunartools.progressdialog.ProgressDialog;
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
		audioPlayer=AudioPlayer.getInstance(model,this);
		audioPlayer.start();
		if(model.getStatusMessage()==null) {
			model.setStatusMessage(new StatusMessage(StatusMessage.Type.INFO,"ready"));
		}
	}

	public void updateModelChanges(Object object) {
		if(object==SimpleEvents.EXIT) {
			exit();
		}else if(object==SimpleEvents.MODEL_MEDIAFILECHANGED) {
//			action_ReadMediaFile();
		}else if(object==SimpleEvents.MODEL_FFMPEGEXECUTABLESELECTED) {
			DetermineFFmpegVersionWorker worker=new DetermineFFmpegVersionWorker(model,this);
			worker.execute();
		}else if(object==SimpleEvents.MODEL_ZOOMCHANGED){
			calculateZoom();
			view.refreshGui();
		}else if(object==SimpleEvents.MODEL_PROJECTDIRTCHANGED){
			view.refreshGui();
		}
	}

	private void calculateZoom() {
		int zoom=model.getZoom();
		int audiodataLengthInSamples=model.getAudiodataLengthInSamples();
		if(zoom==0) {
			model.setViewRangeInSamples(0,audiodataLengthInSamples);
			return;
		}
		int viewWidth=model.getAudiodataViewWidth();
		int viewStartInSamples=model.getViewStartInSamples();
		int viewEndInSamples=model.getViewEndInSamples();
		int viewDeltaInSamples=viewEndInSamples-viewStartInSamples;
		int rangeMax=audiodataLengthInSamples-(viewWidth>>2);//maximum zoom means 1 sample takes 4 pixel on screen

		zoom=AudioCutterModel.ZOOM_MAX-zoom;
		zoom=zoom*zoom;
		double zoomFactor=(double)zoom/AudioCutterModel.ZOOM_MAX;

		int cursor=model.getCursorPositionSampleNumber();
		int viewDeltaNew=(int)(zoomFactor*rangeMax/(double)AudioCutterModel.ZOOM_MAX);
		viewDeltaNew+=viewWidth>>2;//zooming to 1 sample makes no sense, add viewWidth/4 so that on maximum zoom 1 sample takes 4 pixel on screen

		int deltaDelta=viewDeltaInSamples-viewDeltaNew;
		viewStartInSamples+=deltaDelta>>1;
		viewEndInSamples-=deltaDelta>>1;
		if(viewStartInSamples<0) {
			viewStartInSamples=0;
		}
		if(viewEndInSamples>=audiodataLengthInSamples) {
			viewEndInSamples=audiodataLengthInSamples-1;
		}

		if(cursor!=0) {
			viewDeltaNew=viewEndInSamples-viewStartInSamples;
			viewStartInSamples=cursor-(viewDeltaNew>>1);
			viewEndInSamples=cursor+(viewDeltaNew>>1);

		}
		if(viewStartInSamples<0) {
			viewEndInSamples-=viewStartInSamples;
			viewStartInSamples=0;
		}
		if(viewEndInSamples>=audiodataLengthInSamples) {
			viewEndInSamples=audiodataLengthInSamples-1;
		}
		model.setViewRangeInSamples(viewStartInSamples,viewEndInSamples);
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
		AudioPlayer.getInstance().action_stop();
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
