package lunartools.audiocutter.gui;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import lunartools.audiocutter.AudioCutterController;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.SimpleEvents;
import lunartools.audiocutter.gui.preferencespanel.PreferencesController;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;

public class MenubarController implements ActionListener, Observer{
	private static final String ACTIONCOMMAND__OPEN_MEDIAFILE = 	"openMediafile";
	private static final String ACTIONCOMMAND__OPEN_PROJECT = 		"loadProject";
	private static final String ACTIONCOMMAND__SAVE_PROJECTAS = 	"saveProjectAs";
	private static final String ACTIONCOMMAND__SAVE_PROJECT = 		"saveProject";
	private static final String ACTIONCOMMAND__CLOSE_PROJECT = 		"closeProject";
	private static final String ACTIONCOMMAND__CUT_MEDIAFILE = 		"cutMediaFile";
	private static final String ACTIONCOMMAND__PREFERENCES = 		"preferences";
	private static final String ACTIONCOMMAND__AUTOCUT = 			"autocut";
	private static final String ACTIONCOMMAND__CREATE_CUESHEET =	"createCuesheet";
	private static final String ACTIONCOMMAND__ABOUT = 				"about";
	private static final String ACTIONCOMMAND__EXIT = 				"exit";
	private static final String NAME__RECENTMEDIAFILE = 			"RecentMediaFile";
	private static final String NAME__RECENTPROJECTFILE = 			"RecentProjectFile";

	private AudioCutterModel model;
	private AudioCutterController controller;
	private AudioCutterView view;

	private MenuItem menuItem_OpenMediaFile;
	private MenuItem menuItem_LoadProject;
	private MenuItem menuItem_SaveProjectAs;
	private MenuItem menuItem_SaveProject;
	private MenuItem menuItem_CloseProject;
	private MenuItem menuItem_ProcessFile;
	private MenuItem menuItem_Preferences;

	private MenuItem menuItem_Autocut;
	private MenuItem menuItem_CreateCuesheet;

	private Menu menuRecentMediaFiles=new Menu("Recent media files");
	private Menu menuRecentProjectFiles=new Menu("Recent project files");

	public MenubarController(AudioCutterModel model,AudioCutterController controller,AudioCutterView view) {
		this.model=model;
		this.controller=controller;
		this.view=view;
		model.addObserver(this);
	}

