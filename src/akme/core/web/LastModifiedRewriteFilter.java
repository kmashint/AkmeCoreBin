package akme.core.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import akme.core.io.StreamUtil;

/**
 * <p>This will append the given param-value trailer, default " ", after param-value maxAge seconds 
 * to files matching url-patterns of this filter in order to refresh their lastModified after maxAge seconds.
 * It also creates a file by the same path plus a param-value ext, default servletPath+".ver", to remember
 * the lastModified of the original file in case the original is over-written by another process, or person.
 * That way a manual update to the original will also be refreshed after maxAge seconds.
 * </p>
 * <p>The primary use is for .appcache files, e.g. a filter-mapping of <code>url-pattern *.appcache</code> 
 * to an <code>AppcacheRewriteFilter</code> defined with filter init-param settings of 
 * of <code>trailer #LastModifiedRewriteFilter</code>, <code>maxAge 900</code> (15 minutes).
 * </p>
 * <p><pre>
 *   maxAge 900 will automatically create a new file 900 seconds (15 minutes) in the future and continue to forward to it.
 *   trailer #LastModifiedRewriteFilter will append "\n"+"#LastModifiedRewriteFilter" to the new file after the maxAge.
 *   	If no trailer is given, only the lastModified of the file will be changed/touched.
 *   ext _ver (the default) will create files adding extension _ver to whatever servletPath(s) are matched by the filter url-patterns.
 *   delay 2, the default, will only check the File.lastModified every 2 seconds to avoid excessive file checks.
 * </pre></p>
 * 
 * @author akme.org, kmashint@yahoo.com
 * $NoKeywords: $
 */

public class LastModifiedRewriteFilter implements Filter {
	
	//public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
	//public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
	//public static final String HEADER_LAST_MODIFIED = "Last-Modified";
	
	protected FilterConfig fc;
	
	private ConcurrentHashMap<String,AtomicLong> lastTimeMap = null; 
	
	private long delay = 2000;
	private long maxAge = -1;
	private byte[] trailer = " ".getBytes();
	private String ext = "_ver";
	
  	public void init(final FilterConfig filterConfig) {
	    this.fc = filterConfig;
	    this.lastTimeMap = new ConcurrentHashMap<String,AtomicLong>();
	    
	    String value;
	    
	    value = fc.getInitParameter("delay");
	    if (value == null) value = fc.getInitParameter("Delay");
	    if (value != null) delay = Integer.parseInt(value, 10)*1000;

	    // e.g. 900 (seconds is 15 minutes)
	    value = fc.getInitParameter("maxAge");
	    if (value == null) value = fc.getInitParameter("MaxAge");
	    if (value == null) value = fc.getInitParameter("max-age");
	    if (value != null) maxAge = Integer.parseInt(value, 10)*1000;
	    
	    // e.g. #LastModifiedRewriteFilter
	    value = fc.getInitParameter("trailer");
	    if (value == null) value = fc.getInitParameter("Trailer");
	    if (value != null) trailer = ("\n"+value).getBytes(); // ByteBuffer.wrap(("\n"+value).getBytes()) not thread-safe
	    
	    value = fc.getInitParameter("ext");
	    if (value == null) value = fc.getInitParameter("Ext");
	    if (value != null && value.length() != 0) ext = value;
	}

	public void destroy() {
	    this.fc = null;
	    this.lastTimeMap.clear();
	    this.lastTimeMap = null;
	}
	  
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		final HttpServletRequest request = (HttpServletRequest) req;
		//final HttpServletResponse response = (HttpServletResponse) res;

		final String path = request.getServletPath();
		int pos = path.lastIndexOf('.');
		if (pos == -1) pos = path.length();
		if (path.regionMatches(pos-ext.length(), ext, 0, ext.length())) {
			// Continue if this is already the forwarded path.
			chain.doFilter(req, res);
			return;
		}
		
