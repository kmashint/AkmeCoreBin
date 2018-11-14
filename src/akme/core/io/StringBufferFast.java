package akme.core.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import akme.core.util.StringUtil;

/**
 * A MODIFIED string buffer implements a mutable sequence of characters.
 * The methods are NOT synchronized, SO BE CAREFUL WITH THREADS!
 * <p>
 * String buffers are used by the compiler to implement the binary
 * string concatenation operator <code>+</code>. For example, the code:
 * <p><blockquote><pre>
 *     x = "a" + 4 + "c"
 * </pre></blockquote><p>
 * is compiled to the equivalent of:
 * <p><blockquote><pre>
 *     x = new StringBuffer().append("a").append(4).append("c")
 *                           .toString()
 * </pre></blockquote><p>
 * The principal operations on a <code>StringBuffer</code> are the
 * <code>append</code> and <code>insert</code> methods, which are
 * overloaded so as to accept data of any type. Each effectively
 * converts a given datum to a string and then appends or inserts the
 * characters of that string to the string buffer. The
 * <code>append</code> method always adds these characters at the end
 * of the buffer; the <code>insert</code> method adds the characters at
 * a specified point.
 * <p>
 * For example, if <code>z</code> refers to a string buffer object
 * whose current contents are "<code>start</code>", then
 * the method call <code>z.append("le")</code> would cause the string
 * buffer to contain "<code>startle</code>", whereas
 * <code>z.insert(4, "le")</code> would alter the string buffer to
 * contain "<code>starlet</code>".
 * <p>
 * Every string buffer has a capacity. As long as the length of the
 * character sequence contained in the string buffer does not exceed
 * the capacity, it is not necessary to allocate a new internal
 * buffer array. If the internal buffer overflows, it is
 * automatically made larger.
 *
 * @author	Arthur van Hoff
 * @author	Keith Mashinter, AKME Solutions
 * @version 	1.34, 12/16/97
 * @see     java.io.ByteArrayOutputStream
 * @see     java.lang.String
 * @since   JDK1.0
 */
 
public final class StringBufferFast implements java.io.Serializable, Appendable, CharSequence {
    /** The value is used for character storage. */
    private char value[];

    /** The count is the number of characters in the buffer. */
    private int count;
    
	/** Block size for chunked read/write operations. */
    private int blockSize;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    static final long serialVersionUID = 3388685877147921107L;
    
    /** Reusable empty string. */
    static final String EMPTY_STRING = "";

    /**
     * Constructs a string buffer with no characters in it and an 
     * initial capacity of 16 characters. 
     *
     * @since   JDK1.0
     */
    public StringBufferFast() {
	this(16);
    }

    /**
     * Constructs a string buffer with no characters in it and an 
     * initial capacity specified by the <code>length</code> argument. 
     *
     * @param      length   the initial capacity.
     * @exception  NegativeArraySizeException  if the <code>length</code>
     *               argument is less than <code>0</code>.
     * @since      JDK1.0
     */
	public StringBufferFast(int length) {
	value = new char[length];
	blockSize = length;
    }

	/**
	 * Constructs a string buffer so that it represents the same 
	 * sequence of characters as the string argument. The initial 
	 * capacity of the string buffer is <code>16</code> plus the length 
	 * of the string argument. 
	 *
	 * @param   str   the initial contents of the buffer.
	 * @since   JDK1.0
	 */
	public StringBufferFast(String str) {
	this(((str != null) ? str.length() : 0) + 16);
	append(str);
	}

	/**
	 * Constructs a string buffer so that it represents the same 
	 * sequence of characters as the StringBuffer argument. The initial 
	 * capacity of the string buffer is <code>16</code> plus the length 
	 * of the given buffer. 
	 *
	 * @param   str   the initial contents of the buffer.
	 * @since   JDK1.0
	 */
	public StringBufferFast(StringBuffer sb) {
		this(((sb != null) ? sb.length() : 0) + 16);
		if (sb != null) {
			sb.getChars(0,sb.length(),this.value,0);
		}
	}

    /**
     * Returns the length (character count) of this string buffer.
     *
     * @return  the number of characters in this string buffer.
     * @since   JDK1.0
     */
    public int length() {
	return count;
    }

    /**
     * Returns the current capacity of the String buffer. The capacity
     * is the amount of storage available for newly inserted
     * characters; beyond which an allocation will occur.
     *
     * @return  the current capacity of this string buffer.
     * @since   JDK1.0
     */
    public int capacity() {
	return value.length;
    }

    /**
	 * Copies the buffer value.
	*/
	public final void copyInternalBuffer() {
	char newValue[] = new char[value.length];
	System.arraycopy(value, 0, newValue, 0, count);
	value = newValue;
    }

    /**
     * Ensures that the capacity of the buffer is at least equal to the
     * specified minimum.
     * If the current capacity of this string buffer is less than the 
     * argument, then a new internal buffer is allocated with greater 
     * capacity. The new capacity is the larger of: 
	 * <ul>
	 * <li>The <code>minimumCapacity</code> argument.
	 * <li>Twice the old capacity, plus <code>2</code>.
	 * </ul>
	 * If the <code>minimumCapacity</code> argument is nonpositive, this
	 * method takes no action and simply returns.
	 *
	 * @param   minimumCapacity   the minimum desired capacity.
	 * @since   JDK1.0
	 */
	public void ensureCapacity(int minimumCapacity) {
	if (minimumCapacity > value.length) {
		expandCapacity(minimumCapacity);
	}
	}

