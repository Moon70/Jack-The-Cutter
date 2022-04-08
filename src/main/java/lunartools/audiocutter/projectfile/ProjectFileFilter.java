package lunartools.audiocutter.projectfile;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import lunartools.audiocutter.AudioCutterModel;

public class ProjectFileFilter extends FileFilter{

	@Override
	public boolean accept(File file) {
		return file.isDirectory() || file.getName().toLowerCase().endsWith(".project");
	}

	@Override
	public String getDescription() {
		return AudioCutterModel.PROGRAMNAME+" project file";
	}

}