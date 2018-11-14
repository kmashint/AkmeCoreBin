package akme.core.io;

import java.util.Calendar;
import java.util.Date;


/**
 * Easily and efficiently parse JSON.
 * This is optimised for speed, not thread-safety, and is forward-only.
 * e.g. 
 * <code><pre>
 * if (findItem("xObject") && enterObject()) { getValue("xValue1"); getValue("xValue2"); }
 * </pre></code>
 * 
 * @see http://tools.ietf.org/html/rfc4627
 * @see http://en.wikipedia.org/wiki/JSON
 * @author <br/> Original code by AKME Solutions.
 * $NoKeywords: $
 */
public interface JsonDeserializer {

	/**
	 * True to throw an IllegalArgumentException when a tag is not found.
	 */
	public void setThrowIfNotFound(boolean throwIfNotFound) ;
	
	/**
	 * True to throw an IllegalArgumentException when a tag is not found.
	 */
	public boolean isThrowIfNotFound() ;

	/** 
	 * Get the current index within the JSON string, -1 if past the end or invalid.
	 */
	public int getIndex() ;
	
	/**
	 * Get the current item name or null. 
	 */
	public String getName() ;
		
	/**
	 * Return the current value or null if is none, moving the position forward if found.
	 * A while != null loop can work for an array, e.g. { x : [1,2,3], y : 4 } ...
	 * <code>
	 * jt.enterObject();
	 * jt.findItem("x");
	 * jt.entryArray();
	 * for (String value; (value = jt.getValue()) != null;) System.out.println(value);
	 * jt.leaveArray();
	 * jt.findItem("y");
	 * jt.getValue();
	 * jt.leaveObject();
	 * </code>
	 * ... after which 
	 */
	public String getValue() ;
	
	/**
	 * Find and position the internal index at the next item at the same level, 
	 * or false if not found.
	 * Skip to the end of any current tag.
	 */
	public boolean findItemNext() ;
	
	public char getNestChar() ;
	
	public int getNestLevel() ;
	
	public char getLastChar() ;

	public char getThisChar() ;

	/**
	 * Enter an array ([]), returning false if not at one.
	 * This will skip over a colon (:) or comma (,).
	 */
	public boolean enterArray() ;

	/**
	 * Enter an Object/Map ({}), returning false if not at one. 
	 * This will skip over a colon (:) or comma (,).
	 */
	public boolean enterObject() ;
	
	/**
	 * Enter an Object/Map ({}) or Array ([]), returning false if not at one. 
	 * This will skip over a colon (:) or comma (,).
	 * @return '[' if an array, '{' if an object, '\0' if neither.
	 */
	public char enterArrayOrObject() ;
	
	/**
	 * Leave the current array, returning false if not in one.
	 */
	public boolean leaveArray() ;

	/**
	 * Leave the current object, returning false if not in one.
	 */
	public boolean leaveObject() ;

	/**
	 * Leave the current object or array, returning false if not in one.
	 */
	public char leaveArrayOrObject() ;

	/**
	 * Find and position the internal index at the given tag, or false if not found. 
	 * Skip past any current element value before finding the next tag.
	 */
	public boolean findItem(final String itemName) ;
	
	/**
	 * Find and position the internal index at the next of any of the given tags,
	 * returning the array index of tagNames that was found or -1 if none were found.
	 * Skip to the end of any current tag.
	 * This is useful for switch (int) case statements. 
	 */
	public int findItemIn(final String[] itemNames) ;
	
	public String getValueString() ;
	
	public byte getValueByte() ;

	public int getValueInteger() ;

	public long getValueLong() ;

	public float getValueFloat() ;

	public double getValueDouble() ;

	public Calendar getValueCalendar() ;

	public Date getValueDate() ;

}
