package akme.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author keith.mashinter
 */
public class ByteBufferFastOutputStream extends OutputStream {
	
	private final ByteBufferFast sink;
	
	/**
	 * Creates a byte buffer with a default capacity of ByteBufferFast.
	 */
	public ByteBufferFastOutputStream() {
		this.sink = new ByteBufferFast();
	}
	
	/**
	 * Creates a byte buffer with a certain capacity.
	 * @param size the initial capacity
	 */
	public ByteBufferFastOutputStream(int size) {
		this(size,null);
	}
	
	/**
	 * Creates a byte buffer with a certain capacity and character encoding.
	 * @param size the initial capacity
	 */
	public ByteBufferFastOutputStream(int size, String charEncoding) {
		this.sink = new ByteBufferFast(size,charEncoding);
	}

	/**
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException {
		sink.append(b);
	}

	/**
	 * @see java.io.OutputStream#write(byte[],int,int)
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		sink.append(b, off, len);
	}

	public int readFrom(InputStream ins) throws IOException {
		return sink.readFrom(ins);
	}
	
	public void writeTo(OutputStream ous) throws IOException {
		sink.writeTo(ous);
	}
	
	public void writeToAndReset(OutputStream ous) throws IOException {
		sink.writeToAndReset(ous);
	}
	
	public void reset() {
		sink.reset();
	}
	
	public int size() {
		return sink.size();
	}
	
	public ByteBufferFast getInternalBuffer() {
		return sink;
	}
	
	public byte[] toByteArray() {
		return sink.toByteArray();
	}
	
	public String toString() {
		return sink.toString();
	}
	
	public String toString(String enc) throws UnsupportedEncodingException {
		return sink.toString(enc);
	}
	
	byte[] getInternalValue() {
		return sink.getInternalValue();
	}

}
