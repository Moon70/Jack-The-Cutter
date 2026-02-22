package lunartools.audiocutter.core;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.ChangeListenerSupport;
import lunartools.SwingTools;
import lunartools.audiocutter.common.model.AudioSectionModel;
import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.core.model.StatusMessage;

public class AudioCutterModel implements ChangeListenerSupport{
	private static Logger logger = LoggerFactory.getLogger(AudioCutterModel.class);
	public static final String PROGRAM_NAME = "Jack-The-Cutter";
	private static final String PROGRAM_VERSION=SwingTools.determineProgramVersion();
	private static final String PROGRAM_NAME_AND_VERSION=PROGRAM_NAME+" "+PROGRAM_VERSION;
	private final List<ChangeListener> listeners = new ArrayList<>();

	
	public static final int DEFAULT_FRAME_WIDTH=1290;
	public static final int DEFAULT_FRAME_HEIGHT=(int)(DEFAULT_FRAME_WIDTH/SwingTools.SECTIOAUREA);
	private Rectangle frameBounds=new Rectangle(0,0,DEFAULT_FRAME_WIDTH,DEFAULT_FRAME_HEIGHT);
	private int verticalDividerPosition=24;

	public static final int ZOOM_MIN=0;
	public static final int ZOOM_MAX=100;

	private String ffmpegExecutablePath;
	private String ffmpegVersion;
	private File mediaFile;
	private ArrayList<String> recentMediaFilePaths;
	private File wavFile;
	private File projectFile;
	private ArrayList<String> recentProjectFilePaths;
	private boolean projectIsDirty;
	private File sectionsFolder;
	private byte[] audiodata;

	private File cueSheetFile;

	private int viewStartInSamples;
	private int viewEndInSamples;

	private int selectionStartInSamples;
	private int selectionEndInSamples;

	private ArrayList<AudioSectionModel> audioSections=new ArrayList<>();

	private int audiodataViewWidth;
	private int sectionTableWidth;

	private int cursorPositionSampleNumber;
	private int mousePositionSampleNumber;

	private int playPositionSampleNumber;
	private int playPosPreviousSample;

	private int zoom;

	int audioPlayerBufferSize=4096*2;

	private boolean amplitudeZoom;

	private int selectedSection=-1;

	private StatusMessage statusMessage;

	public static String getProgramVersion() {
		return PROGRAM_VERSION;
	}

	public static String getProgramNameAndVersion() {
		return PROGRAM_NAME_AND_VERSION;
	}

	@Override
	public List<ChangeListener> getListeners() {
		return listeners;
	}
	
	public int getHorizontalDividerPosition() {
		return frameBounds.width-sectionTableWidth;
	}

	public int getVerticalDividerPosition() {
		return verticalDividerPosition;
	}

	public void setVerticalDividerPosition(int verticalDividerPosition) {
		this.verticalDividerPosition = verticalDividerPosition;
	}

	public String getFFmpegExecutablePath() {
		return ffmpegExecutablePath;
	}

	public void setFFmpegExecutablePath(String ffmpegExecutablePath) {
		this.ffmpegExecutablePath = ffmpegExecutablePath;
		notifyListeners(SimpleEvents.MODEL_FFMPEGEXECUTABLESELECTED);
	}

	public String getFFmpegVersion() {
		return ffmpegVersion;
	}

	public void setFFmpegVersion(String ffmpegVersion) {
		this.ffmpegVersion = ffmpegVersion;
		setStatusMessage(new StatusMessage(StatusMessage.Type.FFMPEGVERSION, ffmpegVersion));
	}

	public boolean isFFmpegAvailable() {
		return ffmpegVersion!=null && ffmpegVersion.length()!=0;
	}

	public File getMediaFile() {
		return mediaFile;
	}

	public void setMediaFile(File mediaFile) {
		this.mediaFile = mediaFile;
		if(mediaFile!=null) {
			addToRecentMediaFileList(mediaFile.getAbsolutePath());
		}
		notifyListeners(SimpleEvents.MODEL_MEDIAFILECHANGED);
	}

