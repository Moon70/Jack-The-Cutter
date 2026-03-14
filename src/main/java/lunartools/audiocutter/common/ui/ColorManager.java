package lunartools.audiocutter.common.ui;

import java.awt.Color;

import javax.swing.UIManager;

public class ColorManager {
	private static Color backgroundWaveFull;
	private static Color backgroundWaveZoom;
	private static Color extraZoom;
	private static Color section;
	private static Color selection;
	private static Color cursor;
	private static Color cursorPlay;
	private static Color selectionStart;
	private static Color scale;
	private static Color scaleLight;
	private static Color wave;

	static {
		loadColors();
		UIManager.addPropertyChangeListener(e -> {
			if ("lookAndFeel".equals(e.getPropertyName())) {
				loadColors();
			}
		});
	}

	private static void loadColors() {
		backgroundWaveFull =	UIManager.getColor(UiDefaults.COLOR_BACKGROUND_WAVE_FULL);
		backgroundWaveZoom =	UIManager.getColor(UiDefaults.COLOR_BACKGROUND_WAVE_ZOOM);
		extraZoom =				UIManager.getColor(UiDefaults.COLOR_EXTRA_ZOOM);
		section =				UIManager.getColor(UiDefaults.COLOR_SECTION);
		selection =				UIManager.getColor(UiDefaults.COLOR_SELECTION);
		cursor =				UIManager.getColor(UiDefaults.COLOR_CURSOR);
		cursorPlay =			UIManager.getColor(UiDefaults.COLOR_CURSOR_PLAY);
		selectionStart =		UIManager.getColor(UiDefaults.COLOR_SELECTION_START);
		scale =					UIManager.getColor(UiDefaults.COLOR_SCALE);
		scaleLight =			UIManager.getColor(UiDefaults.COLOR_SCALE_LIGHT);
		wave =					UIManager.getColor(UiDefaults.COLOR_WAVE);
	}

	private ColorManager() {}

	public static Color backgroundWaveFull()	{ return backgroundWaveFull; }
	public static Color backgroundWaveZoom()	{ return backgroundWaveZoom; }
	public static Color extraZoom()				{ return extraZoom; }
	public static Color section()				{ return section; }
	public static Color selection()				{ return selection; }
	public static Color cursor()				{ return cursor; }
	public static Color cursorPlay()			{ return cursorPlay; }
	public static Color selectionStart()		{ return selectionStart; }
	public static Color scale()					{ return scale; }
	public static Color scaleLight()			{ return scaleLight; }
	public static Color wave()					{ return wave; }

}
