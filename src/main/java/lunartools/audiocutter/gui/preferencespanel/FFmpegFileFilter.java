package lunartools.audiocutter.gui.preferencespanel;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FFmpegFileFilter extends FileFilter{

	@Override
	public boolean accept(File file) {
		return file.isDirectory() || file.getName().toLowerCase().startsWith("ffmpeg");
	}

	@Override
	public String getDescription() {
		return "FFmpeg executable";
	}

}