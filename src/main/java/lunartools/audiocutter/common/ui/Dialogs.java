package lunartools.audiocutter.common.ui;

import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import lunartools.FileTools;
import lunartools.ImageTools;
import lunartools.audiocutter.core.AudioCutterModel;

public class Dialogs {

	private Dialogs() {}

	public static boolean userCanceledFileExistsDialogue(String message) {
		return userCanceledFileExistsDialogue(message,null);
	}

	public static boolean userCanceledFileExistsDialogue(String message, Component parentComponent) {
		JOptionPane jOptionPane=new JOptionPane(message,JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
		JDialog dialog = jOptionPane.createDialog(parentComponent,AudioCutterModel.getProgramNameAndVersion());
		dialog.setIconImage(getTitleIcon().getImage());
		dialog.setVisible(true);
		Object value=jOptionPane.getValue();
		return value instanceof Integer && ((Integer)value)==JOptionPane.OK_OPTION;
	}

	public static void showErrorMessage(String message) {
		showErrorMessage(message,null);
	}

	public static void showErrorMessage(String message, Component parentComponent) {
		JOptionPane jOptionPane=new JOptionPane(message,JOptionPane.ERROR_MESSAGE);
		JDialog dialog = jOptionPane.createDialog(parentComponent,AudioCutterModel.getProgramNameAndVersion());
		dialog.setIconImage(getTitleIcon().getImage());
		dialog.setVisible(true);
	}

	private static ImageIcon getTitleIcon() {
		return new ImageIcon(Dialogs.class.getResource("/icons/ProgramIcon16.png"));
	}

	public static void showAboutDialog(Component parentComponent) {
		try {
			InputStream inputStream = Dialogs.class.getResourceAsStream("/About_"+AudioCutterModel.PROGRAM_NAME+".html");
			StringBuffer html=FileTools.readInputStreamToStringBuffer(inputStream,StandardCharsets.UTF_8.name());
			JEditorPane editorPane = new JEditorPane("text/html", html.toString());

			editorPane.addHyperlinkListener(new HyperlinkListener(){
				@Override
				public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent){
					if (hyperlinkEvent.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
						try {
							Desktop.getDesktop().browse(hyperlinkEvent.getURL().toURI());
						} catch (IOException | URISyntaxException e) {
							e.printStackTrace();
						}
					}
				}
			});
			editorPane.setEditable(false);
			editorPane.setBackground(parentComponent.getBackground());

			JOptionPane jOptionPane=new JOptionPane(
					editorPane,
					JOptionPane.INFORMATION_MESSAGE,
					JOptionPane.DEFAULT_OPTION,
					ImageTools.createImageIcon("/icons/ProgramIcon90.png"));
			JDialog dialog = jOptionPane.createDialog(parentComponent, "About");
			dialog.setIconImage(getTitleIcon().getImage());
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean userCanceledUnsavedChangesDialogue() {
		return JOptionPane.showOptionDialog(
				null,
				"Current project contains unsaved changes!",
				AudioCutterModel.PROGRAM_NAME,
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null,
				null,
				null
				)!=JOptionPane.OK_OPTION;
	}

	public static boolean userCanceledFolderNotEmptyDialogue() {
		return JOptionPane.showOptionDialog(
				null,
				"Selected folder is not empty!\nOK to proceed and overwrite files.",
				AudioCutterModel.PROGRAM_NAME,
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null,
				null,
				null
				)!=JOptionPane.OK_OPTION;
	}

	public static boolean userCanceledProjectFileExistsDialogue(File choosenProjectFile) {
		return JOptionPane.showConfirmDialog(
				null,
				"Project file already exists, OK to overwrite?\n"+choosenProjectFile.getAbsolutePath(),
				AudioCutterModel.PROGRAM_NAME,
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null
				)!=JOptionPane.OK_OPTION;
	}

	public static boolean userCanceledCueSheetFileExistsDialogue(File choosenCueSheetFile) {
		return JOptionPane.showConfirmDialog(
				null,
				"CUE sheet already exists, OK to overwrite?\n"+choosenCueSheetFile.getAbsolutePath(),
				AudioCutterModel.PROGRAM_NAME,
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null
				)!=JOptionPane.OK_OPTION;
	}

	public static boolean userCanceledWavFileExistsDialogue(File wavFile) {
		return JOptionPane.showConfirmDialog(
				null,
				"Creating this CUE sheet overwrites this WAV file:\n"+wavFile.getAbsolutePath(),
				AudioCutterModel.PROGRAM_NAME,
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null
				)!=JOptionPane.OK_OPTION;
	}

	public static boolean userCanceledAutocutDialogue() {
		return JOptionPane.showOptionDialog(
				null,
				"Autodetected cut points get\nadded to existing cut points",
				AudioCutterModel.PROGRAM_NAME,
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null,
				null,
				null
				)!=JOptionPane.OK_OPTION;
	}
	
}
