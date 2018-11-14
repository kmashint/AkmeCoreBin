package akme.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import akme.core.util.ArrayUtil;

/**
 * Acts like a <CODE>StringBuffer</CODE> but works with <CODE>byte</CODE> arrays.
 *
 * @author Original code by AKME Solutions
 * @author $Author: keith.mashinter $
 * @version $Date: 2007/04/30 15:55:35 $
 * $NoKeywords: $
 */

public class ByteBufferFast {

	//private static final char[] digitChars = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
	private static final byte[] hexBytes = new byte[] {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};

	/** The count of bytes in the buffer. */
	int count;

	/** The buffer where the bytes are stored. */
	byte buf[];

	/** Block size for chunked read/write operations. */
	private int blockSize;

	private final String charEncoding;

	/** Creates new ByteBuffer with capacity 128 */
	public ByteBufferFast() {
		this(128,null);
	}

	/**
	 * Creates a byte buffer with a certain capacity.
	 * @param size the initial capacity
	 */
	public ByteBufferFast(int size) {
		this(size,null);
	}

	/**
	 * Creates a byte buffer with a certain capacity and character encoding.
	 * @param size the initial capacity
	 */
	public ByteBufferFast(int size, String charEncoding) {
		if (size < 1) size = 128;
		this.buf = new byte[size];
		this.blockSize = size;
		this.charEncoding = charEncoding;
	}
	
	/**
	 * Return the character encoding.
	 */
	public String getCharacterEncoding() {
		return this.charEncoding;
	}

	/**
	 * Return the block size for chunked read/write operations.
	 */
	public int getBlockSize() {
		return this.blockSize;
	}

	/**
	 * Sets the block size for chunked read/write operations.
	 */
	public void setBlockSize(int size) {
		this.blockSize = size;
	}

	/**
	  * Ensures that the capacity of the buffer is at least equal to the
	  * specified minimum.
	  * If the current capacity of this string buffer is less than the
	  * argument, then a new internal buffer is allocated with greater
	  * capacity. The new capacity is the larger of:
	  * <ul>
	  * <li>The <code>minimumCapacity</code> argument.
	  * <li>Twice the old capacity, plus <code>2</code>.
	  * </ul>
	  * If the <code>minimumCapacity</code> argument is nonpositive, this
	  * method takes no action and simply returns.
	  *
	  * @param   minimumCapacity   the minimum desired capacity.
	  */
	 public void ensureCapacity(int minSize) {
		 if (minSize > buf.length) {
			byte newbuf[] = new byte[Math.max(buf.length << 1, minSize)];
			System.arraycopy(buf, 0, newbuf, 0, count);
			buf = newbuf;
		 }
	 }

	/**
	 * Appends a <CODE>int</CODE> casting to byte. The size of the array will grow by one.
	 * @param b the int to be appended
	 * @return a reference to this <CODE>ByteBuffer</CODE> object
	 */
	public ByteBufferFast append(int b) {
		int newcount = count + 1;
		ensureCapacity(newcount);
		buf[count] = (byte)b;
		count = newcount;
		return this;
	}

	/**
	 * Appends the subarray of the <CODE>byte</CODE> array. The buffer will grow by
	 * <CODE>len</CODE> bytes.
	 * @param b the array to be appended
	 * @param off the offset to the start of the array
	 * @param len the length of bytes to append
	 * @return a reference to this <CODE>ByteBuffer</CODE> object
	 */
	public ByteBufferFast append(byte b[], int off, int len) {
		if ((off < 0) || (off > b.length) || (len < 0) ||
		((off + len) > b.length) || ((off + len) < 0) || len == 0)
			return this;
		int newcount = count + len;
		ensureCapacity(newcount);
		System.arraycopy(b, off, buf, count, len);
		count = newcount;
		return this;
	}

	/**
	 * Appends an array of bytes.
	 * @param b the array to be appended
	 * @return a reference to this <CODE>ByteBuffer</CODE> object
	 */
	public ByteBufferFast append(byte b[]) {
		return append(b, 0, b.length);
	}

