package lunartools.exec;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Exec extends Thread{
	private String program;
	private String parameter;
	private final ExecOutputCallback execOutputCallback;
	private String characterEncoding;
	public LineReaderThread outputReaderThread;
	public LineReaderThread errorReaderThread;
	Exception exception;

	public Exec(String program, String parameter,ExecOutputCallback lineCallback) {
		program=program.trim();
		if(program.contains(" ") && !program.startsWith("\"")) {
			this.program="\""+program+"\"";
		}else {
			this.program=program;
		}
		this.parameter=parameter;
		this.execOutputCallback=lineCallback;
	}

	public Exec(String program, String parameter,String characterEncoding,ExecOutputCallback lineCallback) {
		this(program,parameter,lineCallback);
		this.characterEncoding=characterEncoding;
	}

	@Override
	public void run() {
		try {
			Process pid;
			BufferedReader outputLineReader;
			BufferedReader errorLineReader;
			if(parameter==null || parameter.length()==0) {
				pid=Runtime.getRuntime().exec(program);
			}else {
				pid=Runtime.getRuntime().exec(program+" "+parameter);
			}
			if(characterEncoding==null) {
				outputLineReader=new BufferedReader(new InputStreamReader(pid.getInputStream()));
				errorLineReader=new BufferedReader(new InputStreamReader(pid.getErrorStream()));
			}else {
				outputLineReader=new BufferedReader(new InputStreamReader(pid.getInputStream(),characterEncoding));
				errorLineReader=new BufferedReader(new InputStreamReader(pid.getErrorStream(),characterEncoding));
			}
			outputReaderThread=new LineReaderThread(outputLineReader,new LineReaderCallback() {

				@Override
				public void lineReceivedFromOutputReader(String line) {
					execOutputCallback.execReceivedOutputLine(line);
				}

				@Override
				public void throwableReceivedFromOutputReader(Throwable throwable) {
					execOutputCallback.execReceivedThrowable(throwable);
				}

			},this);

			errorReaderThread=new LineReaderThread(errorLineReader,new LineReaderCallback() {

				@Override
				public void lineReceivedFromOutputReader(String line) {
					execOutputCallback.execReceivedErrorLine(line);
				}

				@Override
				public void throwableReceivedFromOutputReader(Throwable throwable) {
					execOutputCallback.execReceivedThrowable(throwable);
				}

			},this);

			outputReaderThread.start();
			errorReaderThread.start();

			try {
				while(outputReaderThread.isAlive() || errorReaderThread.isAlive()) {
					if(this.isInterrupted()) {
						outputReaderThread.interrupt();
						errorReaderThread.interrupt();
						return;
					}
				}
			} finally {
				pid.destroy();
				outputLineReader.close();
				errorLineReader.close();
			}
		} catch (Exception e) {
			exception=e;
		}
	}

	public boolean isError() {
		return exception!=null;
	}

	public Exception getException() {
		return exception;
	}

}
