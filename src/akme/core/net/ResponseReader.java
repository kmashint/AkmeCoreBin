package akme.core.net;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.CharBuffer;

/**
 * Encapsulate the response body as a text Reader and some header details about the content.
 */
public class ResponseReader extends Reader implements ResponseStream {
	
	final int status;
	final String statusText;
	final int contentLength;
	final String contentType;
	final long lastModified; 
	final Reader stream;
	final Reader error;
	final URLConnection conn;
	
	public ResponseReader(final int status, final String statusText, 
			final int contentLength, final String contentType,
			final long lastModified, final Reader stream) {
		this.status = status;
		this.statusText = statusText;
		this.contentLength = contentLength;
		this.contentType = contentType;
		this.lastModified = lastModified;
		this.stream = stream;
		this.error = null;
		this.conn = null;
	}

	public ResponseReader(final URLConnection conn) throws IOException {
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
		String charset = null;
		if (this.contentType != null) {
			int pos1 = this.contentType.indexOf(';');
			if (pos1 != -1) pos1 = this.contentType.indexOf("charset=", pos1+1);
			if (pos1 != -1) {
				charset = this.contentType.substring(pos1+8).trim();
			}
		}
		if (conn.getInputStream() != null) {
			this.stream = charset != null ? 
					new InputStreamReader(conn.getInputStream(), charset) :
					new InputStreamReader(conn.getInputStream());
		} else {
			this.stream = null;
		}
		if (conn instanceof HttpURLConnection && this.status >= 400 && 
				((HttpURLConnection)conn).getErrorStream() != null) {
			this.error = charset != null ? 
					new InputStreamReader(((HttpURLConnection)conn).getErrorStream(), charset) :
					new InputStreamReader(((HttpURLConnection)conn).getErrorStream());
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

	public Reader getReader() {
		return stream;
	}

	public Reader getErrorReader() {
		return error;
	}
	
	public URLConnection getURLConnection() {
		return conn;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		return stream.read(cbuf, off, len);
	}

	@Override
	public int read(char[] cbuf) throws IOException {
		return stream.read(cbuf);
	}

	@Override
	public int read(CharBuffer cbuf) throws IOException {
		return stream.read(cbuf);
	}

	@Override
	public int read() throws IOException {
		return stream.read();
	}

	@Override
	public void mark(int readAheadLimit) throws IOException {
		stream.mark(readAheadLimit);
	}

	@Override
	public boolean markSupported() {
		return stream.markSupported();
	}

	@Override
	public boolean ready() throws IOException {
		return stream.ready();
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
