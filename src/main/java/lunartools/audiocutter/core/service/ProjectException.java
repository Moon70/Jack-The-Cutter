package lunartools.audiocutter.core.service;

public class ProjectException extends RuntimeException {
	private Exception exception;
	
	public ProjectException(Exception exception) {
		this.exception=exception;
	}

	public Exception getException() {
		return exception;
	}

	@Override
	public String getMessage() {
		if(exception!=null) {
			return exception.getMessage();
		}
		return super.getMessage();
	}
}
