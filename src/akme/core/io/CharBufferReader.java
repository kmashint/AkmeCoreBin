package akme.core.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.InvalidMarkException;

/**
 * Optimised Reader using the Java NIO CharBuffer.
 * 
 * @author keith.mashinter
 */
public class CharBufferReader extends Reader {
	
	private final CharBuffer cb;
	
	public CharBufferReader(CharBuffer cb) {
		this.cb = cb;
	}

	@Override
	public void close() {
	}

	@Override
	public int read(char[] cbuf, int off, int len) {
		int n = cb.remaining();
		if (n == 0) return -1;
		if (len < n) n = len;
		cb.get(cbuf, off, n);
		return n;
	}
	
	@Override
	public int read(CharBuffer target) {
		int n = cb.remaining();
		if (n == 0) return -1;
		target.put(cb);
		return n;
	}
	
	@Override
	public long skip(long n) {
		if (n < 0L || n > Integer.MAX_VALUE) throw new IllegalArgumentException("skip value is negative or >Integer.MAX_VALUE");
		long len = cb.remaining();
		if (len > 0) cb.position(cb.position()+(int)n);
		return len;
	}

	@Override
    public boolean ready() {
    	return true;
    }
    
	@Override
    public boolean markSupported() {
    	return true;
    }
    
	@Override
    public void mark(int readAheadLimit) {
    	cb.mark();
    }
    
	@Override
    public void reset() {
    	try { cb.reset(); }
    	catch (InvalidMarkException ex) { cb.rewind(); }
    }
	
	
    /**
     * Reads a line of text.  A line is considered to be terminated by any one
     * of a line feed ('\n'), a carriage return ('\r'), or a carriage return
     * followed immediately by a linefeed.
     *
     * @return     A NIO CharSequence containing the contents of the line, not including
     *             any line-termination characters, or null if the end of the
     *             stream has been reached
     */
    public CharSequence readLineSequence() throws IOException {
		int pos1 = cb.position(), pos2 = cb.limit(), eol = 0;
		for (int i=pos1; i<pos2; i++) {
			char c = cb.get();
			if (c == '\r' || c == '\n') {
				eol++;
				if (i+1 < pos2 && cb.get() == '\n') eol++;
				pos2 = i;
				break;
			}
		}
		cb.position(pos2+eol);
		return cb.subSequence(pos1, pos2);
    }

    /**
     * Reads a line of text.  A line is considered to be terminated by any one
     * of a line feed ('\n'), a carriage return ('\r'), or a carriage return
     * followed immediately by a linefeed.
     *
     * @return     A String containing the contents of the line, not including
     *             any line-termination characters, or null if the end of the
     *             stream has been reached
     */
    public String readLine() throws IOException {
		return readLineSequence().toString();
    }
    
}
