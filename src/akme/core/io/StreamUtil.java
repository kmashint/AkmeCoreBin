package akme.core.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import akme.core.util.ExceptionUtil;

/**
 * IO-Stream oriented utilities.
 * 
 * @author akme.org
 */
public abstract class StreamUtil {

	/**
	 * Close the stream, null-safely and without exception. 
	 */
	public static void closeQuiet(Closeable stream) {
		try { if (stream != null) stream.close(); }
		catch (IOException ex) { System.err.println(ExceptionUtil.getShortStackTraceAndCause(ex)); }
	}
	
	/**
	 * Pipe InputStream to OutputStream using the given buffer and close InputStream but NOT OutputStream.
	 * Use this pipeStream with heap ByteBuffer, NOT a direct one.
	 */
	public static void pipeStream(final InputStream ins, final OutputStream ous, final ByteBuffer bbuf) throws IOException {
		if (ins == null) return;
		final byte[] buf = bbuf.array();
		try { for (int n = 0; (n = ins.read(buf, 0, buf.length)) != -1;) if (ous != null) ous.write(buf, 0, n); }
		finally { StreamUtil.closeQuiet(ins); }
	}

	/**
	 * Pipe readable Channel to writable Channel using the given buffer and close readable but NOT writable.
	 * Use this pipeStream with a heap or direct ByteBuffer.
	 */
	public static void pipeStream(final ReadableByteChannel inc, final WritableByteChannel ouc, final ByteBuffer buf) throws IOException {
		if (inc == null) return;
		try { 
			buf.clear();  // Prepare buffer for use.
			while (inc.read(buf) != -1 || buf.position() != 0) if (ouc != null) {
				buf.flip();
				ouc.write(buf);
				buf.compact();  // In case of partial write.
			} else { 
				buf.clear();
			}
			if (ouc != null) {
				// Make sure the buffer is flushed.
				buf.flip();  // EOF will leave buffer in fill state.
				while (buf.hasRemaining()) ouc.write(buf);
			}
		}
		finally { StreamUtil.closeQuiet(inc); }
	}

}
