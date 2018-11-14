package akme.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

/**
 * Utility class for Array-related functionality.
 *
 * @author Copyright(c) 2006 AKME Solutions
 * @author $Author: keith.mashinter $
 * @version $Date: 2007/04/26 13:18:38 $ 
 * $NoKeywords: $
 */
public abstract class ArrayUtil {

	public static final byte[] EMPTY_BYTE_ARRAY = {};
	public static final char[] EMPTY_CHAR_ARRAY = {};
	public static final int[] EMPTY_INT_ARRAY = {};
	public static final long[] EMPTY_LONG_ARRAY = {};
	public static final String[] EMPTY_STRING_ARRAY = {};
	public static final Object[] EMPTY_OBJECT_ARRAY = {};

	/**
	 * Hex pairs from 00 to FF.
	 */
	private static final char[] HEX2 = {
    '0','0', '0','1', '0','2', '0','3', '0','4', '0','5', '0','6', '0','7',
    '0','8', '0','9', '0','a', '0','b', '0','c', '0','d', '0','e', '0','f',
    '1','0', '1','1', '1','2', '1','3', '1','4', '1','5', '1','6', '1','7',
    '1','8', '1','9', '1','a', '1','b', '1','c', '1','d', '1','e', '1','f',
    '2','0', '2','1', '2','2', '2','3', '2','4', '2','5', '2','6', '2','7',
    '2','8', '2','9', '2','a', '2','b', '2','c', '2','d', '2','e', '2','f',
    '3','0', '3','1', '3','2', '3','3', '3','4', '3','5', '3','6', '3','7',
    '3','8', '3','9', '3','a', '3','b', '3','c', '3','d', '3','e', '3','f',
    '4','0', '4','1', '4','2', '4','3', '4','4', '4','5', '4','6', '4','7',
    '4','8', '4','9', '4','a', '4','b', '4','c', '4','d', '4','e', '4','f',
    '5','0', '5','1', '5','2', '5','3', '5','4', '5','5', '5','6', '5','7',
    '5','8', '5','9', '5','a', '5','b', '5','c', '5','d', '5','e', '5','f',
    '6','0', '6','1', '6','2', '6','3', '6','4', '6','5', '6','6', '6','7',
    '6','8', '6','9', '6','a', '6','b', '6','c', '6','d', '6','e', '6','f',
    '7','0', '7','1', '7','2', '7','3', '7','4', '7','5', '7','6', '7','7',
    '7','8', '7','9', '7','a', '7','b', '7','c', '7','d', '7','e', '7','f',
    '8','0', '8','1', '8','2', '8','3', '8','4', '8','5', '8','6', '8','7',
    '8','8', '8','9', '8','a', '8','b', '8','c', '8','d', '8','e', '8','f',
    '9','0', '9','1', '9','2', '9','3', '9','4', '9','5', '9','6', '9','7',
    '9','8', '9','9', '9','a', '9','b', '9','c', '9','d', '9','e', '9','f',
    'a','0', 'a','1', 'a','2', 'a','3', 'a','4', 'a','5', 'a','6', 'a','7',
    'a','8', 'a','9', 'a','a', 'a','b', 'a','c', 'a','d', 'a','e', 'a','f',
    'b','0', 'b','1', 'b','2', 'b','3', 'b','4', 'b','5', 'b','6', 'b','7',
    'b','8', 'b','9', 'b','a', 'b','b', 'b','c', 'b','d', 'b','e', 'b','f',
    'c','0', 'c','1', 'c','2', 'c','3', 'c','4', 'c','5', 'c','6', 'c','7',
    'c','8', 'c','9', 'c','a', 'c','b', 'c','c', 'c','d', 'c','e', 'c','f',
    'd','0', 'd','1', 'd','2', 'd','3', 'd','4', 'd','5', 'd','6', 'd','7',
    'd','8', 'd','9', 'd','a', 'd','b', 'd','c', 'd','d', 'd','e', 'd','f',
    'e','0', 'e','1', 'e','2', 'e','3', 'e','4', 'e','5', 'e','6', 'e','7',
    'e','8', 'e','9', 'e','a', 'e','b', 'e','c', 'e','d', 'e','e', 'e','f',
    'f','0', 'f','1', 'f','2', 'f','3', 'f','4', 'f','5', 'f','6', 'f','7',
    'f','8', 'f','9', 'f','a', 'f','b', 'f','c', 'f','d', 'f','e', 'f','f'};
	
