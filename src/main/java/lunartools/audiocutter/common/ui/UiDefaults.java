package lunartools.audiocutter.common.ui;

import java.awt.Color;

import javax.swing.UIManager;

public class UiDefaults {
	public static final String COLOR_BACKGROUND_WAVE_FULL=	"audiocutter.backgroundWaveFull";
	public static final String COLOR_BACKGROUND_WAVE_ZOOM=	"audiocutter.backgroundWaveZoom";
	public static final String COLOR_EXTRA_ZOOM=			"audiocutter.extraZoom";
	public static final String COLOR_SECTION=				"audiocutter.section";
	public static final String COLOR_SELECTION=				"audiocutter.selection";
	public static final String COLOR_CURSOR=				"audiocutter.cursor";
	public static final String COLOR_CURSOR_PLAY=			"audiocutter.cursorPlay";
	public static final String COLOR_SELECTION_START=		"audiocutter.selectionStart";
	public static final String COLOR_SCALE=					"audiocutter.scale";
	public static final String COLOR_SCALE_LIGHT=			"audiocutter.scaleLight";
	public static final String COLOR_WAVE=					"audiocutter.wave";
	private UiDefaults() {}

	public static void install() {
		installGeneralDefaults();
		installCustomColors();
	}

	private static void installGeneralDefaults() {
		UIManager.put("ScrollBar.width", 16);
		UIManager.put("ScrollBar.thumbArc", 12);
		UIManager.put("ScrollBar.trackArc", 12);
	}

	private static void installCustomColors() {
		//        boolean isDarkTheme = com.formdev.flatlaf.FlatLaf.isLafDark();
		boolean isDarkTheme = false;

		if (isDarkTheme) {
		} else {
			UIManager.put(COLOR_BACKGROUND_WAVE_FULL,	new Color(0xC6E8FF));
			UIManager.put(COLOR_BACKGROUND_WAVE_ZOOM,	new Color(0xFFFFE4));
			UIManager.put(COLOR_EXTRA_ZOOM, 			new Color(0xcccccc));
			UIManager.put(COLOR_SECTION,				new Color(0xFFE4D5));
			UIManager.put(COLOR_SELECTION,				new Color(0xdddddd));
			UIManager.put(COLOR_CURSOR,					new Color(0x0099ff));
			UIManager.put(COLOR_CURSOR_PLAY,			new Color(0x000000));
			UIManager.put(COLOR_SELECTION_START,		new Color(0xff0000));
			UIManager.put(COLOR_SCALE,					new Color(0x000000));
			UIManager.put(COLOR_SCALE_LIGHT,			new Color(0x808080));
			UIManager.put(COLOR_WAVE,					new Color(0x000000));
		}
	}

}
