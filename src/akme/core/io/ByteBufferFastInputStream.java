package akme.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author keith.mashinter
 */
public class ByteBufferFastInputStream extends InputStream {
	
	private final ByteBufferFast source;
	private int next = 0;
	private int mark = 0;
    protected Object lock;
	
	/**
	 * Creates a byte buffer with the given source.
	 */
	public ByteBufferFastInputStream(ByteBufferFast source) {
		this.source = source;
		this.lock = source;
	}

	/** Check to make sure that the stream has not been closed */
	private void ensureOpen() throws IOException {
		if (source == null)
			throw new IOException("Stream closed");
	}

	/**
	 * Read a single byte.
	 *
	 * @return     The byte read, or -1 if the end of the stream has been
	 *             reached
	 *
	 * @exception  IOException  If an I/O error occurs
	 */
	public int read() throws IOException {
		synchronized (lock) {
			ensureOpen();
			if (next >= source.size())
				return -1;
			return source.getInternalValue()[next++];
		}
	}

	/**
	 * Read bytes into a portion of an array.
	 *
	 * @param      cbuf  Destination buffer
	 * @param      off   Offset at which to start writing bytes
	 * @param      len   Maximum number of bytes to read
	 *
	 * @return     The number of bytes read, or -1 if the end of the
	 *             stream has been reached
	 *
	 * @exception  IOException  If an I/O error occurs
	 */
	public int read(byte bbuf[], int off, int len) throws IOException {
		final int length = source.size();
		synchronized (lock) {
			ensureOpen();
			if ((off < 0) || (off > bbuf.length) || (len < 0) || ((off + len) > bbuf.length) || ((off + len) < 0)) {
				throw new IndexOutOfBoundsException();
			}
			else if (len == 0) {
				return 0;
			}
			if (next >= length)
				return -1;
			int n = Math.min(length - next, len);
			System.arraycopy(source.getInternalValue(), next, bbuf, off, n);
			next += n;
			return n;
		}
	}

	/**
	 * Skip bytes.
	 *
	 * @exception  IOException  If an I/O error occurs
	 */
	public long skip(long ns) throws IOException {
		final int length = source.size();
		synchronized (lock) {
			ensureOpen();
			if (next >= length)
				return 0;
			long n = Math.min(length - next, ns);
			next += n;
			return n;
		}
	}
	
	/**
	 * Tell whether this stream is ready to be read.
	 *
	 * @return True if the next read() is guaranteed not to block for input
	 *
	 * @exception  IOException  If the stream is closed
	 */
	public boolean ready() throws IOException {
		synchronized (lock) {
			ensureOpen();
			return true;
		}
	}

	/**
	 * Tell whether this stream supports the mark() operation, which it does.
	 */
	public boolean markSupported() {
		return true;
	}

	/**
	 * Mark the present position in the stream.  Subsequent calls to reset()
	 * will reposition the stream to this point.
	 *
	 * @param  readAheadLimit  Limit on the number of characters that may be
	 *                         read while still preserving the mark.  Because
	 *                         the stream's input comes from a buffer, there
	 *                         is no actual limit, so this argument must not
	 *                         be negative, but is otherwise ignored.
	 *
	 * @exception  IllegalArgumentException  If readAheadLimit is < 0
	 * @exception  IOException  If an I/O error occurs
	 */
	public void mark(int readAheadLimit) {
		if (readAheadLimit < 0) {
			throw new IllegalArgumentException("Read-ahead limit < 0");
		}
		synchronized (lock) {
			mark = next;
		}
	}

	/**
	 * Reset the stream to the most recent mark, or to the beginning
	 * if it has never been marked.
	 *
	 * @exception  IOException  If an I/O error occurs
	 */
	public void reset() throws IOException {
		synchronized (lock) {
			ensureOpen();
			next = mark;
		}
	}
	
	/**
	 * @see ByteBufferFast#readFrom(InputStream) 
	 */
	public int readFrom(InputStream ins) throws IOException {
		return source.readFrom(ins);
	}
	
	/**
	 * @see ByteBufferFast#writeTo(OutputStream) 
	 */
	public void writeTo(OutputStream ous) throws IOException {
		source.writeTo(ous);
	}
	
	/**
	 * @see ByteBufferFast#writeToAndReset(OutputStream) 
	 */
	public void writeToAndReset(OutputStream ous) throws IOException {
		source.writeToAndReset(ous);
	}
	
	public int size() {
		return source.size();
	}
	
	public ByteBufferFast getInternalBuffer() {
		return source;
	}
	
	public byte[] toByteArray() {
		return source.toByteArray();
	}
	
	public String toString() {
		return source.toString();
	}
	
	public String toString(String enc) throws UnsupportedEncodingException {
		return source.toString(enc);
	}

}
