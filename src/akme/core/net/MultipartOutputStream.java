package akme.core.net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Wrap an OutputStream for use with the multipart/form-data MIME-type.
 * @see similar in Apache Wicket
 */
public class MultipartOutputStream {
	
	private static final String NEWLINE = "\r\n";

	private static final String PREFIX = "--";

	/**
	 * Creates a new <code>java.net.URLConnection</code> object from the
	 * specified <code>java.net.URL</code>. This is a convenience method
	 * which will set the <code>doInput</code>, <code>doOutput</code>,
	 * <code>useCaches</code> and <code>defaultUseCaches</code> fields to
	 * the appropriate settings in the correct order.
	 */
	public static URLConnection createConnection(URL url) throws java.io.IOException {
		URLConnection urlConn = url.openConnection();
		if (urlConn instanceof HttpURLConnection) {
			HttpURLConnection httpConn = (HttpURLConnection)urlConn;
			httpConn.setRequestMethod("POST");
		}
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
		urlConn.setUseCaches(false);
		urlConn.setDefaultUseCaches(false);
		return urlConn;
	}

	/**
	 * Creates a multipart boundary string by concatenating 20 hyphens (-) and
	 * the hexadecimal (base-16) representation of the current time in
	 * milliseconds.
	 */
	public static String createBoundary() {
		return "--------------------" + Long.toString(System.currentTimeMillis(), 16);
	}

	/**
	 * Gets the content type string suitable for the
	 * <code>java.net.URLConnection</code> which includes the multipart
	 * boundary string. <br />
	 * <br />
	 * This method is static because, due to the nature of the
	 * <code>java.net.URLConnection</code> class, once the output stream for
	 * the connection is acquired, it's too late to set the content type (or any
	 * other request parameter). So one has to create a multipart boundary
	 * string first before using this class, such as with the
	 * <code>createBoundary()</code> method.
	 */
	public static String getContentType(String boundary) {
		return "multipart/form-data; boundary=" + boundary;
	}
	
	// End of static, start of instance.
	
	/** The output stream for writing. */
	private DataOutputStream out = null;

	/** The multipart boundary string. */
	private String boundary = null;

	/** The buffer size for shuffling bytes. */
	private int bufferSize = 1024;

	/**
	 * Wrap an OutputStream for use with the multipart/form-data MIME-type.
	 */
	public MultipartOutputStream(final OutputStream os, final String boundary) {
		if (os == null) {
			throw new IllegalArgumentException("Output stream is required.");
		}
		if (boundary == null || boundary.length() == 0) {
			throw new IllegalArgumentException("Boundary stream is required.");
		}
		if (os instanceof DataOutputStream) this.out = (DataOutputStream)os;
		else this.out = new DataOutputStream(os);
		this.boundary = boundary;
	}

	public void writeField(String name, boolean value) throws java.io.IOException {
		writeField(name, new Boolean(value).toString());
	}

	public void writeField(String name, double value) throws java.io.IOException {
		writeField(name, Double.toString(value));
	}

	public void writeField(String name, float value) throws java.io.IOException {
		writeField(name, Float.toString(value));
	}

	public void writeField(String name, long value) throws java.io.IOException {
		writeField(name, Long.toString(value));
	}

	public void writeField(String name, int value) throws java.io.IOException {
		writeField(name, Integer.toString(value));
	}

	public void writeField(String name, short value) throws java.io.IOException {
		writeField(name, Short.toString(value));
	}

	public void writeField(String name, char value) throws java.io.IOException {
		writeField(name, new Character(value).toString());
	}

