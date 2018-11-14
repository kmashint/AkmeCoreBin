package akme.core.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * Line-reading stream optimized for ASCII-based single-byte character sets such as:
 * ASCII, ISO-8859-1 (no Euro), ISO-8859-15 (has Euro), Windows-1252 (has Euro).
 * It has better performance since there is no mult-byte character conversion.
 * Multi-byte character set such as UTF-8 will fail with an UnsupportedEncodingException.
 * 
 * For multi-byte character sets such as Unicode UTF-8, use the Java recommendation of
 * <code>new BufferedReader(new InputStreamReader(InputStream ins))</code> or
 * <code>new BufferedReader(new InputStreamReader(InputStream ins, String encoding))</code>.
 * 
 * @author keith.mashinter
 */
public class BufferedLineInputStream extends BufferedInputStream {
	
	public static final Charset ASCII = Charset.forName("ASCII");
	
	private static void throwIfNotSupported(String encoding) throws UnsupportedEncodingException {
		if (!isEncodingSupported(encoding)) {
			throw new UnsupportedEncodingException(encoding 
					+ " is not supported since it is not an ASCII-based single-byte encoding.");
		}
	}

	/** 
	 * Check if the given encoding (e.g. "windows-1252") is supported.
	 */
	public static boolean isEncodingSupported(String encoding) {
		Charset cset = Charset.forName(encoding);
		CharsetEncoder cenc = cset.newEncoder();
		return (cset.contains(ASCII) && cenc.maxBytesPerChar() == 1.0 && cenc.averageBytesPerChar() == 1.0);
	}

	private final String encoding;
	private final ByteBufferFast line;
	
	public BufferedLineInputStream(InputStream ins, String encoding) throws UnsupportedEncodingException {
		super(ins);
		throwIfNotSupported(encoding);
		this.encoding = encoding;
		this.line = new ByteBufferFast();
	}
	
	public BufferedLineInputStream(InputStream ins, int size, String encoding) throws UnsupportedEncodingException {
		super(ins, size);
		throwIfNotSupported(encoding);
		this.encoding = encoding;
		this.line = new ByteBufferFast(size);
	}
    
	/**
	 * Internal details of reading a single-byte ASCII stream while leveraging the underlying buffer.
	 */
	private int readLine1() throws IOException {
		line.reset();
    	int b = 0, b2;
    	boolean loop = true;
		while (loop && (b = read()) != -1) {
			while (loop) {
				if (b == '\n') {
					loop = false;
					break;
				}
				else if (b == '\r') {
					if (this.pos < this.count) {
						if (this.buf[this.pos] == '\n') this.pos++;
					} else {
						mark(1);
						b2 = read(); 
						if (b2 != '\n') reset();
					}
					loop = false;
					break;
				}
				if (line.count < line.buf.length) line.buf[line.count++] = (byte) b;
				else line.append(b);
				if (this.pos < this.count) b = this.buf[this.pos++];
				else break;
			}
		}
		return b;
	}
	
	/**
	 * Read up to the next newline (\r, \n, or \r\n) and return as an array of bytes.
	 */
	public byte[] readLineBytes() throws IOException {
		return (readLine1() != -1 || line.size() > 0) ? line.toByteArray() : null;
	}

	/**
	 * Read up to the next newline (\r, \n, or \r\n) and return as a String 
	 * using the character encoding given in the constructor.
	 */
	public String readLine() throws IOException {
		return (readLine1() != -1 || line.size() > 0) ? line.toString(this.encoding) : null;
	}
	
	/**
	 * Read up to the next newline (\r, \n, or \r\n) and return as a String 
	 * using the given character encoding.
	 */
	public String readLine(String encoding) throws IOException {
		return (readLine1() != -1 || line.size() > 0) ? line.toString(encoding) : null;
	}
	
	/**
	 * Get the line buffer, not null.
	 */
	public ByteBufferFast getLineBuffer() {
		return this.line;
	}
	
}
