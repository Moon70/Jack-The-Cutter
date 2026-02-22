package lunartools.audiocutter.core.controller;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.model.StatusMessage;
import lunartools.audiocutter.core.view.MenuView;

public class MenuPresenter {
	private static Logger logger = LoggerFactory.getLogger(MenuPresenter.class);
	private final AudioCutterModel model;
	private final MenuView menuView;

	public MenuPresenter(AudioCutterModel model,MenuView menuView) {
		this.model=model;
		this.menuView=menuView;
		updateMenuItemState();
		model.addChangeListener(this::updateModelChanges);
	}

	public void updateModelChanges(Object object) {
		if(logger.isTraceEnabled()) {
			logger.trace("updateModelChanges: "+object);
		}
		if(object==SimpleEvents.MODEL_AUDIODATACHANGED) {
			updateMenuItemState();
		}else if(object==SimpleEvents.MODEL_PROJECTDIRTCHANGED) {
			updateMenuItemState();
		}else if(object==SimpleEvents.MODEL_AUDIOSECTIONSCHANGED) {
			updateMenuItemState();
		}else if(object instanceof StatusMessage) {
			StatusMessage statusMessage=(StatusMessage)object;
			if(statusMessage.getType()==StatusMessage.Type.FFMPEGVERSION) {
				updateMenuItemState();
			}
		}else if(object==SimpleEvents.MODEL_RECENTMEDIAFILESLISTCHANGED) {
			updateRecentMediaFilesMenu();
			updateMenuItemState();
		}else if(object==SimpleEvents.MODEL_RECENTPROJECTSFILELISTCHANGED) {
			updateRecentProjectFilesMenu();
			updateMenuItemState();
		}
	}

	private void updateMenuItemState() {
		boolean ffmpegAvailable=model.isFFmpegAvailable();
		boolean audiodataAvailable=model.hasAudiodata();
		boolean projectNameAvailable=model.getProjectFile()!=null;

		menuView.getMenuFileItemOpenMediaFile().setEnabled(ffmpegAvailable);
		menuView.getMenuFileItemOpenProject().setEnabled(ffmpegAvailable);

		menuView.getMenuFileItemSaveProjectAs().setEnabled(audiodataAvailable&ffmpegAvailable);
		menuView.getMenuFileItemSaveProject().setEnabled(audiodataAvailable&ffmpegAvailable&model.isProjectDirty()&projectNameAvailable);
		menuView.getMenuFileItemCloseProject().setEnabled(audiodataAvailable&ffmpegAvailable);
		menuView.getMenuFileItemCutMediaFile().setEnabled(audiodataAvailable&ffmpegAvailable&model.hasAudioSections());

		menuView.getMenuToolsItemAutoCut().setEnabled(audiodataAvailable);
		menuView.getMenuToolsItemCreateCuesheet().setEnabled(audiodataAvailable);

		updateRecentMediaFilesMenu();
		updateRecentProjectFilesMenu();
		menuView.getMenuRecentMediaFiles().setEnabled(menuView.getMenuRecentMediaFiles().getItemCount()>0);
		menuView.getMenuRecentProjectFiles().setEnabled(menuView.getMenuRecentProjectFiles().getItemCount()>0);
	}

	private void updateRecentMediaFilesMenu() {
		ArrayList<String> recentMediaFilePaths=model.getRecentMediaFilePaths();
		JMenu menuRecentMediaFiles=menuView.getMenuRecentMediaFiles();
		menuRecentMediaFiles.removeAll();
		for(int i=0;i<recentMediaFilePaths.size();i++) {
			String path=recentMediaFilePaths.get(i);
			File file=new File(path);
			JMenuItem menuItem=new JMenuItem(menuView.getActionFactory().createOpenRecentMediaFileAction(file));
			menuRecentMediaFiles.add(menuItem);
		}
	}

	private void updateRecentProjectFilesMenu() {
		ArrayList<String> recentProjectFilePaths=model.getRecentProjectFilePaths();
		JMenu menuRecentProjectFiles=menuView.getMenuRecentProjectFiles();
		menuRecentProjectFiles.removeAll();
		for(int i=0;i<recentProjectFilePaths.size();i++) {
			String path=recentProjectFilePaths.get(i);
			File file=new File(path);
			JMenuItem menuItem=new JMenuItem(menuView.getActionFactory().createOpenRecentProjectFileAction(file));
			menuRecentProjectFiles.add(menuItem);
		}
	}

}
