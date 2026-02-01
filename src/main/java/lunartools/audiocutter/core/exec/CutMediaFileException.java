package lunartools.audiocutter.core.exec;

public class CutMediaFileException extends RuntimeException {
	private Exception exception;
	
	public CutMediaFileException(String message) {
		super(message);
	}

	public CutMediaFileException(String message, Exception exception) {
		super(message);
		this.exception=exception;
	}

	public Exception getException() {
		return exception;
	}
	
}