	/**
	 * Convert two characters representing hex 00 to ff (or FF) to a byte.
	 */
	static int hexToByte(char c1, char c2) {
		int b;
		if ((c1 >= 'a')) b = c1 - 'a' + 10;
		else if (c1 >= 'A') b = c1 - 'A' + 10;
		else b = c1 - '0';
		b <<= 4;
		if ((c2 >= 'a')) b |= c2 - 'a' + 10;
		else if (c2 >= 'A') b |= c2 - 'A' + 10;
		else b |= c2 - '0';
		return b;
	}
	
	/**
	 * Convert the given byte array to hex characters, doubling the length since one char holds 4-bits (hex 0-f).
	 */
	public static char[] toUnsignedHex(byte[] buf) {
		final char[] result = new char[buf.length << 1];
		for (int i=0; i<buf.length; i++) {
			System.arraycopy(HEX2, ((int)buf[i] & 0xff)<<1, result, i<<1, 2);
		}
		return result;
	}

	/**
	 * Copy the given byte as unsigned hex to the given character array at the given starting point. 
	 */
	public static int copyUnsignedHex(char[] ary, int aryStart, int byt) {
		//ary[artStart++] = HEX1[(byt >> 4) & 0x0f];
		//ary[artStart++] = HEX1[byt & 0x0f];
		System.arraycopy(HEX2, byt<<1, ary, aryStart, 2);
		return 2;
	}

	/**
	 * Convert the given byte array to hex characters, doubling the length since one char holds 4-bits (hex 0-f).
	 */
	public static void writeUnsignedHex(StringBuilder sb, byte[] buf) {
		for (int i=0; i<buf.length; i++) {
			sb.append(HEX2, ((int)buf[i] & 0xff)<<1, 2);
		}
	}

	/**
	 * Write the given byte as unsigned hex to the given buffer. 
	 */
	public static void writeUnsignedHex(StringBuilder sb, int byt) {
		sb.append(HEX2, byt<<1, 2);
	}

	/**
	 * Write the given byte array as unsigned hex to the given Writer. 
	 */
	public static void writeUnsignedHex(Writer wr, byte[] buf) throws IOException {
		for (int i=0; i<buf.length; i++) {
			wr.write(HEX2, ((int)buf[i] & 0xff)<<1, 2);
		}
	}

	/**
	 * Write the given byte as unsigned hex to the given Writer. 
	 */
	public static void writeUnsignedHex(Writer wr, int byt) throws IOException {
		wr.write(HEX2, byt<<1, 2);
	}

	/**
	 * Convert the given hex character char array to bytes, with half the length since one char holds 4-bits (hex 0-f).
	 */
	public static byte[] toBytesFromUnsignedHex(char[] buf) {
		final byte[] result = new byte[buf.length >>> 1];
		int j=0;
		for (int i=0; i<buf.length; i+=2) {
			result[j++] = (byte) hexToByte(buf[i],buf[i+1]); 
		}
		return result;
	}

	/**
	 * Convert the given hex character char array to bytes, with half the length since one char holds 4-bits (hex 0-f).
	 */
	public static void writeBytesFromUnsignedHex(ByteArrayOutputStream os, char[] buf) {
		for (int i=0; i<buf.length; i+=2) {
			os.write( hexToByte(buf[i],buf[i+1]) );
		}
	}
	
	/**
	 * Convert 4-hex-digits XXXX to the actual Unicode character, 
	 * i.e. provide exactly the trailing 4 hex characters from "\\uXXXX", no more, no less.
	 */
	public static char toCharFromUnsignedHex4(char[] buf) {
		if (buf == null || buf.length != 4) throw new IllegalArgumentException("buf == null || buf.length != 4");
		return (char) ((int) hexToByte(buf[2], buf[3]) | (hexToByte(buf[0], buf[1]) << 16));
	}

	/**
	 * Convert to bytes in network/big-endian order, most significant position [0].
	 */
	public static byte[] toBytes(int val) {
		final byte[] result = new byte[4];
		putBytes(result,0,val);
		return result;
	}
	
	/**
	 * Convert to bytes in network/big-endian order, most significant position [0].
	 */
	public static int putBytes(byte[] buf, int pos, int val) {
		buf[pos++] = (byte) (val >>> 24);
		buf[pos++] = (byte) (val >>> 16);
		buf[pos++] = (byte) (val >>> 8);
		buf[pos++] = (byte) (val >>> 0);
		return pos;
	}

	/**
	 * Convert to bytes in network/big-endian order, most significant position [0].
	 */
	public static byte[] toBytes(long val) {
		final byte[] result = new byte[8];
		putBytes(result,0,val);
		return result;
	}
	
