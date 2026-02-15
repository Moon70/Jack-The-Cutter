package lunartools.audiocutter.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.common.action.ActionFactory;
import lunartools.audiocutter.core.AudioCutterController;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.AudioCutterView;
import lunartools.audiocutter.core.controller.MediaController;
import lunartools.audiocutter.core.controller.ProjectController;
import lunartools.audiocutter.core.service.AutoCutService;
import lunartools.audiocutter.core.service.CreateCueSheetService;
import lunartools.audiocutter.core.service.CutMediaFileService;
import lunartools.audiocutter.core.service.MediaService;
import lunartools.audiocutter.core.service.ProjectService;
import lunartools.audiocutter.menu.MenuPresenter;
import lunartools.audiocutter.menu.MenuView;

public class AudioCutterBootstrap {
	private static Logger logger = LoggerFactory.getLogger(AudioCutterBootstrap.class);

	private AudioCutterBootstrap() {}

	public static void start() {
		AudioCutterModel audioCutterModel=new AudioCutterModel();
		AudioCutterView audioCutterView=new AudioCutterView(audioCutterModel);

		ProjectService projectService=new ProjectService(audioCutterModel);
		ProjectController projectController=new ProjectController(audioCutterModel,projectService);
		MediaService mediaService=new MediaService(audioCutterModel);
		AutoCutService autoCutService=new AutoCutService(audioCutterModel);
		CutMediaFileService cutMediaFileService=new CutMediaFileService(audioCutterModel);
		CreateCueSheetService createCueSheetService=new CreateCueSheetService(audioCutterModel);
		MediaController mediaController=new MediaController(
				audioCutterModel,
				audioCutterView,
				mediaService,
				autoCutService,
				cutMediaFileService,
				createCueSheetService);

		AudioCutterController audioCutterController=new AudioCutterController(
				audioCutterModel,
				audioCutterView,
				projectController,
				mediaController
				);
		audioCutterView.setDropTargetHandler(audioCutterController);
		audioCutterView.temporaryInjectController(audioCutterController);
		
		ActionFactory actionFactory=new ActionFactory(audioCutterController,projectController,mediaController);
		MenuView menuView = new MenuView(actionFactory);
		audioCutterView.setMenuView(menuView);
		new MenuPresenter(audioCutterModel,menuView);
		
		audioCutterView.getPanelLeft().getButtonPanel().setActionFactory(actionFactory);
		
		audioCutterController.openGUI();
		
		logger.info(AudioCutterModel.getProgramNameAndVersion());
	}

}
