package akme.core.io;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;

/**
 * Optimised InputStream using the Java NIO CharBuffer NOT synchronized so it is NOT thread-safe.
 * 
 * @author akme.org
 */
public class ByteBufferInputStream extends InputStream {

	private final ByteBuffer bb;
	
	public ByteBufferInputStream(ByteBuffer bb) {
		this.bb = bb;
	}
	
	@Override
	public int read() {
		return bb.hasRemaining() ? bb.get() : -1;
	}

	@Override
	public int read(byte b[], int off, int len) {
		int n = bb.remaining();
		if (n == 0) return -1;
		if (len < n) n = len;
		bb.get(b, off, n);
		return n;
	}
	
	@Override
	public long skip(long n) {
		if (n < 0L || n > Integer.MAX_VALUE) throw new IllegalArgumentException("skip value is negative or > Integer.MAX_VALUE");
		long len = bb.remaining();
		if (len > 0) bb.position(bb.position()+(int)n);
		return len;
	}
	
	@Override
	public int available() {
		return bb.remaining();
	}
	
	@Override
    public boolean markSupported() {
    	return true;
    }
	
	@Override
	public void mark(int readlimit) {
		// readLimit is useless in this case since the entire stream is a buffer.
		bb.mark();
	}

	public void mark() {
		bb.mark();
	}

	/**
	 * This will reset to a given mark or, if there was no mark, to the start of the buffer/stream.
	 */
	@Override
	public void reset() {
		// Unfortunately ByteBuffer does not support hasMark() to detect if a mark was set.
    	try { bb.reset(); }
    	catch (InvalidMarkException ex) { bb.position(0); }
	}

}
