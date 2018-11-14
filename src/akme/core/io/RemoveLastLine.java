package akme.core.io;

import java.io.*;


/**
 * Remove the last non-blank line from a file given a maximum line length to check,
 * writing the last line and any surrounding blank line on each side to a separate file. 
 * e.g. If the file ends with "\r\n\r\n123 rows\r\n\r\n" then the separate file will contain "\r\n123 rows\r\n\r\n".
 * 
 * <pre><code>java frameworks.core.io.RemoveLastLine "input.txt" 8192 "input-tail.txt"</code><pre>
 * 
 * javac RemoveLastLine.java
 * 
 */
public class RemoveLastLine {
	
	public static void main(final String[] args) {
		try { go(args[0], Integer.valueOf(args[1]).intValue(), args.length > 2 ? args[2] : null); }
		catch (IOException ex) { ex.printStackTrace(); }
	}
	
	public static final void closeQuiet(final Closeable stream) {
		try { if (stream != null) stream.close(); }
		catch (IOException ex) { ex.printStackTrace(); }
	}
	
	public static void go(final String inputName, final int maxLength, final String outputName) throws IOException {
		RandomAccessFile raf = null;
		BufferedOutputStream ous = null;
		try {
			raf = new RandomAccessFile(inputName, "rw");
			if (outputName != null) ous = new BufferedOutputStream(new FileOutputStream(outputName));
			final long fileLength = raf.length();
			final byte[] buf = new byte[(int)(fileLength < (long)maxLength ? fileLength : (long)maxLength)];
			final int newlineLength;
			if (fileLength > 2) {
				raf.read();
				raf.seek(fileLength - buf.length);
				raf.readFully(buf);
				if (buf[buf.length-1] == '\n') newlineLength = (buf[buf.length-2] == '\r') ? 2 : 1;
				else newlineLength = 0;
			} else {
				newlineLength = 0;
			}
			int lines = 0;
			if (newlineLength != 0) for (int i = buf.length - 1 - newlineLength; i >= 0; i--) {
				if (buf[i] != '\n') continue;
				if (newlineLength == 1 || (i>0 && buf[i-1] == '\r')) {
					if (lines <= 1) {
						if (i>=1 && buf[i-1] == '\n') i -= 1;
						if (i>=2 && buf[i-1] == '\r' && buf[i-2] == '\n') i -= 2;
						lines++;
						if (lines <= 1) continue;
					}
					i++;
					raf.setLength(fileLength - ((long)buf.length - i));
					if (ous != null) ous.write(buf, i, buf.length - i);
					break;
				}
			}
		}
		finally { 
			closeQuiet(raf);
			closeQuiet(ous);
		}
	}

}