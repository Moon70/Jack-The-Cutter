package lunartools.audiocutter.gui;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import lunartools.audiocutter.common.action.ActionFactory;
import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.core.AudioCutterController;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.model.StatusMessage;
import lunartools.audiocutter.gui.preferencespanel.PreferencesController;

public class MenuModel implements ActionListener, Observer{
	private ActionFactory actionFactory;
	private JMenuBar menuBar;
	
	private JMenu menuFile;
	private JMenuItem menuFileItemOpenMediaFile;
	private JMenuItem menuFileItemOpenProject;
	private JMenuItem menuFileItemSaveProjectAs;
	private JMenuItem menuFileItemSaveProject;
	private JMenuItem menuFileItemCloseProject;
	private JMenuItem menuFileItemCutMediaFile;
	private JMenuItem menuFileItemPreferences;
	private JMenuItem menuFileItemExitProgram;
	
	private JMenu menuRecentMediaFiles=new JMenu("Recent media files");
	private JMenu menuRecentProjectFiles=new JMenu("Recent project files");
	
	
	private JMenu menuTools;
	private JMenuItem menuToolsItemAutoCut;
	private JMenuItem menuToolsItemCreateCuesheet;
	
	private JMenu menuHelp;
	private JMenuItem menuHelpItemAbout;

	public MenuModel(ActionFactory actionFactory) {
		this.actionFactory=actionFactory;
		
		menuBar=new JMenuBar();
		createFileMenu();
		menuBar.add(menuFile);
		
		createToolsMenu();
		menuBar.add(menuTools);
		
		createHelpMenu();
		menuBar.add(menuHelp);
	}

	private void createFileMenu(){
		menuFile=new JMenu("File");

		menuFileItemOpenMediaFile=new JMenuItem(actionFactory.createOpenMediaFileAction());
		menuFileItemOpenMediaFile.setEnabled(true);
		menuFile.add(menuFileItemOpenMediaFile);

		menuFile.add(menuRecentMediaFiles);
		
		menuFileItemOpenProject=new JMenuItem(actionFactory.createOpenProjectAction());
		menuFileItemOpenProject.setEnabled(true);
		menuFile.add(menuFileItemOpenProject);

		menuFile.add(menuRecentProjectFiles);

		menuFileItemSaveProjectAs=new JMenuItem(actionFactory.createSaveProjectAsAction());
		menuFileItemSaveProjectAs.setEnabled(false);
		menuFile.add(menuFileItemSaveProjectAs);

		menuFileItemSaveProject=new JMenuItem(actionFactory.createSaveProjectAction());
		menuFileItemSaveProject.setEnabled(false);
		menuFile.add(menuFileItemSaveProject);

		menuFileItemCloseProject=new JMenuItem(actionFactory.createCloseProjectAction());
		menuFileItemCloseProject.setEnabled(false);
		menuFile.add(menuFileItemCloseProject);

		menuFileItemCutMediaFile=new JMenuItem(actionFactory.createCutMediaFileAction());
		menuFileItemCutMediaFile.setEnabled(false);
		menuFile.add(menuFileItemCutMediaFile);

		menuFileItemPreferences=new JMenuItem(actionFactory.createOpenPreferencesAction());
		menuFileItemPreferences.setEnabled(true);
		menuFile.add(menuFileItemPreferences);

		menuFileItemExitProgram=new JMenuItem(actionFactory.createExitProgramAction());
		menuFile.add(menuFileItemExitProgram);
	}

	private void createToolsMenu(){
		menuTools=new JMenu("Tools");

		menuToolsItemAutoCut=new JMenuItem(actionFactory.createAutoCutAction());
		menuToolsItemAutoCut.setEnabled(true);
		menuTools.add(menuToolsItemAutoCut);

		menuToolsItemCreateCuesheet=new JMenuItem(actionFactory.createCreateCuesheetAction());
		menuToolsItemCreateCuesheet.setEnabled(true);
		menuTools.add(menuToolsItemCreateCuesheet);
	}
	
	private void createHelpMenu(){
		menuHelp=new JMenu("?");

		menuHelpItemAbout=new JMenuItem(actionFactory.createAboutAction());
		menuHelpItemAbout.setEnabled(true);
		menuHelp.add(menuHelpItemAbout);
	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}

