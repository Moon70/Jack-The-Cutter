package lunartools.audiocutter.projectfile;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import lunartools.audiocutter.AudioCutterModel;

public class ProjectFileFilter extends FileFilter{
	public static final String FILEEXTENSION=".project".toLowerCase();

	@Override
	public boolean accept(File file) {
		return file.isDirectory() || file.getName().toLowerCase().endsWith(FILEEXTENSION);
	}

	@Override
	public String getDescription() {
		return AudioCutterModel.PROGRAMNAME+" project file";
	}

}