package akme.core.util;

import java.io.Serializable;

/**
 * Efficient dynamic list/stack of primitive ints.
 *
 * @author Copyright(c) 2004 AKME Solutions
 * @author keith.mashinter
 * @author $Author: keith.mashinter $
 * @version $Date: 2007/01/11 20:30:08 $
 * $NoKeywords: $
 */
public class IntList implements Cloneable, Serializable {

	private static final long serialVersionUID =  2174872369450874634L;

	private static int DEFAULT_SIZE = 4;

	/**
	 * The value is used for internal storage.
	 */
	private int value[];

	/**
	 * The number of integers in the list/stack.
	 *
	 * @serial
	 */
	private int count;

	/**
	 * Construct with default initial size 4.
	 */
	public IntList() {
		this(DEFAULT_SIZE);
		count = 0;
	}

	/**
	 * Construct with the given size.
	 */
	public IntList(int n) {
		value = new int[n];
		count = 0;
	}

	/**
	 * Construct with a copy of the given array.
	 */
	public IntList(int[] ary) {
		if (ary == null) {
			value = new int[0]; 
		} else { 
			value = new int[ary.length];
			count = value.length;
			System.arraycopy(ary, 0, value, 0, count);
		}
	}

	/**
	 * Construct a copy of the given list.
	 */
	public IntList(IntList list) {
		if (list == null) {
			value = new int[0];
		} else { 
			value = new int[list.size()];
			count = value.length;
			System.arraycopy(list.value, 0, value, 0, count);
		}
	}

	/**
	 * Creates a shallow copy of this object, preserving the internal structure
	 * by copying only references.  The keys and values themselves are not
	 * <code>clone()</code>'d.  The cloned object maintains the same sequence.
	 *
	 * @return A clone of this instance.
	 *
	 * @exception CloneNotSupportedException if clone is not supported by a
	 * subclass.
	 */
	public Object clone() throws CloneNotSupportedException {
		// see http://www.javaworld.com/javaworld/jw-01-1999/jw-01-object.html for super.clone();		
		IntList result = (IntList) super.clone();
		result.value = new int[value.length];
		System.arraycopy(value, 0, result.value, 0, count);
		return result;
	}

	/**
	 * Return the length (character count) of this string buffer.
	 *
	 * @return  the length of the sequence of characters currently
	 *          represented by this string buffer.
	 */
	public int size() {
		return count;
	}

	/**
	  * This implements the expansion semantics of ensureCapacity but is
	  * unsynchronized for use internally by methods which are already
	  * synchronized.
	  *
	  * @see java.lang.StringBuffer#ensureCapacity(int)
	  */
	private void ensureLength(int minLength) {
		if (minLength <= value.length) return;
		int newLength = (value.length + 1) * 2;
		if (newLength < 0) {
			newLength = Integer.MAX_VALUE;
		} else if (minLength > newLength) {
			newLength = minLength;
		}
		int newValue[] = new int[newLength];
		System.arraycopy(value, 0, newValue, 0, count);
		value = newValue;
	}

	/**
	 * Add the given integer.
	 * @param i
	 */
	public void add(int i) {
		ensureLength(count + 1);
		value[count] = i;
		count++;
	}

	/**
	 * Add all integers in the given array.
	 * @param ary
	 */
	public void addAll(int[] ary) {
		if (ary == null) return;
		int newLen = count + ary.length;
		ensureLength(newLen);
		System.arraycopy(ary, 0, value, count, ary.length);
		count = newLen;
	}

	/**
	 * Add all integers in the given list.
	 * @param list
	 */
	public void addAll(IntList list) {
		if (list == null) return;
		int newLen = count + list.value.length;
		ensureLength(newLen);
		System.arraycopy(list.value, 0, value, count, list.value.length);
		count = newLen;
	}

	/**
	 * Check if the list contains the given value.
	 *
	 * @param n Int value to find.
	 * @return True if found.
	 */
	public boolean contains(int n) {
		for (int i=0; i<count; i++) {
			if (value[i] == n) return true;
		}
		return false;
	}

	/**
	 * Set the integer value at the given position.
	 *
	 * @param n Position within the internal storage (0-based).
	 * @param i The value to set at the given position.
	 */
	public void set(int n, int i) {
		if (n >= count) throw new ArrayIndexOutOfBoundsException("Index "+ n +" exceeds 0-based size "+ count);
		value[n] = i;
	}

	/**
	 * Get the integer value at the given position
	 * @param n Position within the internal storage (0-based).
	 * @return The value at that position.
	 */
	public int get(int n) {
		return value[n];
	}

	/**
	 * Get the top, last integer in the list/stack.
	 * @return
	 */
	public int get() {
		return value[count-1];
	}

	/**
	 * Remove the last integer value in the list/stack.
	 * @return The value removed.
	 */
	public int remove() {
		int result = value[count-1];
		if (count > 0) count--;
		return result;
	}

	/**
	 * Return a new int[] with the values in this list.
	 * @return A new array of the values.
	 */
	public int[] toArray() {
		int[] result = new int[count];
		System.arraycopy(value,0,result,0,count);
		return result;
	}
}
