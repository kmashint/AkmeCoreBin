package akme.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Optimised OutputStream using the Java NIO CharBuffer, NOT synchronized so it is NOT thread-safe.
 * This does not suffer from the synchronized overhead of ByteArrayOutputStream.
 * On writes when full, the internal buffer will be reallocated, 
 * losing any prior mark and limit but keeping the position.
 * 
 * @author akme.org
 */
public class ByteBufferOutputStream extends OutputStream {

	public static final int CAPACITY_DEFAULT = 32;
	public static final int CAPACITY_1K = 1024;
	private final boolean directOffHeap;
	private ByteBuffer bb;
	
	/**
	 * Create a buffer with the given initial size and by default on-heap, not-direct.
	 */
	public ByteBufferOutputStream(int size) {
		this(size, false);
	}
	
	/**
	 * Create a buffer with the given initial size and the ability to specify direct, off-heap memory allocation.
	 * ByteBuffer.allocateDirect() is better for long-lived buffers, use if you know you need it.
	 */
	public ByteBufferOutputStream(int size, boolean directOffHeap) {
		this.directOffHeap = directOffHeap;
		this.bb = directOffHeap ? ByteBuffer.allocateDirect(size) : ByteBuffer.allocate(size);
	}
	
	/**
	 * Ensure there is enough capacity to add the number of remaining items (bytes).
	 * This will reallocate a new Buffer if remaining > existing Buffer remaining().
	 * Reallocating the new Buffer will lose the old mark and limit, but retain the position.
	 * Unfortunately the Buffer interface does not have a way to check if a mark exists
	 * without throwing an Exception.
	 */
	protected void ensureRemaining(int remaining) {
		if (remaining <= bb.remaining()) return;

		final int newLimit = bb.position() + remaining;
		int capacity = bb.capacity();
		if (capacity <= newLimit && capacity < CAPACITY_DEFAULT) {
			capacity = CAPACITY_DEFAULT;
		}
		while (capacity <= newLimit) {
			capacity = capacity << 1;
		}

		bb.flip();
		final ByteBuffer newB = directOffHeap ? ByteBuffer.allocateDirect(capacity) : ByteBuffer.allocate(capacity);
		newB.put(bb);
		bb = newB;
	}
	
	/**
	 * Return a new byte[] with the known contents, from 0 to the current position, of the buffer.
	 * This avoids using flip() since flip() will clear a mark.
	 */
	public byte[] toArray() {
		final int pos = bb.position(), lim = bb.limit();
		bb.position(0);
		bb.limit(pos);
		final byte[] ba = new byte[pos];
		bb.get(ba);
		bb.position(pos);
		bb.limit(lim);
		return ba;
	}

	/**
	 * Return a String from ByteBuffer using the given Charset.
	 */
	public String toString(Charset charset) {
		if (bb.hasArray()) {
			return new String(bb.array(), 0, bb.position(), charset);
		} else {
			return new String(toArray(), charset);
		}
	}
	
	/**
	 * Return a String from ByteBuffer using the given charsetName.
	 */
    public String toString(String charsetName)
            throws UnsupportedEncodingException {
    	return toString(Charset.forName(charsetName));
    }

	/**
	 * Return the underlying ByteBuffer.
	 * This may change over time as the ByteBuffer is reallocated and increased.
	 */
	public ByteBuffer buffer() {
		return bb;
	}
	
	/**
	 * Write the given byte to the internal buffer.
	 * If full, the internal buffer will be reallocated, 
	 * losing any prior mark and limit but keeping the position.
	 */
	@Override
	public void write(int b) throws IOException {
		ensureRemaining(1);
		bb.put((byte)b);
	}
	
	/**
	 * Write the given byte to the internal buffer.
	 * If full, the internal buffer will be reallocated, 
	 * losing any prior mark and limit but keeping the position.
	 */
	@Override
	public void write(byte b[], int off, int len) {
		ensureRemaining(len);
		bb.put(b, off, len);
	}

}