	/**
	 * Writes an string field value. 
	 * Sends an empty string ("") if the value is null.
	 */
	public void writeField(String name, String value) throws java.io.IOException {
		if (name == null) {
			throw new IllegalArgumentException("Name cannot be null or empty.");
		}
		if (value == null) {
			value = "";
		}
		/*
		 * --boundary\r\n Content-Disposition: form-data; name="<fieldName>"\r\n
		 * \r\n <value>\r\n
		 */
		// write boundary
		out.writeBytes(PREFIX);
		out.writeBytes(boundary);
		out.writeBytes(NEWLINE);
		// write content header
		out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"");
		out.writeBytes(NEWLINE);
		out.writeBytes(NEWLINE);
		// write content
		out.writeBytes(value);
		out.writeBytes(NEWLINE);
		out.flush();
	}

	/**
	 * Write the contents of a file. 
	 * Throws <code>java.lang.IllegalArgumentException</code> if the file is null, 
	 * does not exist, or is a directory.
	 */
	public void writeFile(String name, String mimeType, File file) throws java.io.IOException {
		if (file == null) {
			throw new IllegalArgumentException("File cannot be null.");
		}
		if (!file.exists()) {
			throw new IllegalArgumentException("File does not exist.");
		}
		if (file.isDirectory()) {
			throw new IllegalArgumentException("File cannot be a directory.");
		}
		writeFile(name, mimeType, file.getCanonicalPath(), new FileInputStream(file));
	}

	/**
	 * Writes a input stream's contents. 
	 * Throws <code>java.lang.IllegalArgumentException</code> if the stream is null.
	 */
	public void writeFile(String name, String mimeType, String fileName, InputStream is)
			throws java.io.IOException {
		if (is == null) {
			throw new IllegalArgumentException("Input stream cannot be null.");
		}
		if (fileName == null || fileName.length() == 0) {
			throw new IllegalArgumentException("File name cannot be null or empty.");
		}
		/*
		 * --boundary\r\n Content-Disposition: form-data; name="<fieldName>";
		 * filename="<filename>"\r\n Content-Type: <mime-type>\r\n \r\n
		 * <file-data>\r\n
		 */
		// write boundary
		out.writeBytes(PREFIX);
		out.writeBytes(boundary);
		out.writeBytes(NEWLINE);
		// write content header
		out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\""
				+ fileName + "\"");
		out.writeBytes(NEWLINE);
		if (mimeType != null) {
			out.writeBytes("Content-Type: " + mimeType);
			out.writeBytes(NEWLINE);
		}
		out.writeBytes(NEWLINE);
		// write content
		byte[] data = new byte[bufferSize];
		int r = 0;
		while ((r = is.read(data, 0, data.length)) != -1) {
			out.write(data, 0, r);
		}
		// close input stream, but ignore any possible exception for it
		try {
			is.close();
		}
		catch (Exception e) {
		}
		out.writeBytes(NEWLINE);
		out.flush();
	}

	/**
	 * Writes the given bytes. The bytes are assumed to be the contents of a
	 * file, and will be sent as such. 
	 * Throws <code>java.lang.IllegalArgumentException</code> if the data is null.
	 */
	public void writeFile(String name, String mimeType, String fileName, byte[] data)
			throws java.io.IOException {
		if (data == null) {
			throw new IllegalArgumentException("Data cannot be null.");
		}
		if (fileName == null || fileName.length() == 0) {
			throw new IllegalArgumentException("File name cannot be null or empty.");
		}
		/*
		 * --boundary\r\n Content-Disposition: form-data; name="<fieldName>";
		 * filename="<filename>"\r\n Content-Type: <mime-type>\r\n \r\n
		 * <file-data>\r\n
		 */
		// write boundary
		out.writeBytes(PREFIX);
		out.writeBytes(boundary);
		out.writeBytes(NEWLINE);
		// write content header
		out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\""
				+ fileName + "\"");
		out.writeBytes(NEWLINE);
		if (mimeType != null) {
			out.writeBytes("Content-Type: " + mimeType);
			out.writeBytes(NEWLINE);
		}
		out.writeBytes(NEWLINE);
		// write content
		out.write(data, 0, data.length);
		out.writeBytes(NEWLINE);
		out.flush();
	}

	/**
	 * Actually, this method does nothing, as write
	 * methods are highly specialized and automatically flush.
	 */
	public void flush() throws java.io.IOException {
		// out.flush();
	}

	/**
	 * Closes the stream. <br />
	 * <br />
	 * <b>NOTE:</b> This method <b>MUST</b> be called to finalize the multipart stream.
	 */
	public void close() throws java.io.IOException {
		// write final boundary
		out.writeBytes(PREFIX);
		out.writeBytes(boundary);
		out.writeBytes(PREFIX);
		out.writeBytes(NEWLINE);
		out.flush();
		out.close();
	}

	/**
	 * Gets the multipart boundary string being used by this stream.
	 * 
	 * @return the boundary
	 */
	public String getBoundary() {
		return this.boundary;
	}


}