	/**
	 * This implements the expansion semantics of ensureCapacity but is
	 * unsynchronized for use internally by methods which are already
	 * synchronized.
	 *
	 * @see java.lang.StringBufferFast#ensureCapacity(int)
	 */
	private void expandCapacity(int minimumCapacity) {
	int newCapacity = (value.length + 1) * 2;
	if (minimumCapacity > newCapacity) {
		newCapacity = minimumCapacity;
	}

	char newValue[] = new char[newCapacity];
	System.arraycopy(value, 0, newValue, 0, count);
	value = newValue;
	}

	/**
	 * Return the block size for chunked read/write operations.
	 */
	public int getBlockSize() {
		return this.blockSize;
	}

	/**
	 * Sets the block size for chunked read/write operations.
 	 */
	public void setBlockSize(int size) {
		this.blockSize = size; 
	}
	
	
    /**
     * Sets the length of this String buffer.
     * If the <code>newLength</code> argument is less than the current 
     * length of the string buffer, the string buffer is truncated to 
     * contain exactly the number of characters given by the 
     * <code>newLength</code> argument. 
     * <p>
     * If the <code>newLength</code> argument is greater than or equal 
     * to the current length, sufficient null characters 
     * (<code>'&#92;u0000'</code>) are appended to the string buffer so that 
     * length becomes the <code>newLength</code> argument. 
     * <p>
     * The <code>newLength</code> argument must be greater than or equal 
     * to <code>0</code>. 
     *
     * @param      newLength   the new length of the buffer.
     * @exception  StringIndexOutOfBoundsException  if the
     *               <code>newLength</code> argument is invalid.
     * @see        StringBufferFast#length()
     * @since      JDK1.0
     */
    public void setLength(int newLength) {
	if (newLength < 0) {
	    throw new StringIndexOutOfBoundsException(newLength);
	}
	
	if (newLength > value.length) {
	    expandCapacity(newLength);
	}

	if (count < newLength) {
		for (; count < newLength; count++) {
		value[count] = '\0';
		}
	} else {
			count = newLength;
	}
    }

   /**
    * Expand the buffer with the given character.
    * 
	* @param      len   the length to extend the buffer.
	* @param      pad   the character to use when extending the buffer.
	* 
	* @see        StringBufferFast#setLength(int)
	*/
   public void padLength(int len, char pad) {
   	int newLength = count + len;
   	if (newLength > value.length) {
   		expandCapacity(newLength);
   	}
	for (; count < newLength; count++) {
	 value[count] = pad;
	}
   }

    /**
     * Returns the character at a specific index in this string buffer. 
     * <p>
     * The first character of a string buffer is at index 
     * <code>0</code>, the next at index <code>1</code>, and so on, for 
     * array indexing. 
     * <p>
     * The index argument must be greater than or equal to 
     * <code>0</code>, and less than the length of this string buffer. 
     *
     * @param      index   the index of the desired character.
     * @return     the character at the specified index of this string buffer.
     * @exception  StringIndexOutOfBoundsException  if the index is invalid.
     * @see        StringBufferFast#length()
     * @since      JDK1.0
     */
    public char charAt(int index) {
	if ((index < 0) || (index >= count)) {
	    throw new StringIndexOutOfBoundsException(index);
	}
	return value[index];
    }

