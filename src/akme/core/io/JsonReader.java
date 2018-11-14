package akme.core.io;

import java.io.IOException;
import java.io.Reader;

import akme.core.util.ArrayUtil;
import akme.core.util.StringUtil;

/**
 * Easily and efficiently parse a JSON stream, forward-only.
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
public class JsonReader extends StreamTokenizer implements JsonDeserializer {
	
	private final Reader reader;
	private final StringBufferFast buf;
	private int idx;
	private StringBuilder nest;
	private String name;
	private boolean throwIfNotFound;
	
	/**
	 * Construct a new JsonReader based on a pre-existing reader.
	 */
	public JsonReader(Reader reader) {
		this(reader, 128);
	}
	
	/**
	 * Construct a new JsonReader based on a pre-existing reader and buffer size (to cover longest name + 3).
	 */
	public JsonReader(final Reader reader, final int bufferSize) {
		this.reader = reader;
		this.buf = new StringBufferFast(bufferSize);
		this.idx = reader != null ? 0 : -1;
		this.nest = new StringBuilder(8);
		this.name = null;
		this.throwIfNotFound = false;
	}

	/**
	 * True to throw an IllegalArgumentException when a tag is not found.
	 */
	public void setThrowIfNotFound(boolean throwIfNotFound) {
		this.throwIfNotFound = throwIfNotFound;
	}
	
	/**
	 * True to throw an IllegalArgumentException when a tag is not found.
	 */
	public boolean isThrowIfNotFound() {
		return throwIfNotFound;
	}
	
	/**
	 * Return the underlying Reader givent to the constructor.
	 */
	public Reader getReader() {
		return this.reader;
	}

	/** 
	 * Get the current index within the JSON buffer, not the entire stream, -1 if past the end or invalid.
	 */
	public int getIndex() {
		return this.idx;
	}
	
	/**
	 * Get the current item name or null. 
	 */
	public String getName() {
		return this.name;
	}
	
	private boolean expectChar(char c) {
		char lc = findTokenNext(0);
		while (-1 != this.idx && lc == ',') lc = findTokenNext(0);
		return (lc == c);
	}

	private char expectChar(char c1, char c2) {
		char lc = findTokenNext(0);
		while (-1 != this.idx && lc == ',') lc = findTokenNext(0);
		return (lc == c1 || lc == c2) ? lc : '\0';
	}

	private void skipWhitespace() {
		if (-1 == this.idx) return;
		try {
			while (-1 != this.idx) {
				for (int i = this.idx; i < this.buf.length(); i++) {
					final char c = this.buf.charAt(i); 
					switch (c) {
					case ' ':
					case '\t':
					case '\r':
					case '\n':
					case '\f':
						break;
					default:
						if (Character.isWhitespace(c)) break;
						this.idx = i;
						return;
					}
				}
				this.idx = 0;
				this.buf.setLength(0);
				if (this.buf.readFrom(reader) == -1) this.idx = -1;
			}
		}
		catch (IOException ex) {
			this.idx = -1;
			throw new RuntimeException(ex);
		}
		// Past end of string.
		this.idx = -1;
		this.buf.setLength(0);
	}
	
	private String getKeywordOrNull() {
		skipWhitespace();
		for (int i=0; i<JsonUtil.KEYWORDS.length && -1 != this.idx; i++) {
			final String word = JsonUtil.KEYWORDS[i];
			if (this.buf.length()+1 < word.length()) {
				try {
					if (this.buf.readFrom(reader) == -1) this.idx = -1;
				}
				catch (IOException ex) {
					this.idx = -1;
					throw new RuntimeException(ex);
				}
			}
			if (-1 != this.idx && this.buf.regionMatches(this.idx, word, 0, word.length())
					&& (this.idx+word.length() >= this.buf.length()
						|| JsonUtil.NON_NAME.indexOf(this.buf.charAt(this.idx+word.length())) != -1)) {
				this.idx += word.length();
				return word;
			}
		}
		return null;
	}
	
	private String getItemName() { // TODO: handle escape characters in "name":
		if (-1 == this.idx) return null;
		final String keyword = getKeywordOrNull();
		if (keyword != null) return keyword;
		// Ensure we've at least read enough to find the longest name less than the buffer capacity.
		try {
			for (int n = this.buf.readFrom(reader); n != -1 && this.buf.length() < this.buf.capacity(); n = this.buf.readFrom(reader)) ;
		}
		catch (IOException ex) {
			this.idx = -1;
			throw new RuntimeException(ex);
		}
		final StringBuilder result = new StringBuilder();
		int i = this.idx;
		final boolean inq = (this.buf.charAt(i) == '\"');
		if (inq) i++;
		for (; i < this.buf.length(); i++) {
			char c = this.buf.charAt(i);
			if (c == '\"' || (!inq && JsonUtil.NON_NAME.indexOf(c) != -1)) break;
			result.append(c);
		}
		if (i < this.buf.length()) {
			if (this.buf.charAt(i) == '\"') i++;
		}
		this.idx = i < this.buf.length() ? i : -1;
		return result.toString();
	}
	
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
	public String getValue() { // TODO: See XmlReader getElementValue for repeatedly filling buffer.
		if (-1 == this.idx) return null;
		skipWhitespace();
		if (-1 != this.idx) {
			switch (this.buf.charAt(this.idx)) {
			case ':': case ',': this.idx++;
			default: break;
			}
		}
		final String keyword = getKeywordOrNull();
		if (keyword != null) return keyword;
		if (-1 == this.idx) return null;
		int i = this.idx;
		char c = this.buf.charAt(i);
		if (JsonUtil.NON_STARTER.indexOf(c) != -1) return null;
		final boolean inq = (c == '\"'); 
		if (inq) i++;
		final char[] unic = new char[4];
		final char[] bufc = new char[6];
		final StringBuilder result = new StringBuilder();
		while (-1 != this.idx) {
			if (i+6 > this.buf.capacity()) { // We may want up to 6 chars to handle \u1234.
				final int len = i+6 < this.buf.length() ? i+6 : this.buf.length();
				this.buf.getChars(i, len, bufc, 0);
				this.buf.setLength(0);
				this.buf.append(bufc, 0, len);
				i = 0;
			}
			try {
				int n;
				for (n = this.buf.readFrom(reader); n != -1 && this.buf.length() < this.buf.capacity(); n = this.buf.readFrom(reader)) ;
				if (-1 == n) this.idx = -1;
			}
			catch (IOException ex) {
				this.idx = -1;
				throw new RuntimeException(ex);
			}
			for (; i<this.buf.length(); i++) {
				c = this.buf.charAt(i);
				if (c == '\\') {
					if (i+1 == this.buf.length()) {
						if (-1 == this.idx) result.append(c);
						break;
					}
					c = this.buf.charAt(i+1);
					if (c == 'u') {
						i += 2;
						final int len = i+4 < this.buf.length() ? 4 : this.buf.length()- (i+4);
						this.buf.getChars(i, i+len, unic, 4-len);
						result.append(ArrayUtil.toCharFromUnsignedHex4(unic));
						//result.append((char) Integer.parseInt(d, 16)); // parseInt is complicated.
						i += len-1;
					} else {
						final int j = JsonUtil.ESCAPE_STR.indexOf(c);
						if (-1 != j) {
							result.append(JsonUtil.REPLACE_STR.charAt(j));
							i++;
						} else {
							// Not a replacement so nothing to escape.
							result.append('\\');
						}
					}
				} else if (inq && c == '\"') {
					i++;
					break;
				} else if (!inq && JsonUtil.NON_VALUE.indexOf(c) != -1) {
					break;
				} else {
					result.append(c);
				}
			}			
		}
		this.idx = i < this.buf.length() ? i : -1;
		return result.toString();
	}
	
	/**
	 * Find and position the internal index at the next item, Object or Array or Property, 
	 * returning the getLastChar() or '\0' if not found.
	 * Skip to the end of any current tag.
	 */
	private char findTokenNext(int lvl) {
		if (-1 == this.idx) return '\0';
		skipWhitespace();
		if (-1 == this.idx) return '\0';
		if (this.buf.charAt(this.idx) == ':') {
			this.idx++;
			getValue();
		}
		if (-1 == this.idx) return '\0';
		char c = this.buf.charAt(this.idx);
		switch (c) {
		case '{':
		case '[':
			this.nest.append(c);
			this.idx++;
			break;
		case '}':
		case ']':
			if (this.nest.length() <= lvl) return '\0';
			if (this.nest.length() > 0) this.nest.setLength(this.nest.length()-1);
			this.idx++;
			break;
		case ',':
			this.idx++;
		default:
			this.name = getItemName();
			break;
		}
		return getLastChar();
	}

	/**
	 * Find and position the internal index at the next item at the same level, 
	 * or false if not found.
	 * Skip to the end of any current tag.
	 */
	public boolean findItemNext() {
		final int lvl = this.nest.length();
		while (-1 != this.idx) {
			skipWhitespace();
			if (-1 == this.idx) return false;
			if (this.buf.charAt(this.idx) == ':') {
				this.idx++;
				getValue();
			}
			if (-1 == this.idx) return false;
			char c = this.buf.charAt(this.idx);
			switch (c) {
			case '{':
			case '[':
				this.nest.append(c);
				this.idx++;
				break;
			case '}':
			case ']':
				if (this.nest.length() > lvl) this.nest.setLength(this.nest.length()-1);
				else return false;
				this.idx++;
				break;
			case ',':
				this.idx++;
			default:
				this.name = getItemName();
				return true;
			}			
		}
		return false;
	}

	public char getNestChar() {
		return this.nest.length() != 0 ? this.nest.charAt(this.nest.length()-1) : '\0';
	}
	
	public int getNestLevel() {
		return this.nest.length();
	}
	
	public char getLastChar() { // TODO: readAFew, a common method to leave 1 char at this.idx = 0 and read at least 6 chars if possible.
		return this.idx > 0 ? this.buf.charAt(this.idx-1) : '\0';
	}

	public char getThisChar() {
		if (-1 == this.idx) return '\0';
		skipWhitespace();
		return -1 == this.idx ? '\0' : this.buf.charAt(this.idx);
	}

	/**
	 * Enter an array ([]), returning false if not at one.
	 * This will skip over a colon (:) or comma (,).
	 */
	public boolean enterArray() {
		if (-1 == this.idx) return false;
		char c = getThisChar();
		if (':' == c || ',' == c) {
			if (this.idx+1 < this.buf.length()) { // TODO: readAFew
				this.idx++;
				c = getThisChar();
			}
		}
		if ('[' == c) {
			this.nest.append(c);
			this.idx++;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Enter an Object/Map ({}), returning false if not at one. 
	 * This will skip over a colon (:) or comma (,).
	 */
	public boolean enterObject() {
		if (-1 == this.idx) return false;
		char c = getThisChar();
		if (':' == c || ',' == c) {
			if (this.idx+1 < this.buf.length()) { // TODO: readAFew
				this.idx++;
				c = getThisChar();
			}
		}
		if ('{' == c) {
			this.nest.append(c);
			this.idx++;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Enter an Object/Map ({}) or Array ([]), returning false if not at one. 
	 * This will skip over a colon (:) or comma (,).
	 * @return '[' if an array, '{' if an object, '\0' if neither.
	 */
	public char enterArrayOrObject() {
		if (-1 == this.idx) return '\0';
		char c = getThisChar();
		if (':' == c || ',' == c) {
			if (this.idx+1 < this.buf.length()) { // TODO: readAFew
				this.idx++;
				c = getThisChar();
			}
		}
		if ('{' == c || '[' == c) {
			this.nest.append(c);
			this.idx++;
			return c;
		} else {
			return '\0';
		}
	}
	
	/**
	 * Leave the current array, returning false if not in one.
	 */
	public boolean leaveArray() {
		boolean result = false;
		final int level = this.nest.length(); 
		while (-1 != this.idx && (this.nest.length() >= level || !result)) result = expectChar(']');
		this.name = null;
		return result;
	}

	/**
	 * Leave the current object, returning false if not in one.
	 */
	public boolean leaveObject() {
		boolean result = false;
		final int level = this.nest.length(); 
		while (-1 != this.idx && (this.nest.length() >= level || !result)) result = expectChar('}');
		this.name = null;
		return result;
	}

	/**
	 * Leave the current object or array, returning false if not in one.
	 */
	public char leaveArrayOrObject() {
		char result = '\0';
		final int level = this.nest.length();
		while (-1 != this.idx && (this.nest.length() >= level || result == '\0')) result = expectChar(']','}');
		this.name = null;
		return result;
	}

	/**
	 * Find and position the internal index at the given tag, or false if not found. 
	 * Skip past any current element value before finding the next tag.
	 */
	public boolean findItem(final String itemName) {
		final int lvl = this.nest.length();
		boolean found = false;
		if (-1 != this.idx) while (findItemNext() || findTokenNext(lvl) != '\0') {
			if (lvl == this.nest.length() && itemName.equals(this.name)) {
				found = true;
				break;
			}
		}
		if (throwIfNotFound && !found) {
			throw new IllegalArgumentException("findItem("+ itemName +") return "+ found);
		}
		return found;
	}
	
	/**
	 * Find and position the internal index at the next of any of the given tags,
	 * returning the array index of tagNames that was found or -1 if none were found.
	 * Skip to the end of any current tag.
	 * This is useful for switch (int) case statements. 
	 */
	public int findItemIn(final String[] itemNames) {
		final int lvl = this.nest.length();
		int j = -1;
		if (-1 != this.idx) while (findItemNext() || findTokenNext(lvl) != '\0') {
			if (lvl == this.nest.length()) for (int i=0; i<itemNames.length; i++) {
				if (itemNames[i].equals(this.name)) {
					j = i;
					break;
				}
			}
			if (-1 != j) {
				break;
			}
		}
		if (throwIfNotFound && -1 == j) {
			throw new IllegalArgumentException("findItemIn("+ StringUtil.joinString(itemNames, StringUtil.COMMA_STRING) +") return "+ j);
		}
		return j;
	}
	
}