	private void addToRecentMediaFileList(String absolutePath) {
		final int maxNumberOfEntries=10;
		recentMediaFilePaths.add(0, absolutePath);
		for(int i=recentMediaFilePaths.size()-1;i>0;i--) {
			if(recentMediaFilePaths.get(i).equalsIgnoreCase(absolutePath)) {
				recentMediaFilePaths.remove(i);
			}
		}
		for(int i=recentMediaFilePaths.size()-1;i>=maxNumberOfEntries;i--) {
			recentMediaFilePaths.remove(i);
		}
		notifyListeners(SimpleEvents.MODEL_RECENTMEDIAFILESLISTCHANGED);
	}

	public File getWavFile() {
		return wavFile;
	}

	public void setWavFile(File wavFile) {
		this.wavFile = wavFile;
	}

	public File getProjectFile() {
		return projectFile;
	}

	public void setProjectFile(File projectFile) {
		this.projectFile = projectFile;
		if(projectFile!=null) {
			addToRecentProjectFileList(projectFile.getAbsolutePath());
		}
		setProjectDirty(false);
	}

	private void addToRecentProjectFileList(String absolutePath) {
		final int maxNumberOfEntries=10;
		recentProjectFilePaths.add(0, absolutePath);
		for(int i=recentProjectFilePaths.size()-1;i>0;i--) {
			if(recentProjectFilePaths.get(i).equalsIgnoreCase(absolutePath)) {
				recentProjectFilePaths.remove(i);
			}
		}
		for(int i=recentProjectFilePaths.size()-1;i>=maxNumberOfEntries;i--) {
			recentProjectFilePaths.remove(i);
		}
		notifyListeners(SimpleEvents.MODEL_RECENTPROJECTSFILELISTCHANGED);
	}

	public File getCueSheetFile() {
		return cueSheetFile;
	}

	public void setCueSheetFile(File cueSheetFile) {
		this.cueSheetFile = cueSheetFile;
	}

	public boolean isProjectDirty() {
		return projectIsDirty;
	}

	public void setProjectDirty(boolean projectIsDirty) {
		this.projectIsDirty=projectIsDirty;
		notifyListeners(SimpleEvents.MODEL_PROJECTDIRTCHANGED);
	}

	public File getSectionsFolder() {
		return sectionsFolder;
	}

	public void setSectionsFolder(File sectionsFolder) {
		this.sectionsFolder = sectionsFolder;
	}

	public void setAudiodata(byte[] audiodata) {
		this.audiodata=audiodata;
		if(audiodata!=null) {
			this.viewStartInSamples=0;
			this.viewEndInSamples=getAudiodataLengthInSamples();
		}else {
			setAudioSections(null);
			selectionStartInSamples=0;
			selectionEndInSamples=0;
		}
		notifyListeners(SimpleEvents.MODEL_AUDIODATACHANGED);
	}

	public byte[] getAudiodata() {
		return audiodata;
	}

	public boolean hasAudiodata() {
		return audiodata!=null;
	}

	public int getAudiodataLengthInSamples() {
		return audiodata==null?0:audiodata.length>>2;
	}

	public int getViewStartInSamples() {
		return viewStartInSamples;
	}

	public void setViewStartInSamples(int viewStartInSamples) {
		this.viewStartInSamples = viewStartInSamples;
		notifyListeners(SimpleEvents.MODEL_ZOOMRANGECHANGED);
	}

	public int getViewEndInSamples() {
		return viewEndInSamples;
	}

	public void setViewEndInSamples(int viewEndInSamples) {
		this.viewEndInSamples = viewEndInSamples;
		notifyListeners(SimpleEvents.MODEL_ZOOMRANGECHANGED);
	}

	public void setViewRangeInSamples(int viewStartInSamples,int viewEndInSamples) {
		calculateZoom(viewStartInSamples,viewEndInSamples);
		this.viewStartInSamples = viewStartInSamples;
		this.viewEndInSamples = viewEndInSamples;
		notifyListeners(SimpleEvents.MODEL_ZOOMRANGECHANGED);
	}

	public int getSelectionStartInSamples() {
		return selectionStartInSamples;
	}

