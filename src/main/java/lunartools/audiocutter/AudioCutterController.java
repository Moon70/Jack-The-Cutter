package lunartools.audiocutter;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.Settings;
import lunartools.audiocutter.gui.AudioCutterView;
import lunartools.audiocutter.gui.statuspanel.StatusController;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;
import lunartools.audiocutter.player.AudioPlayer;
import lunartools.audiocutter.projectfile.ProjectFileFilter;
import lunartools.audiocutter.projectfile.ProjectXmlService;
import lunartools.audiocutter.worker.CreateTempWavFileWorker;
import lunartools.audiocutter.worker.CutMediaFileWorker;
import lunartools.audiocutter.worker.DetermineFFmpegVersionWorker;
import lunartools.progressdialog.ProgressDialog;

public class AudioCutterController implements Observer{
	private static Logger logger = LoggerFactory.getLogger(AudioCutterController.class);
	private static final String SETTING__VIEW_BOUNDS = "ViewBounds";
	private static final String SETTING__VIEW_HORIZONTALDIVIDER = "HDivider";
	private static final String SETTING__AUDIOFILE_PATH = "AudiofilePath";
	private static final String SETTING__PROJECTFILE_PATH = "ProjectfilePath";
	private static final String SETTING__FFMPEG_PATH = "FFmpegExecutable";
	private Settings settings;
	private AudioCutterModel model;
	private AudioCutterView view;
	private StatusController statusController;
	private AudioPlayer audioPlayer;
	private volatile boolean shutdownInProgress;
	private volatile int busyCount;

	public AudioCutterController() {
		settings=new Settings(AudioCutterModel.PROGRAMNAME,AudioCutterModel.determineProgramVersion());
		model=new AudioCutterModel();
		model.addObserver(this);
		String s=settings.getString(SETTING__VIEW_HORIZONTALDIVIDER);
		model.setHorizontalDividerPosition(Integer.parseInt(s));
		model.setAudiodataViewWidth(Integer.parseInt(s));
		statusController=new StatusController(model);
		view=new AudioCutterView(model,this);
		view.setBounds(fixScreenBounds(settings.getRectangle(SETTING__VIEW_BOUNDS, AudioCutterModel.getDefaultFrameBounds()),AudioCutterModel.getDefaultFrameSize()));
		view.addObserver(this);
		model.setFFmpegExecutablePath(settings.getString(SETTING__FFMPEG_PATH));
	}

	private Rectangle fixScreenBounds(Rectangle screenBounds, Dimension defaultFrameSize) {
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();  
		GraphicsDevice[] graphicsDevices = graphicsEnvironment.getScreenDevices();
		int numberOfGraphicsDevices=graphicsDevices.length;
		for(int i=0;i<numberOfGraphicsDevices;i++){
			GraphicsDevice graphicsDevice=graphicsDevices[i];
			Rectangle graphicsDeviceBounds = graphicsDevice.getDefaultConfiguration().getBounds();
			if(
					screenBounds.x>=graphicsDeviceBounds.x &&
					screenBounds.y>=graphicsDeviceBounds.y &&
					screenBounds.x+screenBounds.width<=graphicsDeviceBounds.x+graphicsDeviceBounds.width &&
					screenBounds.y+screenBounds.height<=graphicsDeviceBounds.y+graphicsDeviceBounds.height
					) {
				return screenBounds;
			}
		}
		GraphicsDevice graphicsDevice=graphicsDevices[0];
		Rectangle graphicsDeviceBounds = graphicsDevice.getDefaultConfiguration().getBounds();
		int marginX=(graphicsDeviceBounds.width-defaultFrameSize.width)>>1;
					int marginY=(graphicsDeviceBounds.height-defaultFrameSize.height)>>1;
					return new Rectangle(graphicsDeviceBounds.x+marginX,graphicsDeviceBounds.y+marginY,defaultFrameSize.width,defaultFrameSize.height);
	}

