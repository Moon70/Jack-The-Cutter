package lunartools.audiocutter.core.controller;

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
		menuView.setRecentMediaFiles(model.getRecentMediaFilePaths());
		menuView.setRecentProjectFiles(model.getRecentProjectFilePaths());
		updateEnabledState();
		model.addChangeListener(this::updateModelChanges);
	}

	public void updateModelChanges(Object object) {
		if(logger.isTraceEnabled()) {
			logger.trace("updateModelChanges: "+object);
		}
		if(object==SimpleEvents.MODEL_AUDIODATACHANGED) {
			updateEnabledState();
		}else if(object==SimpleEvents.MODEL_PROJECTDIRTCHANGED) {
			updateEnabledState();
		}else if(object==SimpleEvents.MODEL_AUDIOSECTIONSCHANGED) {
			updateEnabledState();
		}else if(object instanceof StatusMessage) {
			StatusMessage statusMessage=(StatusMessage)object;
			if(statusMessage.getType()==StatusMessage.Type.FFMPEGVERSION) {
				updateEnabledState();
			}
		}else if(object==SimpleEvents.MODEL_RECENTMEDIAFILESLISTCHANGED) {
			menuView.setRecentMediaFiles(model.getRecentMediaFilePaths());
			updateEnabledState();
		}else if(object==SimpleEvents.MODEL_RECENTPROJECTSFILELISTCHANGED) {
			menuView.setRecentProjectFiles(model.getRecentProjectFilePaths());
			updateEnabledState();
		}
	}

	private void updateEnabledState() {
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

		menuView.getMenuRecentMediaFiles().setEnabled(menuView.getMenuRecentMediaFiles().getItemCount()>0);
		menuView.getMenuRecentProjectFiles().setEnabled(menuView.getMenuRecentProjectFiles().getItemCount()>0);
	}

}
