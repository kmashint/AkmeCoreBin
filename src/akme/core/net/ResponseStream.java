package akme.core.net;

import java.io.Closeable;
import java.net.URLConnection;

/**
 * Standard interface between text/char Reader and binary/byte InputStream kinds of Response.
 */
public interface ResponseStream extends Closeable {
	
	public int getStatus() ;

	public String getStatusText() ;
	
	public int getContentLength() ;

	public String getContentType() ;

	public long getLastModified() ;

	/**
	 * URLConnection if created from one, otherwise null.
	 */
	public URLConnection getURLConnection() ;

}