	public MenuBar createMenubar() {
		MenuBar menuBar=new MenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createToolsMenu());
		menuBar.add(createHelpMenu());
		refresh();
		return menuBar;
	}

	private Menu createFileMenu(){
		Menu menu=new Menu("File");
		menu.addActionListener(this);

		menuItem_OpenMediaFile=new MenuItem("Open media file");
		menuItem_OpenMediaFile.setActionCommand(ACTIONCOMMAND__OPEN_MEDIAFILE);
		menu.add(menuItem_OpenMediaFile);

		menu.add(menuRecentMediaFiles);

		menuItem_LoadProject=new MenuItem("Open project");
		menuItem_LoadProject.setActionCommand(ACTIONCOMMAND__OPEN_PROJECT);
		menu.add(menuItem_LoadProject);

		menu.add(menuRecentProjectFiles);

		menuItem_SaveProjectAs=new MenuItem("Save project as");
		menuItem_SaveProjectAs.setActionCommand(ACTIONCOMMAND__SAVE_PROJECTAS);
		menu.add(menuItem_SaveProjectAs);

		menuItem_SaveProject=new MenuItem("Save project");
		menuItem_SaveProject.setActionCommand(ACTIONCOMMAND__SAVE_PROJECT);
		menu.add(menuItem_SaveProject);

		menuItem_CloseProject=new MenuItem("Close project");
		menuItem_CloseProject.setActionCommand(ACTIONCOMMAND__CLOSE_PROJECT);
		menu.add(menuItem_CloseProject);

		menuItem_ProcessFile=new MenuItem("Cut media file");
		menuItem_ProcessFile.setActionCommand(ACTIONCOMMAND__CUT_MEDIAFILE);
		menu.add(menuItem_ProcessFile);

		menuItem_Preferences=new MenuItem("Preferences");
		menuItem_Preferences.setActionCommand(ACTIONCOMMAND__PREFERENCES);
		menu.add(menuItem_Preferences);

		MenuItem menuItem=new MenuItem("Exit");
		menuItem.setActionCommand(ACTIONCOMMAND__EXIT);
		menu.add(menuItem);

		return menu;
	}

	private Menu createToolsMenu() {
		Menu menu=new Menu("Tools");
		menu.addActionListener(this);

		menuItem_Autocut=new MenuItem("Auto cut");
		menuItem_Autocut.setActionCommand(ACTIONCOMMAND__AUTOCUT);
		menu.add(menuItem_Autocut);

		menuItem_CreateCuesheet=new MenuItem("Create CUE Sheet");
		menuItem_CreateCuesheet.setActionCommand(ACTIONCOMMAND__CREATE_CUESHEET);
		menu.add(menuItem_CreateCuesheet);
		return menu;
	}

	private Menu createHelpMenu(){
		Menu menu=new Menu("?");
		MenuItem menuItem=new MenuItem("About "+AudioCutterModel.PROGRAMNAME);
		menuItem.setActionCommand(ACTIONCOMMAND__ABOUT);
		menu.add(menuItem);
		menu.addActionListener(this);
		return menu;
	}

	@Override
	public void actionPerformed(ActionEvent event){
		String actionCommand=event.getActionCommand();
		if(actionCommand.equals(ACTIONCOMMAND__EXIT)){
			view.sendMessage(SimpleEvents.EXIT);
		}else if(actionCommand.equals(ACTIONCOMMAND__OPEN_MEDIAFILE)){
			controller.action_OpenMediaFile();
		}else if(actionCommand.equals(ACTIONCOMMAND__OPEN_PROJECT)){
			controller.action_OpenProjectFile();
		}else if(actionCommand.equals(ACTIONCOMMAND__SAVE_PROJECTAS)){
			controller.action_SaveProjectFileAs();
		}else if(actionCommand.equals(ACTIONCOMMAND__SAVE_PROJECT)){
			controller.action_SaveProjectFile();
		}else if(actionCommand.equals(ACTIONCOMMAND__CLOSE_PROJECT)){
			controller.action_CloseProject();
		}else if(actionCommand.equals(ACTIONCOMMAND__CUT_MEDIAFILE)){
			controller.action_CutMediaFile();
		}else if(actionCommand.equals(ACTIONCOMMAND__PREFERENCES)){
			new PreferencesController(model).editPreferences(view);
		}else if(actionCommand.equals(ACTIONCOMMAND__AUTOCUT)){
			controller.action_AutoCut();
		}else if(actionCommand.equals(ACTIONCOMMAND__CREATE_CUESHEET)){
			controller.action_CreateCueSheet();
		}else if(actionCommand.equals(ACTIONCOMMAND__ABOUT)){
			view.showMessageboxAbout();
		}
		Object object=event.getSource();
		if(object instanceof MenuItem) {
			MenuItem menuItem=(MenuItem)object;
			String name=menuItem.getName();
			if(name.equals(NAME__RECENTMEDIAFILE)) {
				controller.action_OpenRecentMediaFile(menuItem.getActionCommand());
			}else if(name.equals(NAME__RECENTPROJECTFILE)) {
				controller.action_OpenRecentProjectFile(menuItem.getActionCommand());
			}
		}
	}

	@Override
	public void update(Observable observable, Object object) {
		if(object==SimpleEvents.MODEL_AUDIODATACHANGED) {
			refresh();
		}else if(object==SimpleEvents.MODEL_PROJECTDIRTCHANGED) {
			refresh();
		}else if(object==SimpleEvents.MODEL_AUDIOSECTIONSCHANGED) {
			refresh();
		}else if(object instanceof StatusMessage) {
			StatusMessage statusMessage=(StatusMessage)object;
			if(statusMessage.getType()==StatusMessage.Type.FFMPEGVERSION) {
				refresh();
			}
		}else if(object==SimpleEvents.MODEL_RECENTMEDIAFILESLISTCHANGED) {
			updateRecentMediaFilesMenu();
		}else if(object==SimpleEvents.MODEL_RECENTPROJECTSFILELISTCHANGED) {
			updateRecentProjectFilesMenu();
		}
	}

	private void refresh() {
		boolean ffmpegAvailable=model.isFFmpegAvailable();
		boolean audiodataAvailable=model.hasAudiodata();
		boolean projectNameAvailable=model.getProjectFile()!=null;

		menuItem_OpenMediaFile.setEnabled(ffmpegAvailable);
		menuItem_LoadProject.setEnabled(ffmpegAvailable);

		menuItem_SaveProjectAs.setEnabled(audiodataAvailable&ffmpegAvailable);
		menuItem_SaveProject.setEnabled(audiodataAvailable&ffmpegAvailable&model.isProjectDirty()&projectNameAvailable);
		menuItem_CloseProject.setEnabled(audiodataAvailable&ffmpegAvailable);
		menuItem_ProcessFile.setEnabled(audiodataAvailable&ffmpegAvailable&model.hasAudioSections());

		menuItem_Autocut.setEnabled(audiodataAvailable);
		menuItem_CreateCuesheet.setEnabled(audiodataAvailable);
		menuRecentMediaFiles.setEnabled(menuRecentMediaFiles.getItemCount()>0);
		menuRecentProjectFiles.setEnabled(menuRecentProjectFiles.getItemCount()>0);
	}

	private void updateRecentMediaFilesMenu() {
		ArrayList<String> recentMediaFilePaths=model.getRecentMediaFilePaths();
		menuRecentMediaFiles.removeAll();
		for(int i=0;i<recentMediaFilePaths.size();i++) {
			String path=recentMediaFilePaths.get(i);
			MenuItem menuItem=new MenuItem(path);
			menuItem.setName(NAME__RECENTMEDIAFILE);
			menuItem.setActionCommand(path);
			menuItem.addActionListener(this);
			menuRecentMediaFiles.add(menuItem);
		}
		refresh();
	}

	private void updateRecentProjectFilesMenu() {
		ArrayList<String> recentProjectFilePaths=model.getRecentProjectFilePaths();
		menuRecentProjectFiles.removeAll();
		for(int i=0;i<recentProjectFilePaths.size();i++) {
			String path=recentProjectFilePaths.get(i);
			MenuItem menuItem=new MenuItem(path);
			menuItem.setName(NAME__RECENTPROJECTFILE);
			menuItem.setActionCommand(path);
			menuItem.addActionListener(this);
			menuRecentProjectFiles.add(menuItem);
		}
		refresh();
	}

}
