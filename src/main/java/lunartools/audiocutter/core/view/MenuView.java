package lunartools.audiocutter.core.view;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import lunartools.audiocutter.common.action.ActionFactory;
import lunartools.audiocutter.common.ui.IconProvider;
import lunartools.audiocutter.common.ui.Icons;

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
		menuFileItemOpenMediaFile.setIcon(IconProvider.getFlatSvgIcon(Icons.OPEN_MEDIA,menuFileItemOpenMediaFile));
		menuFile.add(menuFileItemOpenMediaFile);

		menuFile.add(menuRecentMediaFiles);
		menuRecentMediaFiles.setIcon(IconProvider.getFlatSvgIcon(Icons.RECENT_MEDIA,menuRecentMediaFiles));

		menuFileItemOpenProject=new JMenuItem(actionFactory.createOpenProjectAction());
		menuFileItemOpenProject.setIcon(IconProvider.getFlatSvgIcon(Icons.OPEN_PROJECT,menuFileItemOpenProject));
		menuFile.add(menuFileItemOpenProject);

		menuFile.add(menuRecentProjectFiles);
		menuRecentProjectFiles.setIcon(IconProvider.getFlatSvgIcon(Icons.RECENT_MEDIA,menuRecentProjectFiles));

		menuFileItemSaveProjectAs=new JMenuItem(actionFactory.createSaveProjectAsAction());
		menuFileItemSaveProjectAs.setIcon(IconProvider.getFlatSvgIcon(Icons.SAVE_PROJECT_AS,menuFileItemSaveProjectAs));
		menuFile.add(menuFileItemSaveProjectAs);

		menuFileItemSaveProject=new JMenuItem(actionFactory.createSaveProjectAction());
		menuFileItemSaveProject.setIcon(IconProvider.getFlatSvgIcon(Icons.SAVE_PROJECT,menuFileItemSaveProject));
		menuFile.add(menuFileItemSaveProject);

		menuFileItemCloseProject=new JMenuItem(actionFactory.createCloseProjectAction());
		menuFileItemCloseProject.setIcon(IconProvider.getFlatSvgIcon(Icons.CLOSE_PROJECT,menuFileItemCloseProject));
		menuFile.add(menuFileItemCloseProject);

		menuFileItemCutMediaFile=new JMenuItem(actionFactory.createCutMediaFileAction());
		menuFileItemCutMediaFile.setIcon(IconProvider.getFlatSvgIcon(Icons.CUT_MEDIA,menuFileItemCutMediaFile));
		menuFile.add(menuFileItemCutMediaFile);

		menuFileItemPreferences=new JMenuItem(actionFactory.createOpenPreferencesAction());
		menuFileItemPreferences.setIcon(IconProvider.getFlatSvgIcon(Icons.PREFERENCES,menuFileItemPreferences));
		menuFile.add(menuFileItemPreferences);

		menuFileItemExitProgram=new JMenuItem(actionFactory.createExitProgramAction());
		menuFileItemExitProgram.setIcon(IconProvider.getFlatSvgIcon(Icons.EXIT_PRGRAM,menuFileItemExitProgram));
		menuFile.add(menuFileItemExitProgram);

		return menuFile;
	}

	private JMenu createToolsMenu(){
		menuTools=new JMenu("Tools");

		menuToolsItemAutoCut=new JMenuItem(actionFactory.createAutoCutAction());
		menuToolsItemAutoCut.setIcon(IconProvider.getFlatSvgIcon(Icons.AUTO_CUT,menuToolsItemAutoCut));
		menuTools.add(menuToolsItemAutoCut);

		menuToolsItemCreateCuesheet=new JMenuItem(actionFactory.createCreateCuesheetAction());
		menuToolsItemCreateCuesheet.setIcon(IconProvider.getFlatSvgIcon(Icons.CREATE_CUESHEET,menuToolsItemCreateCuesheet));
		menuTools.add(menuToolsItemCreateCuesheet);

		return menuTools;
	}

	private JMenu createHelpMenu(){
		menuHelp=new JMenu("?");
		menuHelpItemAbout=new JMenuItem(actionFactory.createAboutAction());
		menuHelpItemAbout.setIcon(IconProvider.getFlatSvgIcon(Icons.ABOUT,menuHelpItemAbout));
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

	//TODO: temporary while refactoring
	public ActionFactory getActionFactory() {
		return actionFactory;
	}

}
