package lunartools.exec;

import java.io.Reader;

class LineReaderThread extends Thread{
	private final Reader reader;
	private final LineReaderCallback lineCallback;
	private final Exec exec;
	private StringBuffer line;

	public LineReaderThread(Reader reader,LineReaderCallback lineCallback,Exec exec) {
		this.reader=reader;
		this.lineCallback=lineCallback;
		this.exec=exec;
	}

	@Override
	public void run() {
		int c;
		try {
			line=new StringBuffer();
			while(((c=reader.read()) != -1)) {
				if(c==10 || c==13) {
					lineCallback.lineReceivedFromOutputReader(line.toString());
					line=new StringBuffer();
				}else {
					line.append((char)c);
				}
				if(this.isInterrupted()) {
					break;
				}
			}
		} catch (Exception exception) {
			exec.exception=exception;
			lineCallback.throwableReceivedFromOutputReader(exception);
		}
	}

}
