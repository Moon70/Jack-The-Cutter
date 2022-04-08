package lunartools.audiocutter.gui;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import lunartools.audiocutter.AudioCutterController;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.SimpleEvents;
import lunartools.audiocutter.gui.preferencespanel.PreferencesController;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;

public class MenubarController implements ActionListener, Observer{
	private static final String ACTIONCOMMAND__OPEN_MEDIAFILE = "openMediafile";
	private static final String ACTIONCOMMAND__OPEN_PROJECT = "loadProject";
	private static final String ACTIONCOMMAND__SAVE_PROJECTAS = "saveProjectAs";
	private static final String ACTIONCOMMAND__SAVE_PROJECT = "saveProject";
	private static final String ACTIONCOMMAND__CLOSE_PROJECT = "closeProject";
	private static final String ACTIONCOMMAND__CUT_MEDIAFILE = "cutMediaFile";
	private static final String ACTIONCOMMAND__PREFERENCES = "preferences";
	private static final String ACTIONCOMMAND__ABOUT = "about";
	private static final String ACTIONCOMMAND__EXIT = "exit";

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

	public MenubarController(AudioCutterModel model,AudioCutterController controller,AudioCutterView view) {
		this.model=model;
		this.controller=controller;
		this.view=view;
		model.addObserver(this);
	}

	public MenuBar createMenubar() {
		MenuBar menuBar=new MenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createHelpMenu());
		return menuBar;
	}

	private Menu createFileMenu(){
		Menu menu=new Menu("File");
		menu.addActionListener(this);

		menuItem_OpenMediaFile=new MenuItem("Open media file");
		menuItem_OpenMediaFile.setActionCommand(ACTIONCOMMAND__OPEN_MEDIAFILE);
		menu.add(menuItem_OpenMediaFile);

		menuItem_LoadProject=new MenuItem("Open project");
		menuItem_LoadProject.setActionCommand(ACTIONCOMMAND__OPEN_PROJECT);
		menu.add(menuItem_LoadProject);

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

		refresh();

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
		}else if(actionCommand.equals(ACTIONCOMMAND__ABOUT)){
			view.showMessageboxAbout();
		}
	}

	@Override
	public void update(Observable observable, Object object) {
		if(object==SimpleEvents.MODEL_AUDIODATACHANGED) {
			refresh();
		}else if(object==SimpleEvents.MODEL_PROJECTDIRTCHANGED) {
			refresh();
		}else if(object instanceof StatusMessage) {
			StatusMessage statusMessage=(StatusMessage)object;
			if(statusMessage.getType()==StatusMessage.Type.FFMPEGVERSION) {
				refresh();
			}
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
	}

}