	/**
	 * Convert to bytes in network/big-endian order, most significant position [0].
	 */
	public static int putBytes(byte[] buf, int pos, long val) {
		buf[pos++] = (byte) (val >>> 56);
		buf[pos++] = (byte) (val >>> 48);
		buf[pos++] = (byte) (val >>> 40);
		buf[pos++] = (byte) (val >>> 32);
		buf[pos++] = (byte) (val >>> 24);
		buf[pos++] = (byte) (val >>> 16);
		buf[pos++] = (byte) (val >>> 8);
		buf[pos++] = (byte) (val >>> 0);
		return pos;
	}
	
	/**
	 * Extract an int using 4 bytes from byte array at the given position.
	 */
	public static int toInt(byte[] buf, int pos) {
		return (((int)buf[pos++] & 0xff) << 24)
			| (((int)buf[pos++] & 0xff) << 16)
			| (((int)buf[pos++] & 0xff) << 8)
			| (((int)buf[pos++] & 0xff) << 0);
	}

	/**
	 * Extract a long using 8 bytes from byte array at the given position.
	 */
	public static long toLong(byte[] buf, int pos) {
		return (((long)buf[pos++] & 0xff) << 56)
			| (((long)buf[pos++] & 0xff) << 48)
			| (((long)buf[pos++] & 0xff) << 40)
			| (((long)buf[pos++] & 0xff) << 32)		
			| (((long)buf[pos++] & 0xff) << 24)
			| (((long)buf[pos++] & 0xff) << 16)
			| (((long)buf[pos++] & 0xff) << 8)
			| (((long)buf[pos++] & 0xff) << 0);
	}

	public static final boolean equals(Object[] a1, Object[] a2) {
		return Arrays.equals(a1, a2);
	}
	
	/**
	 * Check if the array contains the given object.
	 * An Object given as null will match an array value that is null.
	 */
	public static final boolean contains(Object[] a, Object o) {
		return indexOf(a, o) != -1;
	}

	/**
	 * Find the first index of the given value in the array, or -1 if not found. 
	 * An Array given as null will not match anything.
	 * An Object given as null will match an array value that is null.
	 */
	public static final int indexOf(Object[] a, Object o) {
		if (a == null) return -1;
		for (int i=0; i<a.length; i++) {
			if (a[i] == null && o == null) return i;
			if (a[i] != null && o != null && a[i].equals(o)) return i;
		}
		return -1;
	}

	/**
	 * Find the last index of the given value in the array, or -1 if not found. 
	 * An Array given as null will not match anything.
	 * An Object given as null will match an array value that is null.
	 */
	public static final int lastIndexOf(Object[] a, Object o) {
		if (a == null) return -1;
		for (int i=a.length-1; i>=0; i--) {
			if (a[i] == null && o == null) return i;
			if (a[i] != null && o != null && a[i].equals(o)) return i;
		}
		return -1;
	}
	
	/** 
	 * Add one object to the array returning the new array.
	 * Consider using an ArrayList instead -- this is not for normal use but just for special cases.
	 * The new array is constructed using Array.newInstance() with the same ComponentType as the 
	 * first array, or the Object type if the first array is null.
	 */
	public static final Object[] addOne(Object[] a, Object o) {
		if (o == null) return a;
		Object[] result;
		if (a == null) {
			result = (Object[]) Array.newInstance(o.getClass(), 1);
		} else {
			result = (Object[]) Array.newInstance(a.getClass().getComponentType(), a.length + 1);
			System.arraycopy(a, 0, result, 0, a.length);
		}
		result[result.length-1] = o;
		return result;
	}

	/** 
	 * Add the second array to the first one, returning the new combined array.
	 * Consider using an ArrayList instead -- this is not for normal use but just for special cases.
	 * The new array is constructed using Array.newInstance() with the same ComponentType as the 
	 * first array, or the Object type if the first array is null.
	 */
	public static final Object[] addAll(Object[] a, Object[] a2) {
		if (a2 == null) return a;
		Object[] result;
		if (a == null) {
			result = (Object[]) Array.newInstance(a2.getClass().getComponentType(), a2.length);
		} else {
			result = (Object[]) Array.newInstance(a.getClass().getComponentType(), a.length + a2.length);
			System.arraycopy(a, 0, result, 0, a.length);
		}
		System.arraycopy(a2, 0, result, result.length - a2.length, a2.length);
		return result;
	}