	/**
	 * Appends a <CODE>String</CODE> to the buffer. The <CODE>String</CODE> is
	 * converted according to the default character encoding.
	 * @param str the <CODE>String</CODE> to be appended
	 * @return a reference to this <CODE>ByteBuffer</CODE> object
	 */
	public ByteBufferFast append(String str) {
		if (str != null) {
			if (this.charEncoding != null) {
				append(str.getBytes());
			} else {
				try {
					append(str.getBytes(this.charEncoding));
				} catch (UnsupportedEncodingException ex) {
					ex.printStackTrace();
					append(str.getBytes());
				}
			}
		}
		return this;
	}

	/**
	 * Appends another <CODE>ByteBuffer</CODE> to this buffer.
	 * @param buf the <CODE>ByteBuffer</CODE> to be appended
	 * @return a reference to this <CODE>ByteBuffer</CODE> object
	 */
	public ByteBufferFast append(ByteBufferFast buf) {
		return append(buf.buf, 0, buf.count);
	}

	public ByteBufferFast appendHex(byte b) {
		append(hexBytes[(b >> 4) & 0x0f]);
		return append(hexBytes[b & 0x0f]);
	}

	/**
	 * Set the count to zero, essentially clearing the buffer.
	 */
	public void reset() {
		count = 0;
	}
	
	/**
     * Returns the index within this buffer of the first occurrence of the
     * specified byte, starting the search at the specified index.
     * Return -1 if not found.
     */
	public int indexOf(byte b, int fromIndex) {
		if (fromIndex >= count) return -1;
		for (int i=fromIndex < 0 ? 0 : fromIndex; i<count; i++) if (buf[i] == b) return i;
		return -1;
	}

	/**
	 * Find the first occurrance of the given byte[] moving right from the start position. 
	 */
	public int indexOf(final byte[] search, final int start) {
		if (search == null || search.length == 0) return -1;
		final int slen = search.length;
		final int bstop = count - slen;
		final byte s0 = search[0];
		byte bb;
		int result = -1;
		for (int i1=start; result == -1 && i1 <= bstop; ++i1) {
			if (buf[i1] == s0) {
				result = i1;
				for (int i2=i1+1, j=1; i2 < bstop && j < slen; ++i2, ++j) {
					bb = buf[i2];
					// Check for the next possible match.
					if (i1 == i2 && bb == s0) i1 = i2;
					// Check the current match.
					if (bb != search[j]) {
						result = -1;
						break;
					}
				}
			}
		}
		return result;
	}
	
	public int indexOf(final byte[] search) {
		return indexOf(search, 0);
	}

	/**
     * Returns the index within this buffer of the last occurrence of the
     * specified byte, starting the search backward at the specified index.
     * Return -1 if not found.
     */
	public int lastIndexOf(byte b, int fromIndex) {
		if (fromIndex > count) return -1;
		for (int i=fromIndex; i>=0; i--) if (buf[i] == b) return i;
		return -1;
	}
	
	/**
	 * Find the first occurrence of the given byte[] moving left from the start position. 
	 */
	public int lastIndexOf(final byte[] search, final int start) {
		if (search == null || search.length == 0) return -1;
		final int slen = search.length;
		final int bstop = slen - 1;
		final byte s0 = search[slen-1];
		byte bb;
		int result = -1;
		for (int i1=start; result == -1 && i1 >= bstop; --i1) {
			if (buf[i1] == s0) {
				result = i1 - slen + 1;
				for (int i2=i1-1, j=slen-2; i2 >= 0 && j >= 0; --i2, --j) {
					bb = buf[i2];
					// Check for the next possible match.
					if (i1 == i2 && bb == s0) i1 = i2;
					// Check the current match.
					if (bb != search[j]) {
						result = -1;
						break;
					}
				}
			}
		}
		return result;
	}
	