	public void setSelectionStartInSamples(int selectionStartInSamples) {
		this.selectionStartInSamples = selectionStartInSamples;
		notifyListeners(SimpleEvents.MODEL_SELECTIONCHANGED);
	}

	public int getSelectionEndInSamples() {
		return selectionEndInSamples;
	}

	public void setSelectionEndInSamples(int selectionEndInSamples) {
		this.selectionEndInSamples = selectionEndInSamples;
		notifyListeners(SimpleEvents.MODEL_SELECTIONCHANGED);
	}

	public void setSelectionRangeInSamples(int selectionStartInSamples,int selectionEndInSamples) {
		this.selectionStartInSamples = selectionStartInSamples;
		this.selectionEndInSamples = selectionEndInSamples;
		notifyListeners(SimpleEvents.MODEL_SELECTIONCHANGED);
	}

	public boolean hasSelection() {
		return selectionEndInSamples!=0;
	}

	public ArrayList<AudioSectionModel> getAudioSections() {
		return audioSections;
	}

	public void setAudioSections(ArrayList<AudioSectionModel> audioSections) {
		if(audioSections==null) {
			this.audioSections=new ArrayList<>();
			
			//Thread.dumpStack();
			
			
		}else {
			this.audioSections = audioSections;
			boolean oldDirt=projectIsDirty;
			projectIsDirty=true;
			if(!oldDirt) {
				notifyListeners(SimpleEvents.MODEL_PROJECTDIRTCHANGED);
			}
		}
		notifyListeners(SimpleEvents.MODEL_AUDIOSECTIONSCHANGED);
	}

	public boolean hasAudioSections() {
		return audioSections.size()!=0;
	}

	public int getAudioSectionsSize() {
		return audioSections.size();
	}

	public AudioSectionModel getAudioSection(int index) {
		if(index>=audioSections.size()) {
			return null;
		}
		return audioSections.get(index);
	}

	public void setAudioSectionPosition(int audioSectionIndex, int position) {
		this.audioSections.get(audioSectionIndex).setPosition(position);
		projectIsDirty=true;
		notifyListeners(SimpleEvents.MODEL_AUDIOSECTIONSCHANGED);
	}

	public int getSelectedAudioSection() {
		return selectedSection;
	}

	public void setSelectedAudioSection(int selectedSection) {
		this.selectedSection = selectedSection;
		notifyListeners(SimpleEvents.MODEL_SELECTEDSECTIONSCHANGED);
	}

	public int getAudiodataViewWidth() {
		return audiodataViewWidth;
	}

	public void setAudiodataViewWidth(int audiodataViewWidth) {
		this.audiodataViewWidth = audiodataViewWidth;
	}

	public int getSectionTableWidth() {
		return sectionTableWidth;
	}

	public void setSectionTableWidth(int sectionTableWidth) {
		this.sectionTableWidth = sectionTableWidth;
	}

	public int getCursorPositionSampleNumber() {
		return cursorPositionSampleNumber;
	}

	public void setCursorPositionSampleNumber(int cursorPositionSampleNumber) {
		this.cursorPositionSampleNumber = cursorPositionSampleNumber;
		notifyListeners(SimpleEvents.MODEL_CURSORCHANGED);
	}

	public int getMousePositionSampleNumber() {
		return mousePositionSampleNumber;
	}

	public void setMousePositionSampleNumber(int mousePositionSampleNumber) {
		this.mousePositionSampleNumber = mousePositionSampleNumber;
		notifyListeners(SimpleEvents.MODEL_MOUSESAMPLENUMBERCHANGED);
	}

	public int getPlayPositionSampleNumber() {
		return playPositionSampleNumber;
	}

