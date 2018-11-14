package akme.core.io;

import java.io.Writer;
import java.nio.CharBuffer;

/**
 * Optimised Writer using the Java NIO CharBuffer.
 * 
 * @author keith.mashinter
 */
public class CharBufferWriter extends Writer {

	private final CharBuffer cb;
	
	public CharBufferWriter(CharBuffer cb) {
		this.cb = cb;
	}
	
	@Override
	public void close() {
	}

	@Override
	public void flush() {
	}

	@Override
	public void write(char[] cbuf, int off, int len) {
		cb.put(cbuf, off, len);
	}
	
	@Override
	public void write(String str, int off, int len) {
		cb.put(str, off, len);
	}
	
	@Override
	public Writer append(CharSequence csq) {
		cb.append(csq);
		return this;
	}
	
	@Override
	public Writer append(CharSequence csq, int start, int end) {
		cb.append(csq, start, end);
		return this;
	}

}