	/**
	 * Characters are copied from this string buffer into the 
	 * destination character array <code>dst</code>. The first character to 
	 * be copied is at index <code>srcBegin</code>; the last character to 
	 * be copied is at index <code>srcEnd-1.</code> The total number of 
	 * characters to be copied is <code>srcEnd-srcBegin</code>. The 
	 * characters are copied into the subarray of <code>dst</code> starting 
	 * at index <code>dstBegin</code> and ending at index:
	 * <p><blockquote><pre>
	 *     dstbegin + (srcEnd-srcBegin) - 1
	 * </pre></blockquote>
	 *
	 * @param      srcBegin   start copying at this offset in the string buffer.
	 * @param      srcEnd     stop copying at this offset in the string buffer.
	 * @param      dst        the array to copy the data into.
	 * @param      dstBegin   offset into <code>dst</code>.
	 * @exception  StringIndexOutOfBoundsException  if there is an invalid
	 *               index into the buffer.
	 * @since      JDK1.0
	 */
	public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
	if ((srcBegin < 0) || (srcBegin >= count)) {
		throw new StringIndexOutOfBoundsException(srcBegin);
	}
	if ((srcEnd < 0) || (srcEnd > count)) {
		throw new StringIndexOutOfBoundsException(srcEnd);
	}
	if (srcBegin < srcEnd) {
		System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd-srcBegin);
	}
	}

	/**
	 * Characters are copied from this string buffer into the 
	 * destination buffer <code>dst</code>. The first character to 
	 * be copied is at index <code>srcBegin</code>; the last character to 
	 * be copied is at index <code>srcEnd-1.</code> The total number of 
	 * characters to be copied is <code>srcEnd-srcBegin</code>. The 
	 * characters are copied into the subarray of <code>dst</code> starting 
	 * at index <code>dstBegin</code> and ending at index:
	 * <p><blockquote><pre>
	 *     dstbegin + (srcEnd-srcBegin) - 1
	 * </pre></blockquote>
	 *
	 * @param      srcBegin   start copying at this offset in the string buffer.
	 * @param      srcEnd     stop copying at this offset in the string buffer.
	 * @param      dst        the array to copy the data into.
	 * @exception  StringIndexOutOfBoundsException  if there is an invalid
	 *               index into the buffer.
	 * @since      JDK1.0
	 */
	public void getChars(int srcBegin, int srcEnd, StringBuffer dst, int dstBegin) {
		if ((srcBegin < 0) || (srcBegin >= count)) {
			throw new StringIndexOutOfBoundsException(srcBegin);
		}
		if ((srcEnd < 0) || (srcEnd > count)) {
			throw new StringIndexOutOfBoundsException(srcEnd);
		}
		if (srcBegin < srcEnd) {
			dst.insert(dstBegin, this.value, srcBegin, srcEnd-srcBegin);
		}
	}

	/**
	 * Characters are copied from this string buffer into the 
	 * destination buffer <code>dst</code>. The first character to 
	 * be copied is at index <code>srcBegin</code>; the last character to 
	 * be copied is at index <code>srcEnd-1.</code> The total number of 
	 * characters to be copied is <code>srcEnd-srcBegin</code>. The 
	 * characters are appended to <code>dst</code>.
	 *
	 * @param      srcBegin   start copying at this offset in the string buffer.
	 * @param      srcEnd     stop copying at this offset in the string buffer.
	 * @param      dst        the array to copy the data into.
	 * @exception  StringIndexOutOfBoundsException  if there is an invalid
	 *               index into the buffer.
	 * @since      JDK1.0
	 */
	public void getChars(int srcBegin, int srcEnd, StringBuffer dst) {
		if ((srcBegin < 0) || (srcBegin >= count)) {
			throw new StringIndexOutOfBoundsException(srcBegin);
		}
		if ((srcEnd < 0) || (srcEnd > count)) {
			throw new StringIndexOutOfBoundsException(srcEnd);
		}
		if (srcBegin < srcEnd) {
			dst.append(this.value, srcBegin, srcEnd-srcBegin);
		}
	}

	/**
	 * The character at the specified index of this string buffer is set
	 * to <code>ch</code>.
	 * <p>
	 * The offset argument must be greater than or equal to
	 * <code>0</code>, and less than the length of this string buffer.
	 *
	 * @param      index   the index of the character to modify.
	 * @param      ch      the new character.
	 * @exception  StringIndexOutOfBoundsException  if the index is invalid.
	 * @see        StringBufferFast#length()
	 * @since      JDK1.0
	 */
	public void setCharAt(int index, char ch) {
	if ((index < 0) || (index >= count)) {
		throw new StringIndexOutOfBoundsException(index);
	}
	value[index] = ch;
    }

    /**
     * Appends the string representation of the <code>Object</code> 
     * argument to this string buffer. 
     * <p>
     * The argument is converted to a string as if by the method 
     * <code>String.valueOf</code>, and the characters of that 
     * string are then appended to this string buffer. 
     *
     * @param   obj   an <code>Object</code>.
     * @return  this string buffer.
     * @see     java.lang.String#valueOf(java.lang.Object)
     * @see     java.lang.StringBufferFast#append(java.lang.String)
     * @since   JDK1.0
     */
	public StringBufferFast append(Object obj) {
	return append((obj != null) ? String.valueOf(obj) : EMPTY_STRING);
    }

	/**
	 * Appends the string to this string buffer. 
	 * <p>
	 * The characters of the <code>String</code> argument are appended, in 
	 * order, to the contents of this string buffer, increasing the 
	 * length of this string buffer by the length of the argument. 
	 *
	 * @param   str   a string.
	 * @return  this string buffer.
	 * @since   JDK1.0
	 */
	public StringBufferFast append(String str) {
	if (str == null) return this;

	int len = str.length();
	int newcount = count + len;
	if (newcount > value.length)
		expandCapacity(newcount);
	str.getChars(0, len, value, count);
	count = newcount;
	return this;
	}

	/**
	 * Appends the substring to this string buffer.
	 * <p>
	 * The characters of the <code>String</code> argument are appended, in 
	 * order, to the contents of this string buffer, increasing the 
	 * length of this string buffer by the length of the argument. 
	 *
	 * @param   str   a string.
	 * @return  this string buffer.
	 */
	public StringBufferFast append(String str, int begin, int end) {
	if (begin < 0) {
		throw new StringIndexOutOfBoundsException(begin);
	} 
	if (end > str.length()) {
		throw new StringIndexOutOfBoundsException(end);
	} 
	if (begin > end) {
		throw new StringIndexOutOfBoundsException(end - begin);
	}
	int len = end - begin;
	int newcount = count + len;
	if (newcount > value.length)
		expandCapacity(newcount);
	str.getChars(begin, end, value, count);
	count = newcount;
	return this;
	}
	
	/**
	 * Appends the string to this string buffer. 
	 * <p>
	 * The characters of the <code>String</code> argument are appended, in 
	 * order, to the contents of this string buffer, increasing the 
	 * length of this string buffer by the length of the argument. 
	 *
	 * @param   str   a string.
	 * @return  this string buffer.
	 * @since   JDK1.0
	 */
	public StringBufferFast append(StringBuffer sb) {
		if (sb == null) {
			return this;
		}
		int len = sb.length();
		int newcount = count + len;
		if (newcount > value.length) expandCapacity(newcount);
		sb.getChars(0, len, value, count);
		count = newcount;
		return this;
	}

	/**
	 * Appends the substring to this string buffer.
	 * <p>
	 * The characters of the <code>String</code> argument are appended, in 
	 * order, to the contents of this string buffer, increasing the 
	 * length of this string buffer by the length of the argument. 
	 *
	 * @param   str   a string.
	 * @return  this string buffer.
	 */
	public StringBufferFast append(StringBuffer sb, int begin, int end) {
		if (begin < 0) {
			throw new StringIndexOutOfBoundsException(begin);
		} 
		if (end > sb.length()) {
			throw new StringIndexOutOfBoundsException(end);
		} 
		if (begin > end) {
			throw new StringIndexOutOfBoundsException(end - begin);
		}
		int len = end - begin;
		int newcount = count + len;
		if (newcount > value.length) expandCapacity(newcount);
		sb.getChars(begin, end, value, count);
		count = newcount;
		return this;
	}
	
    /**
     * Appends the string representation of the <code>char</code> array 
     * argument to this string buffer. 
     * <p>
     * The characters of the array argument are appended, in order, to 
     * the contents of this string buffer. The length of this string 
     * buffer increases by the length of the argument. 
     *
     * @param   str   the characters to be appended.
     * @return  this string buffer.
     * @since   JDK1.0
     */
    public StringBufferFast append(char str[]) {
	int len = str.length;
	int newcount = count + len;
	if (newcount > value.length)
	    expandCapacity(newcount);
	System.arraycopy(str, 0, value, count, len);
	count = newcount;
	return this;
    }

    /**
     * Appends the string representation of a subarray of the 
     * <code>char</code> array argument to this string buffer. 
     * <p>
     * Characters of the character array <code>str</code>, starting at 
     * index <code>offset</code>, are appended, in order, to the contents 
     * of this string buffer. The length of this string buffer increases 
     * by the value of <code>len</code>. 
     *
     * @param   str      the characters to be appended.
     * @param   offset   the index of the first character to append.
     * @param   len      the number of characters to append.
     * @return  this string buffer.
     * @since   JDK1.0
     */
    public StringBufferFast append(char str[], int offset, int len) {
        int newcount = count + len;
	if (newcount > value.length)
	    expandCapacity(newcount);
	System.arraycopy(str, offset, value, count, len);
	count = newcount;
	return this;
    }

    /**
     * Appends the string representation of the <code>boolean</code> 
     * argument to the string buffer. 
     * <p>
     * The argument is converted to a string as if by the method 
     * <code>String.valueOf</code>, and the characters of that 
     * string are then appended to this string buffer. 
     *
     * @param   b   a <code>boolean</code>.
     * @return  this string buffer.
     * @see     java.lang.String#valueOf(boolean)
     * @see     java.lang.StringBufferFast#append(java.lang.String)
     * @since   JDK1.0
     */
    public StringBufferFast append(boolean b) {
	return append(String.valueOf(b));
    }

    /**
     * Appends the string representation of the <code>char</code> 
     * argument to this string buffer. 
     * <p>
     * The argument is appended to the contents of this string buffer. 
     * The length of this string buffer increases by <code>1</code>. 
     *
     * @param   ch   a <code>char</code>.
     * @return  this string buffer.
     * @since   JDK1.0
     */
    public StringBufferFast append(char c) {
        int newcount = count + 1;
	if (newcount > value.length)
	    expandCapacity(newcount);
	value[count++] = c;
	return this;
    }

    /**
     * Appends the string representation of the <code>int</code> 
     * argument to this string buffer. 
     * <p>
     * The argument is converted to a string as if by the method 
     * <code>String.valueOf</code>, and the characters of that 
     * string are then appended to this string buffer. 
     *
     * @param   i   an <code>int</code>.
     * @return  this string buffer.
     * @see     java.lang.String#valueOf(int)
     * @see     java.lang.StringBufferFast#append(java.lang.String)
     * @since   JDK1.0
     */
    public StringBufferFast append(int i) {
	return append(String.valueOf(i));
    }

    /**
     * Appends the string representation of the <code>long</code> 
     * argument to this string buffer. 
     * <p>
     * The argument is converted to a string as if by the method 
     * <code>String.valueOf</code>, and the characters of that 
     * string are then appended to this string buffer. 
     *
     * @param   l   a <code>long</code>.
     * @return  this string buffer.
     * @see     java.lang.String#valueOf(long)
     * @see     java.lang.StringBufferFast#append(java.lang.String)
     * @since   JDK1.0
     */
    public StringBufferFast append(long l) {
	return append(String.valueOf(l));
    }

    /**
     * Appends the string representation of the <code>float</code> 
     * argument to this string buffer. 
     * <p>
     * The argument is converted to a string as if by the method 
     * <code>String.valueOf</code>, and the characters of that 
     * string are then appended to this string buffer. 
     *
     * @param   f   a <code>float</code>.
     * @return  this string buffer.
     * @see     java.lang.String#valueOf(float)
     * @see     java.lang.StringBufferFast#append(java.lang.String)
     * @since   JDK1.0
     */
    public StringBufferFast append(float f) {
	return append(String.valueOf(f));
    }

    /**
     * Appends the string representation of the <code>double</code> 
     * argument to this string buffer. 
     * <p>
     * The argument is converted to a string as if by the method 
     * <code>String.valueOf</code>, and the characters of that 
     * string are then appended to this string buffer. 
     *
     * @param   d   a <code>double</code>.
     * @return  this string buffer.
     * @see     java.lang.String#valueOf(double)
     * @see     java.lang.StringBufferFast#append(java.lang.String)
     * @since   JDK1.0
     */
    public StringBufferFast append(double d) {
	return append(String.valueOf(d));
    }

	/**
	 * Appends a fixed length of the string to this string buffer, 
	 * padding further if necessary.
	 * 
	 * @param   str   a string.
	 * @param   fixed length to fix output (no more, no less).
	 * @param   pad   pad character in case given string is less than the fixed length. 
	 * @return  this string buffer.
	 * @since   WCNA-2004-08
	 */
	public StringBufferFast appendLength(String str, int fixLen, char pad) {
	int len = (str != null) ? str.length() : 0;
	int newcount = count + fixLen;
	if (newcount > value.length)
		expandCapacity(newcount);
	if (fixLen > len) {
		if (len != 0) {
			str.getChars(0, len, value, count); 
			count += len;
		}
		padLength(fixLen-len,pad);
	} else {
		if (len != 0) str.getChars(0, fixLen, value, count);
		count = newcount;
	}
	return this;
	}
	
    /**
     * Inserts the string representation of the <code>Object</code> 
     * argument into this string buffer. 
     * <p>
     * The second argument is converted to a string as if by the method 
     * <code>String.valueOf</code>, and the characters of that 
     * string are then inserted into this string buffer at the indicated 
     * offset. 
     * <p>
     * The offset argument must be greater than or equal to 
     * <code>0</code>, and less than or equal to the length of this 
     * string buffer. 
     *
     * @param      offset   the offset.
     * @param      b        an <code>Object</code>.
     * @return     this string buffer.
     * @exception  StringIndexOutOfBoundsException  if the offset is invalid.
     * @see        java.lang.String#valueOf(java.lang.Object)
     * @see        StringBufferFast#insert(int, java.lang.String)
     * @see        StringBufferFast#length()
     * @since      JDK1.0
     */
    public StringBufferFast insert(int offset, Object obj) {
	return insert(offset, String.valueOf(obj));
    }

	/**
	 * Inserts the string into this string buffer. 
	 * <p>
	 * The characters of the <code>String</code> argument are inserted, in 
	 * order, into this string buffer at the indicated offset. The length 
	 * of this string buffer is increased by the length of the argument. 
	 * <p>
	 * The offset argument must be greater than or equal to 
	 * <code>0</code>, and less than or equal to the length of this 
	 * string buffer. 
	 *
	 * @param      offset   the offset.
	 * @param      str      a string.
	 * @return     this string buffer.
	 * @exception  StringIndexOutOfBoundsException  if the offset is invalid.
	 * @see        StringBufferFast#length()
	 * @since      JDK1.0
	 */
	public StringBufferFast insert(int offset, String str) {
	if ((offset < 0) || (offset > count)) {
		throw new StringIndexOutOfBoundsException();
	}
	int len = str.length();
	int newcount = count + len;
	if (newcount > value.length)
		expandCapacity(newcount);
	System.arraycopy(value, offset, value, offset + len, count - offset);
	str.getChars(0, len, value, offset);
	count = newcount;
	return this;
	}

	/**
	 * Inserts the string into this string buffer. 
	 * <p>
	 * The characters of the <code>String</code> argument are inserted, in 
	 * order, into this string buffer at the indicated offset. The length 
	 * of this string buffer is increased by the length of the argument. 
	 * <p>
	 * The offset argument must be greater than or equal to 
	 * <code>0</code>, and less than or equal to the length of this 
	 * string buffer. 
	 *
	 * @param      offset   the offset.
	 * @param      str      a string.
	 * @return     this string buffer.
	 * @exception  StringIndexOutOfBoundsException  if the offset is invalid.
	 * @see        StringBufferFast#length()
	 * @since      JDK1.0
	 */
	public StringBufferFast insert(int offset, StringBuffer sb) {
		if ((offset < 0) || (offset > count)) {
			throw new StringIndexOutOfBoundsException();
		}
		int len = sb.length();
		int newcount = count + len;
		if (newcount > value.length)
			expandCapacity(newcount);
		System.arraycopy(value, offset, value, offset + len, count - offset);
		sb.getChars(0, len, value, offset);
		count = newcount;
		return this;
	}

    /**
     * Inserts the string representation of the <code>char</code> array 
     * argument into this string buffer. 
     * <p>
     * The characters of the array argument are inserted into the 
     * contents of this string buffer at the position indicated by 
     * <code>offset</code>. The length of this string buffer increases by 
     * the length of the argument. 
     *
     * @param      offset   the offset.
     * @param      ch       a character array.
     * @return     this string buffer.
     * @exception  StringIndexOutOfBoundsException  if the offset is invalid.
     * @since      JDK1.0
     */
    public StringBufferFast insert(int offset, char str[]) {
	if ((offset < 0) || (offset > count)) {
	    throw new StringIndexOutOfBoundsException();
	}
	int len = str.length;
	int newcount = count + len;
	if (newcount > value.length)
		expandCapacity(newcount);
	System.arraycopy(value, offset, value, offset + len, count - offset);
	System.arraycopy(str, 0, value, offset, len);
	count = newcount;
	return this;
	}

	/**
	 * Inserts the string representation of the <code>boolean</code>
	 * argument into this string buffer.
	 * <p>
	 * The second argument is converted to a string as if by the method
	 * <code>String.valueOf</code>, and the characters of that
	 * string are then inserted into this string buffer at the indicated
	 * offset.
     * <p>
     * The offset argument must be greater than or equal to 
     * <code>0</code>, and less than or equal to the length of this 
     * string buffer. 
     *
     * @param      offset   the offset.
     * @param      b        a <code>boolean</code>.
     * @return     this string buffer.
     * @exception  StringIndexOutOfBoundsException  if the offset is invalid.
     * @see        java.lang.String#valueOf(boolean)
     * @see        StringBufferFast#insert(int, java.lang.String)
     * @see        StringBufferFast#length()
     * @since      JDK1.0
     */
    public StringBufferFast insert(int offset, boolean b) {
	return insert(offset, String.valueOf(b));
    }

    /**
     * Inserts the string representation of the <code>char</code> 
     * argument into this string buffer. 
     * <p>
     * The second argument is inserted into the contents of this string 
     * buffer at the position indicated by <code>offset</code>. The length 
     * of this string buffer increases by one. 
     * <p>
     * The offset argument must be greater than or equal to 
     * <code>0</code>, and less than or equal to the length of this 
     * string buffer. 
     *
     * @param      offset   the offset.
     * @param      ch       a <code>char</code>.
     * @return     this string buffer.
     * @exception  StringIndexOutOfBoundsException  if the offset is invalid.
     * @see        StringBufferFast#length()
     * @since      JDK1.0
     */
    public StringBufferFast insert(int offset, char c) {
	int newcount = count + 1;
	if (newcount > value.length)
	    expandCapacity(newcount);
	System.arraycopy(value, offset, value, offset + 1, count - offset);
	value[offset] = c;
	count = newcount;
	return this;
    }

    /**
     * Inserts the string representation of the second <code>int</code> 
     * argument into this string buffer. 
     * <p>
     * The second argument is converted to a string as if by the method 
     * <code>String.valueOf</code>, and the characters of that 
     * string are then inserted into this string buffer at the indicated 
     * offset. 
     * <p>
     * The offset argument must be greater than or equal to 
     * <code>0</code>, and less than or equal to the length of this 
     * string buffer. 
     *
     * @param      offset   the offset.
     * @param      b        an <code>int</code>.
     * @return     this string buffer.
     * @exception  StringIndexOutOfBoundsException  if the offset is invalid.
     * @see        java.lang.String#valueOf(int)
     * @see        StringBufferFast#insert(int, java.lang.String)
     * @see        StringBufferFast#length()
     * @since      JDK1.0
     */
    public StringBufferFast insert(int offset, int i) {
	return insert(offset, String.valueOf(i));
    }

    /**
     * Inserts the string representation of the <code>long</code> 
     * argument into this string buffer. 
     * <p>
     * The second argument is converted to a string as if by the method 
     * <code>String.valueOf</code>, and the characters of that 
     * string are then inserted into this string buffer at the indicated 
     * offset. 
     * <p>
     * The offset argument must be greater than or equal to 
     * <code>0</code>, and less than or equal to the length of this 
     * string buffer. 
     *
     * @param      offset   the offset.
     * @param      b        a <code>long</code>.
     * @return     this string buffer.
     * @exception  StringIndexOutOfBoundsException  if the offset is invalid.
     * @see        java.lang.String#valueOf(long)
     * @see        StringBufferFast#insert(int, java.lang.String)
     * @see        StringBufferFast#length()
     * @since      JDK1.0
     */
    public StringBufferFast insert(int offset, long l) {
	return insert(offset, String.valueOf(l));
    }

    /**
     * Inserts the string representation of the <code>float</code> 
     * argument into this string buffer. 
     * <p>
     * The second argument is converted to a string as if by the method 
     * <code>String.valueOf</code>, and the characters of that 
     * string are then inserted into this string buffer at the indicated 
     * offset. 
     * <p>
     * The offset argument must be greater than or equal to 
     * <code>0</code>, and less than or equal to the length of this 
     * string buffer. 
     *
     * @param      offset   the offset.
     * @param      b        a <code>float</code>.
     * @return     this string buffer.
     * @exception  StringIndexOutOfBoundsException  if the offset is invalid.
     * @see        java.lang.String#valueOf(float)
     * @see        StringBufferFast#insert(int, java.lang.String)
     * @see        StringBufferFast#length()
     * @since      JDK1.0
     */
    public StringBufferFast insert(int offset, float f) {
	return insert(offset, String.valueOf(f));
    }

    /**
     * Inserts the string representation of the <code>double</code> 
     * argument into this string buffer. 
     * <p>
     * The second argument is converted to a string as if by the method 
     * <code>String.valueOf</code>, and the characters of that 
     * string are then inserted into this string buffer at the indicated 
     * offset. 
     * <p>
     * The offset argument must be greater than or equal to 
     * <code>0</code>, and less than or equal to the length of this 
     * string buffer. 
     *
     * @param      offset   the offset.
     * @param      b        a <code>double</code>.
     * @return     this string buffer.
     * @exception  StringIndexOutOfBoundsException  if the offset is invalid.
     * @see        java.lang.String#valueOf(double)
     * @see        StringBufferFast#insert(int, java.lang.String)
     * @see        StringBufferFast#length()
     * @since      JDK1.0
     */
    public StringBufferFast insert(int offset, double d) {
	return insert(offset, String.valueOf(d));
    }

	/**
	 * Replaces the characters in a substring of this <code>StringBuffer</code>
	 * with characters in the specified <code>String</code>. The substring
	 * begins at the specified <code>start</code> and extends to the character
	 * at index <code>end - 1</code> or to the end of the
	 * <code>StringBuffer</code> if no such character exists. First the
	 * characters in the substring are removed and then the specified
	 * <code>String</code> is inserted at <code>start</code>. (The
	 * <code>StringBuffer</code> will be lengthened to accommodate the
	 * specified String if necessary.)
	 * 
	 * @param      start    The beginning index, inclusive.
	 * @param      end      The ending index, exclusive.
	 * @param      str   String that will replace previous contents.
	 * @return     This string buffer.
	 * @exception  StringIndexOutOfBoundsException  if <code>start</code>
	 *             is negative, greater than <code>length()</code>, or
	 *		   greater than <code>end</code>.
	 * @since      1.2
	 */ 
	public StringBufferFast replace(int start, int end, String str) {
		if (start < 0) throw new StringIndexOutOfBoundsException("start "+start);
		if (end > count) throw new StringIndexOutOfBoundsException("end "+ end +" greater than length "+ count);
		if (start > end) throw new StringIndexOutOfBoundsException("start "+ start +" greater than end "+ end);

		int len = str != null ? str.length() : 0;
		int newCount = count + len - (end - start);
		if (newCount > value.length) expandCapacity(newCount);

		System.arraycopy(value, end, value, start + len, count - end);
		if (len != 0) str.getChars(0, len, value, start);
		count = newCount;
		return this;
	}
	
	/**
	 * Compare a region of the buffer to another returning true for an exact match.
	 * @see {@link String#regionMatches(int, String, int, int)}.
	 * 
	 * @param toffset Offset of this object to start comparison.
	 * @param other Other StringBufferFast object.
	 * @param ooffset Offset of other object to start comparison
	 * @param len Length of comparison.
	 * @return true if the sub-region exactly matches, otherwise false.
	 */
	public boolean regionMatches(int toffset, StringBufferFast other, int ooffset, int len) {
		if (toffset < 0 || ooffset < 0 || (toffset > count-len) || (ooffset > other.count-len)) {
			return false;
		}
		int i = toffset;
		int j = ooffset;
		while (len-- > 0) {
			if (value[i++] != other.value[j++]) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Compare a region of the buffer to another returning true for an exact match.
	 * @see {@link String#regionMatches(int, String, int, int)}.
	 * 
	 * @param toffset Offset of this object to start comparison.
	 * @param other Other StringBufferFast object.
	 * @param ooffset Offset of other object to start comparison
	 * @param len Length of comparison.
	 * @return true if the sub-region exactly matches, otherwise false.
	 */
	public boolean regionMatches(int toffset, String other, int ooffset, int len) {
		if (toffset < 0 || ooffset < 0 || (toffset > count-len) || (ooffset > other.length()-len)) {
			return false;
		}
		int i = toffset;
		int j = ooffset;
		while (len-- > 0) {
			if (value[i++] != other.charAt(j++)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Return the location of the given character starting at the beginning, index 0.
	 * Return -1 if not found. 
	 */
	public int indexOf(char c) {
		return indexOf(c, 0);
	}

	/**
	 * Return the location of the given character starting at the given index.
	 * Return -1 if not found.
	 */ 
	public int indexOf(char c, int fromIndex) {
		if (fromIndex >= count) return -1;
		for (int i=fromIndex < 0 ? 0 : fromIndex; i<count; i++) if (value[i] == c) return i;
		return -1;
	}

	/**
	 * Returns the index within this string of the first occurrence of the
	 * specified substring. The integer returned is the smallest value 
	 * <i>k</i> such that:
	 * <blockquote><pre>
	 * this.toString().startsWith(str, <i>k</i>)
	 * </pre></blockquote>
	 * is <code>true</code>.
	 *
	 * @param   str   any string.
	 * @return  if the string argument occurs as a substring within this
	 *          object, then the index of the first character of the first
	 *          such substring is returned; if it does not occur as a
	 *          substring, <code>-1</code> is returned.
	 * @exception java.lang.NullPointerException if <code>str</code> is 
	 *          <code>null</code>.
	 * @since   1.4
	 */
	public int indexOf(String str) {
		return indexOf(str, 0);
	}

	/**
	 * Returns the index within this string of the first occurrence of the
	 * specified substring, starting at the specified index.  The integer
	 * returned is the smallest value <tt>k</tt> for which:
	 * <blockquote><pre>
	 *     k &gt;= Math.min(fromIndex, str.length()) && this.startsWith(str, k)
	 * </pre></blockquote>
	 * If no such value of <i>k</i> exists, then -1 is returned.
	 *
	 * @param   str         the substring for which to search.
	 * @param   fromIndex   the index from which to start the search.
	 * @return  the index within this string of the first occurrence of the
	 *          specified substring, starting at the specified index.
	 */
	public int indexOf(String str, int fromIndex) {
		return StringUtil.indexOf(value, 0, count,
					   str.toCharArray(), 0, str.length(), fromIndex);
	}
	
	/**
	 * Returns the index within this string of the first occurrence of the
	 * specified substring, starting at the specified index.
	 * If no such value of <i>k</i> exists, then -1 is returned.
	 *
	 * @param   str         the substring for which to search.
	 * @param   fromIndex   the index from which to start the search.
	 * @return  the index within this string of the first occurrence of the
	 *          specified substring, starting at the specified index.
	 */
	public int indexOf(char[] str, int fromIndex) {
		return StringUtil.indexOf(value, 0, count,
					   str, 0, str.length, fromIndex);
	}
	
	/**
     * Returns the index within this string of the last occurrence of the
     * specified character, starting the search backward at the specified index.
     * Return -1 if not found.
     */
	public int lastIndexOf(char c, int fromIndex) {
		if (fromIndex > count) return -1;
		for (int i=fromIndex; i>=0; i--) if (value[i] == c) return i;
		return -1;
	}
	
	/**
	 * Returns the index within this string of the rightmost occurrence
	 * of the specified substring.  The rightmost empty string "" is
	 * considered to occur at the index value <code>this.length()</code>.
	 * The returned index is the largest value <i>k</i> such that
	 * <blockquote><pre>
	 * this.startsWith(str, k)
	 * </pre></blockquote>
	 * is true.
	 *
	 * @param   str   the substring to search for.
	 * @return  if the string argument occurs one or more times as a substring
	 *          within this object, then the index of the first character of
	 *          the last such substring is returned. If it does not occur as
	 *          a substring, <code>-1</code> is returned.
	 */
	public int lastIndexOf(String str) {
	return lastIndexOf(str, count);
	}

	/**
	 * Returns the index within this string of the last occurrence of the
	 * specified substring, searching backward starting at the specified index.
	 * The integer returned is the largest value <i>k</i> such that:
	 * <blockquote><pre>
	 *     k &lt;= Math.min(fromIndex, str.length()) && this.startsWith(str, k)
	 * </pre></blockquote>
	 * If no such value of <i>k</i> exists, then -1 is returned.
	 * 
	 * @param   str         the substring to search for.
	 * @param   fromIndex   the index to start the search from.
	 * @return  the index within this string of the last occurrence of the
	 *          specified substring.
	 */
	public int lastIndexOf(String str, int fromIndex) {
		return StringUtil.lastIndexOf(value, 0, count,
						   str.toCharArray(), 0, str.length(), fromIndex);
	}

	/**
	 * Returns the index within this string of the last occurrence of the
	 * specified substring, searching backward starting at the specified index.
	 * If no such value of <i>k</i> exists, then -1 is returned.
	 * 
	 * @param   str         the substring to search for.
	 * @param   fromIndex   the index to start the search from.
	 * @return  the index within this string of the last occurrence of the
	 *          specified substring.
	 */
	public int lastIndexOf(char[] str, int fromIndex) {
		return StringUtil.lastIndexOf(value, 0, count,
						   str, 0, str.length, fromIndex);
	}

    /**
     * The character sequence contained in this string buffer is 
     * replaced by the reverse of the sequence. 
     *
     * @return  this string buffer.
     * @since   JDK1.0.2
     */
	public StringBufferFast reverse() {
	int n = count - 1;
	for (int j = (n-1) >> 1; j >= 0; --j) {
	    char temp = value[j];
	    value[j] = value[n - j];
	    value[n - j] = temp;
	}
	return this;
    }

	/**
	 * Set the count to zero, essentially clearing the buffer.
	 */
	public void reset() {
		count = 0;
	}

    /**
     * Read available characters from the specified <code>Reader</code>.
     *
     * @param		in   the reader from which data will be read.	
     * @return		the number of bytes read or -1 at the end-of-stream.
     * @exception	IOException  if an I/O error occurs.
     */
    public int readFrom(Reader in) throws IOException {
    	ensureCapacity(count + blockSize);
		int len = in.read(value, count, value.length - count);
		if (len > 0) count += len;
        return len;
    }
    
	/**
	 * Write the contents of this buffer to the specified <code>Writer</code>.
	 *
	 * @param      out   the output stream to which to write the data.
	 * @exception  IOException  if an I/O error occurs.
	 */
	public void writeTo(Writer out) throws IOException {
		out.write(value, 0, count);
	}

	/**
	 * Write the contents of this buffer to the specified <code>Writer</code>.
	 * Reset the buffer
	 *
	 * @param      out   the output stream to which to write the data.
	 * @exception  IOException  if an I/O error occurs.
	 */
	public void writeToAndReset(Writer out) throws IOException {
		out.write(value, 0, count);
		reset();
	}
	
    /**
     * Converts to a string representing the data in this string buffer.
     * A new <code>String</code> object is allocated and initialized to 
     * contain the character sequence currently represented by this 
     * string buffer. This <code>String</code> is then returned. Subsequent 
     * changes to the string buffer do not affect the contents of the 
     * <code>String</code>. 
     *
     * @return  a string representation of the string buffer.
     * @since   JDK1.0
     */
    public String toString() {
	return (count > 0) ? new String(value, 0, count) : EMPTY_STRING;
    }

    /**
	 * Return a substring from the current string buffer.
	 * 
	 * @param fromIndex Inclusive.
	 * @param toIndex Exclusive.
	 */
	public String substring(int fromIndex, int toIndex) {
		if (toIndex > count) throw new StringIndexOutOfBoundsException("toIndex "+ toIndex);
		else if (count == 0 && fromIndex == 0 && toIndex == 0) return EMPTY_STRING;
		else return new String(value, fromIndex, toIndex-fromIndex);
	}
	
    /**
     * Return a subsequence from the current string buffer.
     * 
     * @param fromIndex Inclusive.
	 * @param toIndex Exclusive.
     */
    public CharSequence subSequence(int fromIndex, int toIndex) {
    	return substring(fromIndex, toIndex);
    }
    
    public StringBufferFast append(CharSequence csq) {
    	if (csq == null) return this;
    	return append(csq, 0, csq.length());
    }
	
    public StringBufferFast append(CharSequence csq, int fromIndex, int toIndex) {
    	if (csq == null) return this;

    	int len = toIndex-fromIndex;
    	int newcount = count + len;
    	if (newcount > value.length)
    		expandCapacity(newcount);
    	if (csq instanceof String) {
    		((String) csq).getChars(fromIndex, toIndex, value, count);
    	} else if (csq instanceof StringBuffer) {
    		((StringBuffer) csq).getChars(fromIndex, toIndex, value, count);
    	} else if (csq instanceof StringBufferFast) {
    		((StringBufferFast) csq).getChars(fromIndex, toIndex, value, count);
    	} else if (csq instanceof StringBuilder) {
    		((StringBuilder) csq).getChars(fromIndex, toIndex, value, count);
    	} else {
        	csq.subSequence(fromIndex, toIndex).toString().getChars(0, len, value, count);
    	}
    	count = newcount;
    	return this;
    }
	
    //
    // The following two methods are needed by String to efficiently
	// convert a StringBufferFast into a String.  They are not public.
    // They shouldn't be called by anyone but String.
	final char[] getInternalValue() { return value; }

}