	public void openGUI() {
		view.setVisible(true);
		audioPlayer=AudioPlayer.getInstance(model,this);
		audioPlayer.start();
		if(model.getStatusMessage()==null) {
			model.setStatusMessage(new StatusMessage(StatusMessage.Type.INFO,"ready"));
		}
	}

	@Override
	public void update(Observable observable, Object object) {
		if(object==SimpleEvents.EXIT) {
			exit();
		}else if(object==SimpleEvents.MODEL_MEDIAFILECHANGED) {
			action_ReadMediaFile();
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

	private void exit() {
		if(model.isProjectDirty() && userCanceledUnsavedChangesDialogue()){
			return;
		}
		shutdownInProgress=true;
		settings.setRectangle(SETTING__VIEW_BOUNDS, view.getBounds());
		settings.set(SETTING__VIEW_HORIZONTALDIVIDER, ""+model.getHorizontalDividerPosition());
		if(model.hasAudiodata()) {
			settings.set(SETTING__AUDIOFILE_PATH, model.getMediaFile().getAbsolutePath());
		}
		File fileProject=model.getProjectFile();
		if(fileProject!=null){
			settings.set(SETTING__PROJECTFILE_PATH, fileProject.getAbsolutePath());
		}
		String ffmpegExecutable=model.getFFmpegExecutablePath();
		if(ffmpegExecutable!=null) {
			settings.set(SETTING__FFMPEG_PATH, ffmpegExecutable);
		}
		try {
			settings.saveSettings();
		} catch (IOException e) {
			logger.error("error while saving settings",e);
		}
		view.setVisible(false);
		view.dispose();
	}

	public boolean isShutdownInProgress() {
		return shutdownInProgress;
	}

	private void action_ReadMediaFile() {
		if(!model.isFFmpegAvailable()) {
			model.setStatusMessage(new StatusMessage(StatusMessage.Type.WARNING,"please set path to FFmpeg executable"));
			return;
		}
		File mediafile=model.getMediaFile();
		if(mediafile==null) {
			return;
		}
		if(!mediafile.exists()) {
			JOptionPane.showMessageDialog(
					view,
					"File does not exist\n"+mediafile,
					AudioCutterModel.PROGRAMNAME,
					JOptionPane.ERROR_MESSAGE
					);
			return;
		}
		CreateTempWavFileWorker worker=new CreateTempWavFileWorker(model,this);
		ProgressDialog.executeWithProgresssDialog(view,AudioCutterModel.PROGRAMNAME, "Processing media file",worker);
	}

	public boolean userCanceledUnsavedChangesDialogue() {
		return JOptionPane.showOptionDialog(
				view,
				"Current project contains unsaved changes!",
				AudioCutterModel.PROGRAMNAME,
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null,
				null,
				null
				)!=JOptionPane.OK_OPTION;
	}

	public boolean userCanceledFolderNotEmptyDialogue() {
		return JOptionPane.showOptionDialog(
				view,
				"Selected folder is not empty!\nOK to proceed and overwrite files.",
				AudioCutterModel.PROGRAMNAME,
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null,
				null,
				null
				)!=JOptionPane.OK_OPTION;
	}

	private boolean userCanceledProjectFileExistsDialogue(File choosenProjectFile) {
		return JOptionPane.showConfirmDialog(
				view,
				"Project file already exists, OK to overwrite?\n"+choosenProjectFile.getAbsolutePath(),
				AudioCutterModel.PROGRAMNAME,
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null
				)!=JOptionPane.OK_OPTION;
	}

	public void action_OpenMediaFile() {
		if(model.isProjectDirty() && userCanceledUnsavedChangesDialogue()){
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
		File currentMediaFile=model.getMediaFile();
		if(currentMediaFile!=null) {
			fileChooser.setCurrentDirectory(currentMediaFile.getParentFile());
		}else {
			String filepath=settings.getString(SETTING__AUDIOFILE_PATH);
			if(filepath!=null && filepath.length()>0) {
				File lastAudioFile=new File(filepath);
				fileChooser.setCurrentDirectory(lastAudioFile);
			}
		}
		fileChooser.setDialogTitle("Select media file to load");
		fileChooser.setPreferredSize(new Dimension(800,(int)(800/AudioCutterModel.SECTIOAUREA)));
		if(fileChooser.showOpenDialog(view)==JFileChooser.APPROVE_OPTION) {
			File choosenMediaFile=fileChooser.getSelectedFile();
			AudioPlayer.getInstance().action_stop();
			model.closeProject();
			model.setMediaFile(choosenMediaFile);
		}else if(logger.isTraceEnabled()){
			logger.trace("Open media dialogue canceled");
		}
	}

	public void action_OpenProjectFile(){
		if(model.isProjectDirty() && userCanceledUnsavedChangesDialogue()){
			return;
		}
		final JFileChooser fileChooser=new JFileChooser() {
			public void updateUI() {
				putClientProperty("FileChooser.useShellFolder", Boolean.FALSE);
				super.updateUI();
			}
		};
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new ProjectFileFilter());
		File currentProjectFile=model.getProjectFile();
		if(currentProjectFile!=null) {
			fileChooser.setCurrentDirectory(currentProjectFile.getParentFile());
			fileChooser.setSelectedFile(currentProjectFile);
		}else if(settings.getString(SETTING__PROJECTFILE_PATH)!=null){
			File lastProjectFile=new File(settings.getString(SETTING__PROJECTFILE_PATH));
			fileChooser.setCurrentDirectory(lastProjectFile.getParentFile());
		}else {
			File currentMediaFile=model.getMediaFile();
			if(currentMediaFile!=null) {
				fileChooser.setCurrentDirectory(currentMediaFile.getParentFile());
			}
		}
		fileChooser.setDialogTitle("Select project file to load");
		fileChooser.setPreferredSize(new Dimension(800,(int)(800/AudioCutterModel.SECTIOAUREA)));
		if(fileChooser.showOpenDialog(view)==JFileChooser.APPROVE_OPTION) {
			File choosenProjectFile=fileChooser.getSelectedFile();
			String filename=choosenProjectFile.getName();
			if(!filename.toLowerCase().endsWith(".project")) {
				choosenProjectFile=new File(choosenProjectFile.getParentFile(),filename+".project");
			}
			if(!choosenProjectFile.exists()) {
				JOptionPane.showMessageDialog(
						view,
						"File does not exist\n"+choosenProjectFile,
						AudioCutterModel.PROGRAMNAME,
						JOptionPane.ERROR_MESSAGE
						);
				return;
			}
			processProjectFile(choosenProjectFile);
		}else if(logger.isTraceEnabled()){
			logger.trace("Open project dialogue canceled");
		}
	}

	public void processProjectFile(File projectFile) {
		action_CloseProject();
		try {
			new ProjectXmlService().loadProject(model, this,projectFile);
		} catch (Exception e) {
			String message="error loading project";
			logger.error(message,e);
			model.setStatusMessage(new StatusMessage(StatusMessage.Type.ERROR,message+": "+e.getMessage()));
		}
	}

	public void action_SaveProjectFileAs() {
		final JFileChooser fileChooser=new JFileChooser() {
			public void updateUI() {
				putClientProperty("FileChooser.useShellFolder", Boolean.FALSE);
				super.updateUI();
			}
		};
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new ProjectFileFilter());
		File currentProjectFile=model.getProjectFile();
		if(currentProjectFile!=null) {
			fileChooser.setCurrentDirectory(currentProjectFile.getParentFile());
			fileChooser.setSelectedFile(currentProjectFile);
		}else if(settings.getString(SETTING__PROJECTFILE_PATH)!=null){
			File lastProjectFile=new File(settings.getString(SETTING__PROJECTFILE_PATH));
			fileChooser.setCurrentDirectory(lastProjectFile.getParentFile());
		}else {
			File currentMediaFile=model.getMediaFile();
			if(currentMediaFile!=null) {
				fileChooser.setCurrentDirectory(currentMediaFile.getParentFile());
			}
		}
		fileChooser.setDialogTitle("Select project file to save");
		fileChooser.setPreferredSize(new Dimension(800,(int)(800/AudioCutterModel.SECTIOAUREA)));
		if(fileChooser.showSaveDialog(view)==JFileChooser.APPROVE_OPTION) {
			File choosenProjectFile=fileChooser.getSelectedFile();
			String filename=choosenProjectFile.getName();
			if(!filename.toLowerCase().endsWith(".project")) {
				choosenProjectFile=new File(choosenProjectFile.getParentFile(),filename+".project");
			}
			if(choosenProjectFile.exists() && userCanceledProjectFileExistsDialogue(choosenProjectFile)) {
				return;
			}
			model.setProjectFile(choosenProjectFile);
			action_SaveProjectFile();
		}
	}

	public void action_SaveProjectFile() {
		try {
			new ProjectXmlService().saveProject(model,this,model.getProjectFile());
		} catch (Exception e) {
			String message="error saving project";
			logger.error(message,e);
			model.setStatusMessage(new StatusMessage(StatusMessage.Type.ERROR,message+": "+e.getMessage()));
		}
	}

	public void action_CloseProject() {
		if(model.isProjectDirty() && userCanceledUnsavedChangesDialogue()){
			return;
		}
		audioPlayer.action_stop();
		model.closeProject();
	}

	public void action_CutMediaFile() {
		if(!model.isFFmpegAvailable()) {
			model.setStatusMessage(new StatusMessage(StatusMessage.Type.WARNING,"please set path to FFmpeg executable"));
			return;
		}

		final JFileChooser fileChooser=new JFileChooser() {
			public void updateUI() {
				putClientProperty("FileChooser.useShellFolder", Boolean.FALSE);
				super.updateUI();
			}
		};
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		File currentMediaFile=model.getMediaFile();
		File lastSectionsFolder=model.getSectionsFolder();

		if(lastSectionsFolder!=null) {
			fileChooser.setCurrentDirectory(lastSectionsFolder.getParentFile());
			fileChooser.setSelectedFile(lastSectionsFolder);
		}else {
			fileChooser.setCurrentDirectory(currentMediaFile.getParentFile());
		}
		File choosenSectionFolder=null;
		fileChooser.setDialogTitle("Select folder to save sections");
		fileChooser.setPreferredSize(new Dimension(800,(int)(800/AudioCutterModel.SECTIOAUREA)));
		if(fileChooser.showOpenDialog(view)==JFileChooser.APPROVE_OPTION) {
			choosenSectionFolder=fileChooser.getSelectedFile();
		}else if(logger.isTraceEnabled()){
			final String message="Select section folder dialogue canceled";
			logger.debug(message);
			model.setStatusMessage(new StatusMessage(StatusMessage.Type.INFO,message));
			return;
		}
		if(choosenSectionFolder.exists()) {
			if(choosenSectionFolder.list().length>0) {
				if(userCanceledFolderNotEmptyDialogue()) {
					final String message="save media sections canceled";
					logger.debug(message);
					model.setStatusMessage(new StatusMessage(StatusMessage.Type.INFO,message));
					return;
				}
			}
		}else {
			choosenSectionFolder.mkdirs();
		}
		model.setSectionsFolder(choosenSectionFolder);
		CutMediaFileWorker worker=new CutMediaFileWorker(model,this);
		ProgressDialog.executeWithProgresssDialog(view,AudioCutterModel.PROGRAMNAME, "Cutting media file","",worker);
	}

	public StatusController getStatusController() {
		return statusController;
	}

	public void setBusy(boolean busy) {
		if(busy) {
			busyCount++;
		}else {
			busyCount--;
		}
		if(busyCount==0) {
			view.getRootPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}else {
			view.getRootPane().setCursor(new Cursor(Cursor.WAIT_CURSOR));
		}
	}

}
