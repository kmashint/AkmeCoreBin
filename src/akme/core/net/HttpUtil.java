package akme.core.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;

import akme.core.io.StreamUtil;

/**
 * HTTP Utility methods. 
 */
public abstract class HttpUtil {

	/**
	 * pipeStream() for an HttpURLConnection, trying getInputStream() then getErrorStream().
	 * If getInputStream() successfully returns, read the entire response body.
	 * If an IOException occurs, catch the exception and call getErrorStream() to get the response body, if any.
	 * @see http://docs.oracle.com/javase/6/docs/technotes/guides/net/http-keepalive.html
	 */
	public static void pipeStream(final HttpURLConnection urlConn, final OutputStream ous, final ByteBuffer bbuf) throws IOException {
		try { 
			StreamUtil.pipeStream(urlConn.getInputStream(), ous, bbuf);
		}
		catch (IOException ex) {
			StreamUtil.pipeStream(urlConn.getErrorStream(), ous, bbuf);
		}
	}
	
}
