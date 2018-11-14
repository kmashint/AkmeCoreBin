package akme.core.io;

import java.util.Collection;

/**
 * Easily and efficiently format/create JSON.
 * This is optimised for speed, not thread-safety, and is forward-only.
 * e.g. 
 * <code><pre>
 * openObject(); addItem("count",2"); addArray("data", new Object[] {1,2}); closeLevel();
 * </pre></code>
 * 
 * @see http://tools.ietf.org/html/rfc4627
 * @see http://en.wikipedia.org/wiki/JSON
 * @author <br/> Original code by AKME Solutions.
 * $NoKeywords: $
 */
public interface JsonSerializer {

	public int getNestLevel() ;
		
	public char getNestChar() ;
		
	public boolean isNestFirst() ;
	
	public void setNestAfterFirst() ;
	
	public void addCommaAfterFirst() ;
	
	public void addItem(final String name, final Object value) ;
	
	public void addItem(final Object value) ;
	
	public void addArray(final String name, final Object[] ary) ;
	
	public void addArray(final String name, final Collection<?> list) ;
	
	public void addArray(final Object[] ary) ;
	
	public void addArray(final Collection<?> list) ;
	
	public void openArray() ;
	
	public void openArray(final String name) ;
	
	public void openObject() ;
	
	public void openObject(final String name) ;
	
	public void closeLevel(final Object value) ;
	
	public void closeLevel() ;

}