	/**
	 * Copy/set the given elements to the array from the start index.
	 * @return The given array or a new array, if the given was too small, with the elements set. 
	 */
	public static final <T> T[] copyAll(T[] ary, int aryStart, T... elems) {
		final int n = aryStart + elems.length;
		final T[] result = (n > ary.length) ? Arrays.copyOf(ary, n) : ary;
		System.arraycopy(elems, 0, result, aryStart, elems.length);
		return result;
	}
	/**
	 * Copy/set the given collection elements to the array from the start index.
	 * @return The given array or a new array, if the given was too small, with the elements set. 
	 */
	public static final <T> T[] copyAll(T[] ary, int aryStart, T[]... elems) {
		int n = 0;
		for (int i=0; i<elems.length; i++) n += elems[i].length;
		final T[] result = (n > ary.length) ? Arrays.copyOf(ary, n) : ary;
		for (int i=0; i<elems.length; i++) {
			n = elems[i].length;
			System.arraycopy(elems[i], 0, result, aryStart, n);
			aryStart += n;
		}
		return result;
	}
	/**
	 * Copy/set the given collection elements to the array from the start index.
	 * @return The given array or a new array, if the given was too small, with the elements set. 
	 */
	public static final <T> T[] copyAll(T[] ary, int aryStart, Collection<? extends T> coll) {
		final int n = aryStart + coll.size();
		final T[] result = (n > ary.length) ? Arrays.copyOf(ary, n) : ary;
		for (T item : coll) result[aryStart++] = item;
		return result;
	}
	/**
	 * Copy/set the given collection elements to the array from the start index.
	 * @return The given array or a new array, if the given was too small, with the elements set. 
	 * @see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6227971
	 */
	public static final <T> T[] copyAll(T[] ary, int aryStart, Collection<? extends T>... elems) {
		int n = 0;
		for (int i=0; i<elems.length; i++) n += elems[i].size();
		final T[] result = (n > ary.length) ? Arrays.copyOf(ary, n) : ary;
		for (int i=0; i<elems.length; i++) {
			for (T item : elems[i]) result[aryStart++] = item;
		}
		return result;
	}
	
	/**
	 * Ensure the given array is at least the given length, returning a new one if not.
	 * @return The given array or a larger copy if too small.
	 */
	public static final <T> T[] ensureLength(T[] ary, int len) {
		return (len > ary.length) ? Arrays.copyOf(ary, len) : ary;
	}
	
	/**
	 * Convert the given Object[] to a mutable ArrayList, rather than the Array.asList().
	 */
	public static final ArrayList<?> toArrayList(Object[] a) {
		return new ArrayList<Object>(Arrays.asList(a));
	}
	
	/**
	 * Convert the given Object[] to a fixed, immutable List.
	 */
	public static final List<?> toFixedList(Object[] a) {
		return Arrays.asList(a);
	}

	/**
	 * Convert a list to an ArrayList of Object[colCount]. 
	 */
	public static final ArrayList<Object[]> toArrayListOfObjectArrays(Collection<?> coll, int colCount) {
		ArrayList<Object[]> result = new ArrayList<Object[]>(coll.size()/colCount+1);
		Object[] row = null;
		if (coll instanceof List && coll instanceof RandomAccess) {
			List<?> list = (List<?>) coll;
			for (int i=0; i<coll.size(); i++) {
				if (i % colCount == 0) {
					row = new Object[Math.min(colCount,coll.size()-i)];
					result.add(row);
				} 
				row[i % colCount] = list.get(i);
			}
		} else {
			int i=0;
			for (Iterator<?> it=coll.iterator(); it.hasNext(); i++) {
				if (i % colCount == 0) {
					row = new Object[Math.min(colCount,coll.size()-i)];
					result.add(row);
				}
				row[i % colCount] = it.next();
			}
		}
		return result;
	}

	public static final Object[] toObjectArray(Collection<?> coll) {
		return coll.toArray();
	}
	
	public static final String[] toStringArray(Collection<?> coll) {
		return (String[]) coll.toArray(new String[coll.size()]);
 	}
	
	public static final Number[] toNumberArray(Collection<?> coll) {
		return (Number[]) coll.toArray(new Number[coll.size()]);
 	}
	
	public static final Date[] toDateArray(Collection<?> coll) {
		return (Date[]) coll.toArray(new Date[coll.size()]);
 	}

	public static final Calendar[] toCalendarArray(Collection<?> coll) {
		return (Calendar[]) coll.toArray(new Calendar[coll.size()]);
 	}
	
	public static final Boolean[] toBooleanArray(Collection<?> coll) {
		return (Boolean[]) coll.toArray(new Boolean[coll.size()]);
	}
	
	/**
	 * Add [key1, value1, key2, value2, ...] given array element pairs to the result map.
	 */
	@SuppressWarnings("unchecked")
	public static final Map toMap(Map result, Object[] given) {
		for (int i=0; i<given.length; i+=2) result.put(given[i], given[i+1]);
		return result;
	}
	
}
