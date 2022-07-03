package lunartools.audiocutter;

import lunartools.Settings;

public class AudioCutterSettings {
	//public static final String VIEW_HORIZONTALDIVIDER =						"HDivider";
	public static final String VIEW_BOUNDS = 									"ViewBounds";
	public static final String VIEW_SECTIONTABLE_WIDTH =						"SectionTableWidth";
	public static final String AUDIOFILE_PATH = 								"AudiofilePath";
	public static final String PROJECTFILE_PATH = 								"ProjectfilePath";
	public static final String FFMPEG_PATH = 									"FFmpegExecutable";
	public static final String RECENT_MEDIA_PATHS = 							"RecentMedia";
	public static final String RECENT_PROJECT_PATHS = 							"RecentProject";

	public static final String FFMPEG_DETERMINEVERSION_TIMEOUT=					"ffmpeg.determineversion.timeoutinseconds";
	public static final String FFMPEG_DETERMINEVERSION_PARAMETER=				"ffmpeg.determineversion.parameter";
	public static final String FFMPEG_DETERMINEVERSION_PATTERN=					"ffmpeg.determineversion.pattern";

	public static final String FFMPEG_CREATETEMPWAV_TIMEOUT=					"ffmpeg.createtempwav.timeoutinseconds";
	public static final String FFMPEG_CREATETEMPWAV_PARAMETER=					"ffmpeg.createtempwav.parameter";
	public static final String FFMPEG_CREATETEMPWAV_PATTERN_DURATION=			"ffmpeg.createtempwav.pattern.duration";
	public static final String FFMPEG_CREATETEMPWAV_PATTERN_PROGRESS=			"ffmpeg.createtempwav.pattern.progress";

	public static final String FFMPEG_CREATESECTIONS_TIMEOUT=					"ffmpeg.createsections.timeoutinseconds";
	public static final String FFMPEG_CREATESECTIONS_PARAMETER=					"ffmpeg.createsections.parameter";
	public static final String FFMPEG_CREATESECTIONS_PATTERN_OUTPUTFORMATERROR=	"ffmpeg.createsections.pattern.outputformaterror";

	public static final String AUTOCUT_SILENCE_LENGTH=							"autocut.silence.lengthintenthsofseconds";
	public static final String AUTOCUT_TRESHHOLD=								"autocut.treshhold";
	public static final String AUTOCUT_DISTANCE=								"autocut.distanceseconds";
	public static final String AUTOCUT_FINETUNE_LEFT=							"autocut.finetune.leftlengthinseconds";
	public static final String AUTOCUT_FINETUNE_RIGHT=							"autocut.finetune.rightlengthinseconds";
	public static final String AUTOCUT_FINETUNE_SILENCE_LENGTH=					"autocut.finetune.silence.lengthintenthsofseconds";


	private static Settings settings;

	public static Settings getSettings() {
		if(settings==null) {
			settings=new Settings(AudioCutterModel.PROGRAMNAME,AudioCutterModel.determineProgramVersion());
		}
		return settings;
	}

}
