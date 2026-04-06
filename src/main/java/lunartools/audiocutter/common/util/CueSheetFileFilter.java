package lunartools.audiocutter.common.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class CueSheetFileFilter extends FileFilter{
	public static final String FILE_EXTENSION="cue".toLowerCase();
	public static final String FILE_EXTENSION_WITH_DOT="."+FILE_EXTENSION;
	public static final String DESCRIPTION="CUE sheet";

	@Override
	public boolean accept(File file) {
		return file.isDirectory() || file.getName().toLowerCase().endsWith(FILE_EXTENSION_WITH_DOT);
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

}