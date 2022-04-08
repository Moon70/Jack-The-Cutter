package lunartools.exec;

public interface ExecOutputCallback {

	public void execReceivedOutputLine(String line);

	public void execReceivedErrorLine(String line);

	public void execReceivedThrowable(Throwable throwable);

}
