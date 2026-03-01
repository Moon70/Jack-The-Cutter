package lunartools.cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Objects;

public class Exec extends Thread{
	private String[] cmdArray;
	private final ExecOutputCallback execOutputCallback;
	private String characterEncoding;
	private LineReaderThread outputReaderThread;
	private LineReaderThread errorReaderThread;
	private volatile Exception exception;
	private volatile int exitCode=-1;

	public Exec(String[] cmdArray,ExecOutputCallback lineCallback) {
		this.cmdArray=Objects.requireNonNull(cmdArray);
		this.execOutputCallback=lineCallback;
	}

	public Exec(String[] cmdArray, String characterEncoding,ExecOutputCallback lineCallback) {
		this(cmdArray,lineCallback);
		this.characterEncoding=Objects.requireNonNull(characterEncoding);
		try {
			Charset.forName(characterEncoding);
		} catch (UnsupportedCharsetException e) {
		    throw new IllegalArgumentException("Invalid character encoding: " + characterEncoding, e);
		}
	}

	@Override
	public void run() {
		Process process=null;
		try {
			process=Runtime.getRuntime().exec(cmdArray);
			BufferedReader outputLineReader=characterEncoding==null
					?new BufferedReader(new InputStreamReader(process.getInputStream()))
					:new BufferedReader(new InputStreamReader(process.getInputStream(),characterEncoding));

			BufferedReader errorLineReader=characterEncoding==null
					?new BufferedReader(new InputStreamReader(process.getErrorStream()))
					:new BufferedReader(new InputStreamReader(process.getErrorStream(),characterEncoding));

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
			exitCode = process.waitFor();
			while(outputReaderThread.isAlive() || errorReaderThread.isAlive()) {
				try {
					outputReaderThread.join(100);
					errorReaderThread.join(100);
					if(this.isInterrupted()) {
						outputReaderThread.interrupt();
						errorReaderThread.interrupt();
						return;
					}
				}catch(InterruptedException e){
					outputReaderThread.interrupt();
					errorReaderThread.interrupt();
					Thread.currentThread().interrupt();
				}
			}
		} catch (Exception e) {
			exception=e;
		} finally {
			if(process!=null && Thread.currentThread().isInterrupted()) {
				process.destroy();
			}
		}
	}

	public boolean isError() {
		return exception!=null;
	}

	void setException(Exception exception) {
		this.exception=exception;
	}
	
	public Exception getException() {
		return exception;
	}

}
