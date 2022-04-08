package lunartools.exec;

interface LineReaderCallback {

	public void lineReceivedFromOutputReader(String line);

	public void throwableReceivedFromOutputReader(Throwable throwable);

}
