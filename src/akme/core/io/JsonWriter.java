package akme.core.io;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

/**
 * Wrapper for a JSON, Javascript Object Notation, buffer.
 * 
 * $NoKeywords: $
 */
public class JsonWriter implements JsonSerializer {

	/** Internal buffer. */
	private final Writer buffer;

	/** State of line-wrapping. */
	private final boolean lineWrapping;
	
	/** Track nesting of [] arrays and {} objects; [ or { on the first, else ] or }. */
	private final StringBuilder nest;
	
	/** Track the character at the top of the next, a.k.a. the bird. */
	private char nestTop = '\0';
	
	/**
	 * Construct a new JsonWriter, by default using a <code>CharArrayWriter</code>.
	 */
	public JsonWriter() {
		this(new CharArrayWriter(), false);
	}

	/**
	 * Construct a new JsonWriter appending to a pre-existing buffer.
	 */
	public JsonWriter(final Writer out) {
		this(out, false);
	}
	
	/**
	 * Construct a new JsonWriter appending to a pre-existing buffer using the given lineWrapping.
	 */
	public JsonWriter(final Writer out, final boolean lineWrapping) {
		this.buffer = (out != null) ? out : new CharArrayWriter();
		this.nest = new StringBuilder();
		this.lineWrapping = lineWrapping;
	}
	
	/**
	 * Return the internal writer.
	 *
	 * @return Internal writer.
	 */
	public Writer getWriter() {
		return buffer;
	}
	
	public boolean getLineWrapping() {
		return lineWrapping;
	}
	
	private void addName(final String name) throws IOException {
		if (lineWrapping) {
			buffer.write('\n');
		}
		addCommaAfterFirst();
		if (lineWrapping) {
			for (int i=0; i<nest.length(); i++) buffer.write('\t');
		}
		buffer.write('\"');
		buffer.write(name);
		buffer.write('\"');
		if (lineWrapping) buffer.write(" : "); 
		else buffer.write(':');
	}
	
	private void addValue(final Object value) throws IOException {
		if (nestTop == ']') buffer.write(',');
		if (value != null) {
			if (value instanceof Boolean || value instanceof Number) {
				buffer.write(String.valueOf(value));
			} else {
				buffer.write('\"');
				buffer.write(JsonUtil.encodeValue(String.valueOf(value)));
				buffer.write('\"');
			}
		}
		else {
			buffer.write(JsonUtil.NULL);
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
		try {
			if (nestTop == '}' || nestTop == ']') buffer.write(',');
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	public void addItem(final String name, final Object value) {
		try {
			addName(name);
			addValue(value);
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * Add an item value to an array.
	 * @throws IllegalStateException if called outside an Array.
	 */
	public void addItem(final Object value) {
		if (nestTop != ']' && nestTop != '[') throw new IllegalStateException("addArrayItem called outside an Array!");
		try {
			addValue(value);
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	public void addArray(final String name, final Object[] ary) {
		try {
			addName(name);
			addArray(ary, false);
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	public void addArray(final String name, final Collection<?> list) {
		try {
			addName(name);
			addArray(list, false);
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}		

	public void addArray(final Object[] ary) {
		addArray(ary, true);
	}

	protected void addArray(final Object[] ary, final boolean useComma) {
		try {
			if (ary == null) {
				if (useComma) addCommaAfterFirst();
				buffer.write(JsonUtil.NULL);
				return;
			}
			openArray(useComma);
			for (int i=0; i<ary.length; i++) addValue(ary[i]);
			closeLevel();
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	public void addArray(final Collection<?> list) {
		addArray(list, true);
	}

	protected void addArray(final Collection<?> list, final boolean useComma) {
		try {
			if (list == null) {
				if (useComma) addCommaAfterFirst();
				buffer.write(JsonUtil.NULL);
				return;
			}
			openArray(useComma);
			for (Object item : list) addValue(item);
			closeLevel();
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	public void openArray() {
		openArray(true);
	}
	
	protected void openArray(final boolean useComma) {
		if (useComma) addCommaAfterFirst();
		try {
			nestTop = '[';
			nest.append(nestTop);
			buffer.write(nestTop);
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	public void openArray(final String name) {
		try {
			addName(name);
			nestTop = '[';
			nest.append(nestTop);
			buffer.write(nestTop);
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	public void openObject() {
		addCommaAfterFirst();
		try {
			nestTop = '{';
			nest.append(nestTop);
			buffer.write(nestTop);
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	public void openObject(final String name) {
		try {
			addName(name);
			nestTop = '{';
			nest.append(nestTop);
			buffer.write(nestTop);
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	public void closeLevel(final Object value) {
		try {
			addValue(value);
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
		closeLevel();
	}
	
	public void closeLevel() {
		if (nestTop != '\0') {
			try {
				if (nestTop == '{' || nestTop == '}') buffer.write('}');
				else if (nestTop == '[' || nestTop == ']') buffer.write(']');
				nest.setLength(nest.length()-1);
				nestTop = nest.length() != 0 ? nest.charAt(nest.length()-1) : '\0';
				if (lineWrapping) {
					for (int i=0; i<nest.length(); i++) buffer.write('\t');
				}
			}
			catch (IOException ex) {
				throw new IllegalStateException(ex);
			}
			setNestAfterFirst();
		}
	}	
	
	/**
	 * @see Writer
	 */
	public void write(char cbuf[], int off, int len) throws IOException {
		buffer.write(cbuf,off,len);
	}

	/**
	 * @see Writer
	 */
	public void flush() throws IOException {
		buffer.flush();
	}

	/**
	 * @see Writer
	 */
	public void close() throws IOException {
		buffer.close();
	}

}
