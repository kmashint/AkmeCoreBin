package akme.core.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Reads an input stream and, at the same time, writes the same bytes to an output stream.
 * 
 * @author akme.org
 */
public class CopyInputStream extends FilterInputStream {

	private OutputStream outputStream;
	
	public CopyInputStream(InputStream inputStream, OutputStream outputStream) {
		super(inputStream);
		if (outputStream == null) throw new IllegalArgumentException("OutputStream cannot be null");
		this.outputStream = outputStream;
	}
	
	@Override
	public int read() throws IOException {
		int b = super.read();
		if (b > -1) this.outputStream.write(b);
		return b;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int result = super.read(b, off, len);
		if (result > -1) this.outputStream.write(b, off, result);
		return result;
	}

	@Override
	public void close() throws IOException {
		try { super.close(); }
		finally { StreamUtil.closeQuiet(this.outputStream); }
	}
	

}