	public void setPlayPositionSampleNumber(int playPositionSampleNumber) {
		this.playPositionSampleNumber = playPositionSampleNumber;
		if(playPositionSampleNumber!=playPosPreviousSample) {
			notifyListeners(SimpleEvents.MODEL_PLAYCURSORCHANGED);
			playPosPreviousSample=playPositionSampleNumber;
		}
	}

	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
		notifyListeners(SimpleEvents.MODEL_ZOOMCHANGED);
	}

	public int getAudioPlayerBufferSize() {
		return audioPlayerBufferSize;
	}

	public void setAudioPlayerBufferSize(int audioPlayerBufferSize) {
		this.audioPlayerBufferSize = audioPlayerBufferSize;
		notifyListeners(SimpleEvents.MODEL_AUDIOPLAYERBUFFERSIZECHANGED);
	}

	public boolean isAmplitudeZoom() {
		return amplitudeZoom;
	}

	public void setAmplitudeZoom(boolean amplitudeZoom) {
		this.amplitudeZoom = amplitudeZoom;
		notifyListeners(SimpleEvents.MODEL_ZOOMCHANGED);
	}

	private void calculateZoom(int viewStartInSamples,int viewEndInSamples) {
		int delta=viewEndInSamples-viewStartInSamples;

		int rangeMax=getAudiodataLengthInSamples()-(getAudiodataViewWidth()>>2);//maximum zoom means 1 sample takes 4 pixel on screen
		for(int i=AudioCutterModel.ZOOM_MIN;i<=AudioCutterModel.ZOOM_MAX;i++) {
			int zoom=AudioCutterModel.ZOOM_MAX-i;
			zoom=zoom*zoom;
			double zoomFactor=(double)zoom/AudioCutterModel.ZOOM_MAX;
			int viewDeltaNew=(int)(zoomFactor*rangeMax/(double)AudioCutterModel.ZOOM_MAX);
			if(delta>=viewDeltaNew) {
				this.zoom=i;
				break;
			}
		}
	}

	public void closeProject() {
		playPositionSampleNumber=0;
		cursorPositionSampleNumber=0;
		selectedSection=-1;
		viewStartInSamples=0;
		selectionStartInSamples=0;
		selectionEndInSamples=0;
		setAudioSections(null);
		setProjectFile(null);
		setMediaFile(null);
		setAudiodata(null);
		setCueSheetFile(null);
		setZoom(0);
	}

	public StatusMessage getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(StatusMessage statusMessage) {
		if(statusMessage==null) {
			return;
		}
		this.statusMessage=statusMessage;
		notifyListeners(statusMessage);
	}

	public static Dimension getDefaultFrameSize() {
		return new Dimension(DEFAULT_FRAME_WIDTH,DEFAULT_FRAME_HEIGHT);
	}

	public static Rectangle getDefaultFrameBounds() {
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();  
		GraphicsDevice defaultGraphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
		Rectangle graphicsDeviceBounds = defaultGraphicsDevice.getDefaultConfiguration().getBounds();
		int marginX=(graphicsDeviceBounds.width-DEFAULT_FRAME_WIDTH)>>1;
		int marginY=(graphicsDeviceBounds.height-DEFAULT_FRAME_HEIGHT)>>1;
		return new Rectangle(graphicsDeviceBounds.x+marginX,graphicsDeviceBounds.y+marginY,DEFAULT_FRAME_WIDTH,DEFAULT_FRAME_HEIGHT);
	}

	public Rectangle getFrameBounds() {
		return frameBounds;
	}

	public void setFrameBounds(Rectangle frameBounds) {
		this.frameBounds = frameBounds;
		notifyListeners(SimpleEvents.MODEL_FRAMESIZECHANGED);
	}

	public ArrayList<String> getRecentMediaFilePaths() {
		return recentMediaFilePaths;
	}

	public void setRecentMediaFilePaths(ArrayList<String> recentMediaFilePaths) {
		this.recentMediaFilePaths = recentMediaFilePaths;
		notifyListeners(SimpleEvents.MODEL_RECENTMEDIAFILESLISTCHANGED);
	}

	public ArrayList<String> getRecentProjectFilePaths() {
		return recentProjectFilePaths;
	}

	public void setRecentProjectFilePaths(ArrayList<String> recentProjectFilePaths) {
		this.recentProjectFilePaths = recentProjectFilePaths;
		notifyListeners(SimpleEvents.MODEL_RECENTPROJECTSFILELISTCHANGED);
	}

}
