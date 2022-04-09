package lunartools.audiocutter.gui.statuspanel;

public class StatusMessage {
	public enum Type{INFO,WARNING,ERROR,FFMPEGVERSION}

	private Type type;
	private String message;
	private Throwable throwable;

	public StatusMessage(Type type, String message) {
		this.type=type;
		this.message=message;
	}

	public StatusMessage(Type type, String message,Throwable error) {
		this(type,message);
		this.throwable=error;
	}

	public Type getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	public Throwable getError() {
		return throwable;
	}

	public String toString() {
		return type+", "+message;
	}

}
