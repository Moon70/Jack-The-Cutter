package lunartools.audiocutter.common.ui;

public enum Icons {
	OPEN_MEDIA				("audio_file_24px.svg"),
	OPEN_PROJECT			("library_music_24px.svg"),
	SAVE_PROJECT_AS			("save_as_24px.svg"),
	SAVE_PROJECT			("save_24px.svg"),
	RECENT_MEDIA			("history_24px.svg"),
	RECENT_PROJECTS			("history_24px.svg"),
	CLOSE_PROJECT			("close_24px.svg"),
	EXIT_PRGRAM				("logout_24px.svg"),
	PREFERENCES				("settings_24px.svg"),
	CUT_MEDIA				("content_cut_24px.svg"),
	AUTO_CUT				("wand_stars_24px.svg"),
	CREATE_CUESHEET			("album_24px.svg"),
	ABOUT					("info_24px.svg"),
	PLAY_CURSOR				("play_arrow_24px.svg"),
	PLAY_SELECTION			("play_circle_24px.svg"),
	PAUSE					("pause_24px.svg"),
	STOP					("stop_24px.svg"),
	NEXT_SECTION			("skip_next_24px.svg"),
	PREV_SECTION			("skip_previous_24px.svg"),
	ZOOM_IN					("zoom_in_24px.svg"),
	ZOOM_OUT				("zoom_out_24px.svg"),
	ZOOM_SELECTION			("search_check_24px.svg"),
	AMPLITUDE_ZOOM			("mystery_24px.svg"),
	ADD_CUT_POINT			("add_location_24px.svg"),
	EDIT_START				("edit_location_left_24px.svg"),
	EDIT_END				("edit_location_alt_24px.svg"),
	DELETE_LEFT_CUTPOINT	("wrong_location_mirror_24px.svg"),
	DELETE_RIGHT_CUTPOINT	("wrong_location_24px.svg"),
	FIT_TO_WINDOW			("fit_screen_24px.svg"),
	FOLDER_OPEN				("folder_open_24px.svg"),
	RECENT_MEDIA_FILE		("music_note_24px.svg"),
	RECENT_PROJECT_FILE		("queue_music_24px.svg");

	private static final String FOLDER="icons/material-symbols/";
	private final String fileName;

	Icons(String fileName) {
		this.fileName=fileName;
	}

	public String getPath() {
		return FOLDER.concat(fileName);
	}

}
