package lunartools.audiocutter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class CueSheetFileFilter extends FileFilter{
	public static final String FILEEXTENSION=".cue".toLowerCase();

	@Override
	public boolean accept(File file) {
		return file.isDirectory() || file.getName().toLowerCase().endsWith(FILEEXTENSION);
	}

	@Override
	public String getDescription() {
		return "CUE sheet";
	}

}