package akme.core.io;

import java.util.Calendar;
import java.util.Date;

import akme.core.util.DateUtil;

/**
 * Abstract Tokenizer for an XML or JSON stream. 
 */
public abstract class StreamTokenizer {
	
	/**
	 * Find and position the internal index at the given tag, or false if not found. 
	 * Skip past any current element value before finding the next tag.
	 */
	public abstract boolean findItem(final String itemName) ;
	
	/**
	 * Find and position the internal index at the next of any of the given tags,
	 * returning the array index of tagNames that was found or -1 if none were found.
	 * Skip to the end of any current tag.
	 * This is useful for switch (int) case statements. 
	 */
	public abstract int findItemIn(final String[] itemNames) ;
	
	/**
	 * Find and position the internal index at the next item at the same level, 
	 * or false if not found.
	 * Skip to the end of any current tag.
	 */
	public abstract boolean findItemNext() ;
	
	/**
	 * Get the current item name or null. 
	 */
	public abstract String getName() ;
	
	/**
	 * Return the current value or null if is none, moving the position forward if found.
	 */
	public abstract String getValue() ;
	
	public String getValueString() {
		return getValue();
	}
	
	public byte getValueByte() {
		return Byte.parseByte(getValue());
	}

	public int getValueInteger() {
		return Integer.parseInt(getValue());
	}

	public long getValueLong() {
		return Long.parseLong(getValue());
	}

	public float getValueFloat() {
		return Float.parseFloat(getValue());
	}

	public double getValueDouble() {
		return Double.parseDouble(getValue());
	}

	public Calendar getValueCalendar() {
		return DateUtil.parseIsoCalendar(getValue());
	}

	public Date getValueDate() {
		final Calendar result = DateUtil.parseIsoCalendar(getValue());
		return result != null ? result.getTime() : null; 
	}
	
}
