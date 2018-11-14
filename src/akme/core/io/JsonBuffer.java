package akme.core.io;

import java.util.Collection;

/**
 * Wrapper for a JSON, Javascript Object Notation, buffer.
 * 
 * @see http://tools.ietf.org/html/rfc4627
 * @see http://en.wikipedia.org/wiki/JSON
 * @author <br/> Original code by AKME Solutions.
 * $NoKeywords: $
 */
public class JsonBuffer implements JsonSerializer {

	/** Internal buffer. */
	private final StringBuilder buffer;

	/** State of line-wrapping. */
	private final boolean lineWrapping;
	
	/** Track nesting of [] arrays and {} objects; [ or { on the first, else ] or }. */
	private final StringBuilder nest;
	
	/** Track the character at the top of the next, a.k.a. the bird. */
	private char nestTop = '\0';

	/**
	 * Construct a new JsonBuffer, by default using a <code>StringBuffer</code>.
	 */
	public JsonBuffer() {
		this(new StringBuilder(), false);
	}

	/**
	 * Construct a new JsonBuffer appending to a pre-existing buffer.
	 */
	public JsonBuffer(final StringBuilder sb) {
		this(sb, false);
	}
	
	/**
	 * Construct a new JsonBuffer appending to a pre-existing buffer using the given lineWrapping.
	 */
	public JsonBuffer(final StringBuilder sb, final boolean lineWrapping) {
		this.buffer = (sb != null) ? sb : new StringBuilder();
		this.nest = new StringBuilder();
		this.lineWrapping = lineWrapping;
	}
	
	/**
	 * Return the internal buffer.
	 *
	 * @return Internal buffer.
	 */
	public StringBuilder getBuffer() {
		return buffer;
	}
	
	public boolean getLineWrapping() {
		return lineWrapping;
	}
	
	private void addName(final String name) {
		if (lineWrapping) {
			buffer.append('\n');
		}
		addCommaAfterFirst();
		if (lineWrapping) {
			for (int i=0; i<nest.length(); i++) buffer.append('\t');
		}
		buffer.append('\"');
		buffer.append(name);
		buffer.append('\"');
		if (lineWrapping) buffer.append(" : "); 
		else buffer.append(':');
	}
	
	private void addValue(final Object value) {
		if (nestTop == ']') buffer.append(',');
		if (value != null) {
			if (value instanceof Boolean || value instanceof Number) {
				buffer.append(String.valueOf(value));
			} else {
				buffer.append('\"');
				buffer.append(JsonUtil.encodeValue(String.valueOf(value)));
				buffer.append('\"');
			}
		}
		else {
			buffer.append(JsonUtil.NULL);
		}
		setNestAfterFirst();
	}

	public int getNestLevel() {
		return nest.length();
	}
		
	public char getNestChar() {
		return nestTop;
	}
		
	public boolean isNestFirst() {
		return (nestTop == '{' || nestTop == '[');
	}
	
	public void setNestAfterFirst() {
		final char c;
		switch (nestTop) {
		case '{': c = '}'; break;
		case '[': c = ']'; break;
		default: return;
		}
		nestTop = c;
		nest.setCharAt(nest.length()-1, c);
	}
	
	public void addCommaAfterFirst() {
		if (nestTop == '}' || nestTop == ']') buffer.append(',');
	}
	
	public void addItem(final String name, final Object value) {
		addName(name);
		addValue(value);
	}
	
	/**
	 * Add an item value to an array.
	 * @throws IllegalStateException if called outside an Array.
	 */
	public void addItem(final Object value) {
		if (nestTop != ']' && nestTop != '[') throw new IllegalStateException("addArrayItem called outside an Array!");
		addValue(value);
	}

	public void addArray(final String name, final Object[] ary) {
		addName(name);
		addArray(ary, false);
	}
	
	public void addArray(final String name, final Collection<?> list) {
		addName(name);
		addArray(list, false);
	}

	public void addArray(final Object[] ary) {
		addArray(ary, true);
	}

	protected void addArray(final Object[] ary, final boolean useComma) {
		if (ary == null) {
			if (useComma) addCommaAfterFirst();
			buffer.append(JsonUtil.NULL);
			return;
		}
		openArray(useComma);
		for (int i=0; i<ary.length; i++) addValue(ary[i]);
		closeLevel();
	}

	public void addArray(final Collection<?> list) {
		addArray(list, true);
	}
	
	protected void addArray(final Collection<?> list, final boolean useComma) {
		if (list == null) {
			if (useComma) addCommaAfterFirst();
			buffer.append(JsonUtil.NULL);
			return;
		}
		openArray(useComma);
		for (Object item : list) addValue(item);
		closeLevel();
	}
	
	public void openArray() {
		openArray(true);
	}
	
	protected void openArray(final boolean useComma) {
		if (useComma) addCommaAfterFirst();
		nestTop = '[';
		nest.append(nestTop);
		buffer.append(nestTop);
	}
	
	public void openArray(final String name) {
		addName(name);
		nestTop = '[';
		nest.append(nestTop);
		buffer.append(nestTop);
	}
	
	public void openObject() {
		addCommaAfterFirst();
		nestTop = '{';
		nest.append(nestTop);
		buffer.append(nestTop);
	}
	
	public void openObject(final String name) {
		addName(name);
		nestTop = '{';
		nest.append(nestTop);
		buffer.append(nestTop);
	}
	
	public void closeLevel(final Object value) {
		addValue(value);
		closeLevel();
	}
	
	public void closeLevel() {
		if (nestTop != '\0') {
			if (nestTop == '{' || nestTop == '}') buffer.append('}');
			else if (nestTop == '[' || nestTop == ']') buffer.append(']');
			nest.setLength(nest.length()-1);
			nestTop = nest.length() != 0 ? nest.charAt(nest.length()-1) : '\0';
			if (lineWrapping) {
				for (int i=0; i<nest.length(); i++) buffer.append('\t');
			}
			setNestAfterFirst();
		}
	}
	
	public String toJson() {
		while (nest.length() != 0) closeLevel();
		return buffer.toString();
	}

}
