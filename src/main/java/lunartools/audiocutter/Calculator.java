package lunartools.audiocutter;

import java.util.Locale;

public class Calculator {

	public static String convertNumberOfSamplesToHourMinuteSecondString(int samples) {
		double seconds=samples/44100.0;
		int hours=(int)(seconds/3600.0);
		seconds-=hours*3600;
		int minutes=(int)(seconds/60.0);
		seconds-=minutes*60;
		if(hours>0) {
			return String.format(Locale.ENGLISH,"%01d:%02d:%02d",hours,minutes,(int)seconds);
		}else {
			return String.format(Locale.ENGLISH,"%02d:%02d",minutes,(int)seconds);
		}
	}

	public static String convertNumberOfSamplesToHourMinuteSecondFractionString(int samples) {
		double seconds=samples/44100.0;
		int hours=(int)(seconds/3600.0);
		seconds-=hours*3600;
		int minutes=(int)(seconds/60.0);
		seconds-=minutes*60;
		return String.format(Locale.ENGLISH,"%01d:%02d:%06.3f",hours,minutes,seconds);
	}

	public static String convertNumberOfSamplesToSecondsAsString(int samples) {
		double seconds=samples/44100.0;
		return String.format(Locale.ENGLISH,"%.6f",seconds);
	}

	public static int getSecondsFromTimestamp(String hours,String minutes,String seconds) {
		return Integer.parseInt(seconds)+
				Integer.parseInt(minutes)*60+
				Integer.parseInt(hours)*3600;
	}

}