		// Remember the lastTime we performed the full check of File.lastModified.
		final String outPath = path.substring(0, pos)+ext+path.substring(pos);
		final long now = System.currentTimeMillis();
		final AtomicLong lastTimeObj = this.lastTimeMap.putIfAbsent(path, new AtomicLong(now));
//System.err.println("outPath "+ outPath +" lastTimeObj "+ lastTimeObj);
		if (lastTimeObj != null) {
			final long lastTime = lastTimeObj.longValue();
			if (now < lastTime + delay || ! lastTimeObj.compareAndSet(lastTime, now)) {
				// Forward it on if not new and within the delay period to avoid excessive File.lastModified checking.
				// Also forward if another thread won the compareAndSet race (compareAndSet is highly optimised).
//System.err.println("within delay "+ delay +" or !compareAndSet "+ lastTime +","+ now);
				req.getRequestDispatcher(outPath).forward(req, res);
				return;
			}
		}
		
		final File inf = new File(fc.getServletContext().getRealPath(path));
		final long lastModified = inf.lastModified();
		final File ouf = new File(fc.getServletContext().getRealPath(outPath));
		final boolean oufNew = ouf.createNewFile();
		final long oufModified = ouf.lastModified();
		final long diffModified = (oufNew || oufModified < lastModified + maxAge) && now > lastModified + maxAge ? maxAge : (
			oufNew || lastModified > oufModified ? 0 : -1 );
		if (diffModified != -1) {
//System.err.println("LastModifiedRewriteFilter new "+ oufNew + " diffModified "+ diffModified +" maxAge "+ maxAge);
			FileChannel inc = null;
			FileChannel ouc = null;
			FileLock lock = null;
			final long infSize = inf.length();
			final int bufSize = res.getBufferSize();
			try {
				// Closing a channel will close its related stream, just like InputStreamReader.close().
				inc = new FileInputStream(inf).getChannel();
				ouc = new FileOutputStream(ouf, false).getChannel();
				try {
					lock = ouc.tryLock(0, infSize + (trailer != null ? trailer.length : 0), false);
//System.err.println("LastModifiedRewriteFilter lock "+ lock);
					if (lock != null) { 
						// Write if the lock was obtained, otherwise another process is modifying it.
						while (inc.position() < infSize) {
							ouc.transferFrom(inc, inc.position(), bufSize);
						}
						if (maxAge == diffModified && trailer != null) {
							// ByteBuffer changes state on write and is not thread-safe so create it upon use.
							ouc.write(ByteBuffer.wrap(trailer), infSize);
						}
                    }
				}
                catch (OverlappingFileLockException ex) {
                    // Another thread in this JVM, not another process, has already obtained a lock so tryLock failed.
                    lock = null;
                }
                finally {
                    if (lock != null) lock.release(); 
                }
            }
            finally {
                StreamUtil.closeQuiet(inc);
                StreamUtil.closeQuiet(ouc);
            }
            if (lock != null) {
                // Set the lastModified of the outPath if we locked it. 
                ouf.setLastModified(lastModified + diffModified);
            } else {
                // Continue to the given path if another thread or process locked the outPath.
                chain.doFilter(req, res);
                return;
			}
		}
		
		// Forward to the outPath.
		req.getRequestDispatcher(outPath).forward(req, res);
  	}
	
/* Sample of using Channel for best NIO copying at least before Java 7.
public static void copyFile(File inf, File ouf) throws IOException {
 final long bufSize = 8*1024;
 final long infSize = inf.length();
 if (!ouf.exists()) ouf.createNewFile();
 FileChannel ins = null;
 FileChannel ous = null;
 try {
  ins = new FileInputStream(inf).getChannel();
  ous = new FileOutputStream(ouf).getChannel();
  while (ins.position() < infSize) ous.transferFrom(ins, ins.position(), bufSize);
 }
 finally {
  StreamUtil.closeQuiet(ous);
  StreamUtil.closeQuiet(ins);
 }
}
*/
	
}
