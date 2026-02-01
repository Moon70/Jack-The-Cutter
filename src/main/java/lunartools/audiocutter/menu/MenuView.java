package lunartools.audiocutter.menu;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import lunartools.audiocutter.common.action.ActionFactory;

public class MenuView {
	private final ActionFactory actionFactory;
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

	public MenuView(ActionFactory actionFactory) {
		this.actionFactory=actionFactory;
		menuBar=new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createToolsMenu());
		menuBar.add(createHelpMenu());
	}

	private JMenu createFileMenu(){
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
		
		return menuFile;
	}

	private JMenu createToolsMenu(){
		menuTools=new JMenu("Tools");

		menuToolsItemAutoCut=new JMenuItem(actionFactory.createAutoCutAction());
		menuToolsItemAutoCut.setEnabled(true);
		menuTools.add(menuToolsItemAutoCut);

		menuToolsItemCreateCuesheet=new JMenuItem(actionFactory.createCreateCuesheetAction());
		menuToolsItemCreateCuesheet.setEnabled(true);
		menuTools.add(menuToolsItemCreateCuesheet);
		
		return menuTools;
	}
	
	private JMenu createHelpMenu(){
		menuHelp=new JMenu("?");
		menuHelpItemAbout=new JMenuItem(actionFactory.createAboutAction());
		menuHelpItemAbout.setEnabled(true);
		menuHelp.add(menuHelpItemAbout);
		
		return menuHelp;
	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}


	public JMenuItem getMenuFileItemOpenMediaFile() {
		return menuFileItemOpenMediaFile;
	}

	
	public JMenuItem getMenuFileItemOpenProject() {
		return menuFileItemOpenProject;
	}

	
	public JMenuItem getMenuFileItemSaveProjectAs() {
		return menuFileItemSaveProjectAs;
	}

	
	public JMenuItem getMenuFileItemSaveProject() {
		return menuFileItemSaveProject;
	}

	
	public JMenu getMenuRecentMediaFiles() {
		return menuRecentMediaFiles;
	}

	
	public JMenu getMenuRecentProjectFiles() {
		return menuRecentProjectFiles;
	}

	
	public JMenuItem getMenuToolsItemAutoCut() {
		return menuToolsItemAutoCut;
	}

	
	public JMenuItem getMenuToolsItemCreateCuesheet() {
		return menuToolsItemCreateCuesheet;
	}

	public JMenuItem getMenuFileItemCloseProject() {
		return menuFileItemCloseProject;
	}

	public JMenuItem getMenuFileItemCutMediaFile() {
		return menuFileItemCutMediaFile;
	}
	
}
