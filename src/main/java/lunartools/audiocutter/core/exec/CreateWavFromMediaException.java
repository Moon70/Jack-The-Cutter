package lunartools.audiocutter.core.exec;

public class CreateWavFromMediaException extends RuntimeException {
	private Exception exception;
	
	public CreateWavFromMediaException(String message) {
		super(message);
	}

	public CreateWavFromMediaException(String message, Exception exception) {
		super(message);
		this.exception=exception;
	}

	public Exception getException() {
		return exception;
	}
	
}
