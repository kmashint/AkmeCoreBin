package akme.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Use a String as the source of an InputStream, converting characters to bytes along the way.
 * 
 * @author keith.mashinter
 */
public class StringInputStream extends InputStream {

	private static final int DEFAULT_BLOCK_SIZE = 1024;
	
	private final String source;

	private final String charEncoding;

	private final int blockSize;

	private final ByteBufferFastOutputStream byteBuf;
	
	private final OutputStreamWriter streamWriter;

	private int charIdx = 0;
	private int byteBufIdx = 0;
	private int byteBufCount = 0;
	
	public StringInputStream(String source) {
		this(source,null,Math.min((source != null) ? source.length() : 0, DEFAULT_BLOCK_SIZE));
	}

	public StringInputStream(String source, int blockSize) {
		this(source,null,blockSize);
	}

	public StringInputStream(String source, String charEncoding) {
		this(source,charEncoding,Math.min((source != null) ? source.length() : 0, DEFAULT_BLOCK_SIZE));
	}
	
	public StringInputStream(String source, String charEncoding, int blockSize) {
		this.source = source;
		this.charEncoding = charEncoding;
		this.blockSize = blockSize;
		// Character encodings may take up to 4 bytes per character.
		this.byteBuf = new ByteBufferFastOutputStream(blockSize * 4);
		OutputStreamWriter osw; 
		if (this.charEncoding != null) {
			try {
				osw = new OutputStreamWriter(byteBuf,this.charEncoding);
			} 
			catch (UnsupportedEncodingException ex) {
				osw = new OutputStreamWriter(byteBuf);
			}
		} else {
			osw = new OutputStreamWriter(byteBuf);
		}
		this.streamWriter = osw;
	}

	/**
	 * Return the block size for chunked read/write operations.
	 */
	public int getBlockSize() {
		return this.blockSize;
	}
	
	/**
	 * Return the number of bytes that can be retrieved without risking thread 
	 * or i/o synchronization / blocking.
	 * 
	 * @see java.io.InputStream#available()
	 */
	public int available() throws IOException {
		return (byteBufCount - byteBufIdx);
	}

	/**
	 * Read bytes while converting from chars.
	 * This tries to be as efficient as possible in not creating copies of data,
	 * coverting directly to bytes in chunks through OutputStreamWriter.
	 * 
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		int result;
		if (byteBufIdx < byteBufCount) {
			result = byteBuf.getInternalValue()[byteBufIdx++];
		} else {
			final int charLen = source.length();
			if (charIdx < charLen) {
				int charEnd = charIdx + blockSize;
				if (charEnd > charLen) charEnd = charLen;
				// clear the underlying buffer
				byteBuf.reset();
				// write the the string through converter to bytes
				streamWriter.write(source,charIdx,charEnd-charIdx);
				streamWriter.flush();
				charIdx = charEnd;
				// get the number of bytes written
				byteBufCount = byteBuf.size();
				byteBufIdx = 0;
				result = byteBuf.getInternalValue()[byteBufIdx++];
			} else {
				result = -1;
			}
		}
		return result;
	}
		
	public int read(byte[] b, int off, int len) throws IOException {
		int charLen = source.length();
		int bIdx = off;
		byte[] bufAry = byteBuf.getInternalValue();
		boolean todo = (bIdx - off < len);
		while (todo) {
			while (byteBufIdx < byteBufCount && todo) {
				b[bIdx++] = bufAry[byteBufIdx++];
				todo = (bIdx - off < len);
			}
			if (!(charIdx < charLen)) todo = false;
			if (todo) {
				int charEnd = charIdx + (len < blockSize ? len : blockSize);
				if (charEnd > charLen) charEnd = charLen;
				// clear the underlying buffer
				byteBuf.reset();
				// write the the string through converter to bytes
				streamWriter.write(source,charIdx,charEnd-charIdx);
				streamWriter.flush();
				charIdx = charEnd;
				// get the number of bytes written
				byteBufCount = byteBuf.size();
				byteBufIdx = 0;
			}
		}
		if (bIdx > off) return (bIdx - off);
		else return -1;
	}

    /**
     * Cleanup and close any held resource.
     * 
	 * @see java.io.InputStream#close()
	 */
	public void close() throws IOException {
		byteBufCount = 0;
		byteBufIdx = 0;
		streamWriter.close();
		byteBuf.close();
		//source.close();
	}

}