	public int lastIndexOf(final byte[] search) {
		return lastIndexOf(search, count-1);
	}

	/**
	 * Creates a newly allocated byte array. Its size is the current
	 * size of this output stream and the valid contents of the buffer
	 * have been copied into it.
	 *
	 * @return  the current contents of this output stream, as a byte array.
	 */
	public byte[] toByteArray() {
		byte newbuf[] = new byte[count];
		System.arraycopy(buf, 0, newbuf, 0, count);
		return newbuf;
	}
	
	/**
	 * Return a subarray of bytes from the current buffer by exclusive fromIndex and inclusive toIndex,
	 * similar to substring for Strings. 
	 * 
	 * @param fromIndex Inclusive.
	 * @param toIndex Exclusive.
	 */
	public byte[] subarray(int fromIndex, int toIndex) {
		if (toIndex > count) throw new ArrayIndexOutOfBoundsException("toIndex "+ toIndex);
		else if (count == 0 && fromIndex == 0 && toIndex == 0) return ArrayUtil.EMPTY_BYTE_ARRAY;
		else {
			byte newbuf[] = new byte[toIndex-fromIndex];
			System.arraycopy(buf, fromIndex, newbuf, 0, newbuf.length);
			return newbuf;
		}
	}

	/**
	 * Returns the current size of the buffer.
	 *
	 * @return the value of the <code>count</code> field, which is the number of valid bytes in this byte buffer.
	 */
	public int size() {
		return count;
	}

	/**
	 * Converts the buffer's contents into a string, translating bytes into
	 * characters according to the platform's default character encoding.
	 *
	 * @return String translated from the buffer's contents.
	 */
	public String toString() {
		return new String(buf, 0, count);
	}

	/**
	 * Converts the buffer's contents into a string, translating bytes into
	 * characters according to the specified character encoding.
	 *
	 * @param   enc  a character-encoding name.
	 * @return String translated from the buffer's contents.
	 * @throws UnsupportedEncodingException
	 *         If the named encoding is not supported.
	 */
	public String toString(String enc) throws UnsupportedEncodingException {
		return new String(buf, 0, count, enc);
	}

	/**
	 * Read from available bytes from the of this byte buffer output to
	 * the specified output stream argument, as if by calling the output
	 * stream's write method using <code>in.read(buf, 0, count)</code>.
	 * 
	 *
	 * @param      	in   the input stream from which data will be read.
	 * @return		number of bytes read of -1 at end-of-stream.
	 * @exception  	IOException  if an I/O error occurs.
	 */
	public int readFrom(InputStream in) throws IOException {
		ensureCapacity(count + blockSize);
		int len = in.read(buf, count, buf.length - count);
		if (len > 0) count += len;
		return len;
	}

	/**
	 * Writes the complete contents of this byte buffer output to
	 * the specified output stream argument, as if by calling the output
	 * stream's write method using <code>out.write(buf, 0, count)</code>.
	 * This will keep the content, rather than reset/empty the buffer,
	 * which is good for writing to multiple output streams and matches
	 * the writeTo() of the ByteArrayOutputStream.
	 *
	 * @param      out   the output stream to which to write the data.
	 * @exception  IOException  if an I/O error occurs.
	 */
	public void writeTo(OutputStream out) throws IOException {
		out.write(buf, 0, count);
	}

	/**
	 * Writes the complete contents of this byte buffer output to
	 * the specified output stream argument, as if by calling the output
	 * stream's write method using <code>out.write(buf, 0, count)</code>.
	 * This will reset/empty the buffer.
	 *
	 * @param      out   the output stream to which to write the data.
	 * @exception  IOException  if an I/O error occurs.
	 */
	public void writeToAndReset(OutputStream out) throws IOException {
		out.write(buf, 0, count);
		reset();
	}

	//
	// The following two methods are needed by String to efficiently
	// convert a StringBufferFast into a String.  They are not public.
	// They shouldn't be called by anyone but String.
	final byte[] getInternalValue() { return buf; }

}