	@Override
	public void actionPerformed(ActionEvent event){
//		String actionCommand=event.getActionCommand();
//		if(actionCommand.equals(ACTIONCOMMAND__EXIT)){
//			view.sendMessage(SimpleEvents.EXIT);
//		}else if(actionCommand.equals(ACTIONCOMMAND__OPEN_MEDIAFILE)){
//			controller.action_OpenMediaFile();
//		}else if(actionCommand.equals(ACTIONCOMMAND__OPEN_PROJECT)){
//			controller.action_OpenProjectFile();
//		}else if(actionCommand.equals(ACTIONCOMMAND__SAVE_PROJECTAS)){
//			controller.action_SaveProjectFileAs();
//		}else if(actionCommand.equals(ACTIONCOMMAND__SAVE_PROJECT)){
//			controller.action_SaveProjectFile();
//		}else if(actionCommand.equals(ACTIONCOMMAND__CLOSE_PROJECT)){
//			controller.action_CloseProject();
//		}else if(actionCommand.equals(ACTIONCOMMAND__CUT_MEDIAFILE)){
//			controller.action_CutMediaFile();
//		}else if(actionCommand.equals(ACTIONCOMMAND__PREFERENCES)){
//			new PreferencesController(model).editPreferences(view);
//		}else if(actionCommand.equals(ACTIONCOMMAND__AUTOCUT)){
//			controller.action_AutoCut();
//		}else if(actionCommand.equals(ACTIONCOMMAND__CREATE_CUESHEET)){
//			controller.action_CreateCueSheet();
//		}else if(actionCommand.equals(ACTIONCOMMAND__ABOUT)){
//			view.showMessageboxAbout();
//		}
//		Object object=event.getSource();
//		if(object instanceof MenuItem) {
//			MenuItem menuItem=(MenuItem)object;
//			String name=menuItem.getName();
//			if(name.equals(NAME__RECENTMEDIAFILE)) {
//				controller.action_OpenRecentMediaFile(menuItem.getActionCommand());
//			}else if(name.equals(NAME__RECENTPROJECTFILE)) {
//				controller.action_OpenRecentProjectFile(menuItem.getActionCommand());
//			}
//		}
	}

	@Override
	public void update(Observable observable, Object object) {
//		if(object==SimpleEvents.MODEL_AUDIODATACHANGED) {
//			refresh();
//		}else if(object==SimpleEvents.MODEL_PROJECTDIRTCHANGED) {
//			refresh();
//		}else if(object==SimpleEvents.MODEL_AUDIOSECTIONSCHANGED) {
//			refresh();
//		}else if(object instanceof StatusMessage) {
//			StatusMessage statusMessage=(StatusMessage)object;
//			if(statusMessage.getType()==StatusMessage.Type.FFMPEGVERSION) {
//				refresh();
//			}
//		}else if(object==SimpleEvents.MODEL_RECENTMEDIAFILESLISTCHANGED) {
//			updateRecentMediaFilesMenu();
//		}else if(object==SimpleEvents.MODEL_RECENTPROJECTSFILELISTCHANGED) {
//			updateRecentProjectFilesMenu();
//		}
	}

	private void refresh() {
//		boolean ffmpegAvailable=model.isFFmpegAvailable();
//		boolean audiodataAvailable=model.hasAudiodata();
//		boolean projectNameAvailable=model.getProjectFile()!=null;
//
//		menuItem_OpenMediaFile.setEnabled(ffmpegAvailable);
//		menuItem_LoadProject.setEnabled(ffmpegAvailable);
//
//		menuItem_SaveProjectAs.setEnabled(audiodataAvailable&ffmpegAvailable);
//		menuItem_SaveProject.setEnabled(audiodataAvailable&ffmpegAvailable&model.isProjectDirty()&projectNameAvailable);
//		menuItem_CloseProject.setEnabled(audiodataAvailable&ffmpegAvailable);
//		menuItem_ProcessFile.setEnabled(audiodataAvailable&ffmpegAvailable&model.hasAudioSections());
//
//		menuItem_Autocut.setEnabled(audiodataAvailable);
//		menuItem_CreateCuesheet.setEnabled(audiodataAvailable);
//		menuRecentMediaFiles.setEnabled(menuRecentMediaFiles.getItemCount()>0);
//		menuRecentProjectFiles.setEnabled(menuRecentProjectFiles.getItemCount()>0);
	}

	private void updateRecentMediaFilesMenu() {
//		ArrayList<String> recentMediaFilePaths=model.getRecentMediaFilePaths();
//		menuRecentMediaFiles.removeAll();
//		for(int i=0;i<recentMediaFilePaths.size();i++) {
//			String path=recentMediaFilePaths.get(i);
//			MenuItem menuItem=new MenuItem(path);
//			menuItem.setName(NAME__RECENTMEDIAFILE);
//			menuItem.setActionCommand(path);
//			menuItem.addActionListener(this);
//			menuRecentMediaFiles.add(menuItem);
//		}
//		refresh();
	}

	private void updateRecentProjectFilesMenu() {
//		ArrayList<String> recentProjectFilePaths=model.getRecentProjectFilePaths();
//		menuRecentProjectFiles.removeAll();
//		for(int i=0;i<recentProjectFilePaths.size();i++) {
//			String path=recentProjectFilePaths.get(i);
//			MenuItem menuItem=new MenuItem(path);
//			menuItem.setName(NAME__RECENTPROJECTFILE);
//			menuItem.setActionCommand(path);
//			menuItem.addActionListener(this);
//			menuRecentProjectFiles.add(menuItem);
//		}
//		refresh();
	}

}
