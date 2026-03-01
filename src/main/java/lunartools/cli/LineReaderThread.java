package lunartools.cli;

import java.io.IOException;
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
				if (c == '\r') {
					c=reader.read();
					if(c==-1 || c=='\n') {
						lineCallback.lineReceivedFromOutputReader(line.toString());
						line=new StringBuffer();
						continue;
					}
				}
				if (c == '\n'){
					lineCallback.lineReceivedFromOutputReader(line.toString());
					line=new StringBuffer();
				}else {
					line.append((char)c);
				}
				if(this.isInterrupted()) {
					break;
				}
			}
			if(line.length()>0) {
				lineCallback.lineReceivedFromOutputReader(line.toString());
			}
		} catch (Exception exception) {
			exec.setException(exception);
			lineCallback.throwableReceivedFromOutputReader(exception);
		}finally {
			try {
				reader.close();
			} catch (IOException ignored) {}
		}
	}

}
