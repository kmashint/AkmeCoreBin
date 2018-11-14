package akme.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;

/**
 * Encapsulate the response body as a binary InputStream and some header details about the content.
 */
public class ResponseInputStream extends InputStream implements ResponseStream {
	
	final int status;
	final String statusText;
	final int contentLength;
	final String contentType; 
	final long lastModified; 
	final InputStream stream;
	final InputStream error;
	final URLConnection conn;
	
	public ResponseInputStream(final int status, final String statusText, 
			final int contentLength, final String contentType,
			final long lastModified, final InputStream stream) {
		this.status = status;
		this.statusText = statusText;
		this.contentLength = contentLength;
		this.contentType = contentType;
		this.lastModified = lastModified;
		this.stream = stream;
		this.error = null;
		this.conn = null;
	}
	
	public ResponseInputStream(final URLConnection conn) throws IOException {
		this.conn = conn;
		if (conn instanceof HttpURLConnection) {
			this.status = ((HttpURLConnection)conn).getResponseCode();
			this.statusText = ((HttpURLConnection)conn).getResponseMessage();
		} else {
			this.status = 0;
			this.statusText = null;
		}
		this.contentLength = conn.getContentLength();
		this.contentType = conn.getContentType();
		this.lastModified = conn.getLastModified();
		this.stream = conn.getInputStream();
		if (conn instanceof HttpURLConnection && this.status >= 400) {
			this.error = ((HttpURLConnection)conn).getErrorStream();
		} else {
			this.error = null;
		}
	}
	
	public int getStatus() {
		return status;
	}

	public String getStatusText() {
		return statusText;
	}
	
	public int getContentLength() {
		return status;
	}

	public String getContentType() {
		return statusText;
	}
	
	public long getLastModified() {
		return lastModified;
	}

	public InputStream getReader() {
		return stream;
	}

	public InputStream getErrorReader() {
		return error;
	}

	public URLConnection getURLConnection() {
		return conn;
	}

	@Override
	public int read(byte[] cbuf, int off, int len) throws IOException {
		return stream.read(cbuf, off, len);
	}

	@Override
	public int read(byte[] cbuf) throws IOException {
		return stream.read(cbuf);
	}

	@Override
	public int read() throws IOException {
		return stream.read();
	}

	@Override
	public void mark(int readAheadLimit) {
		stream.mark(readAheadLimit);
	}

	@Override
	public boolean markSupported() {
		return stream.markSupported();
	}

	@Override
	public int available() throws IOException {
		return stream.available();
	}

	@Override
	public void reset() throws IOException {
		stream.reset();
	}

	@Override
	public long skip(long n) throws IOException {
		return stream.skip(n);
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}

}
