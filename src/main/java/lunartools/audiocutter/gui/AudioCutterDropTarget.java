package lunartools.audiocutter.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterController;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;
import lunartools.audiocutter.player.AudioPlayer;

public class AudioCutterDropTarget extends DropTarget{
	private static Logger logger = LoggerFactory.getLogger(AudioCutterDropTarget.class);
	private AudioCutterModel model;
	private AudioCutterController controller;

	public AudioCutterDropTarget(AudioCutterModel model,AudioCutterController controller) {
		this.model=model;
		this.controller=controller;
	}

	public synchronized void drop(DropTargetDropEvent evt) {
		try {
			evt.acceptDrop(DnDConstants.ACTION_LINK);
			Transferable transferable=evt.getTransferable();
			Object object=transferable.getTransferData(DataFlavor.javaFileListFlavor);
			if(!(object instanceof List<?>)) {
				return;
			}
			List<File> droppedFiles = (List<File>)object;
			if(droppedFiles.size()>1) {
				model.setStatusMessage(new StatusMessage(StatusMessage.Type.WARNING,"only drag single files to Jack, please"));
				return;
			}
			if(model.isProjectDirty() && controller.userCanceledUnsavedChangesDialogue()){
				return;
			}
			AudioPlayer.getInstance().action_stop();
			model.closeProject();
			File file=droppedFiles.get(0);
			if(file.getName().toLowerCase().endsWith(".project")) {
				controller.processProjectFile(file);
			}else {
				model.setMediaFile(file);
			}

		} catch (Exception e) {
			logger.error("Error when drag´n´drop file on Jack",e);
		}
	}

}
