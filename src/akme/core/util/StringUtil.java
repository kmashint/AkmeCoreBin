package akme.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import akme.core.util.IntList;

/**
 * Utility class for manipulating String values.
 * Leverage org.apache.commons.lang.* where possible but those are often slower.
 *
 * @author Copyright(c) 2003 AKME Solutions
 * @author $Author: keith.mashinter $
 * @version $Date: 2007/08/14 18:14:33 $
 * $NoKeywords: $
 */
public abstract class StringUtil {

	public static final String EMPTY_STRING = "";
	public static final String COMMA_STRING = ",";
	public static final String LESS_THAN_ZERO = " < 0"; 

	/** Whitespace chars as defined by Character.isWhitespace. */
	public static final String WHITESPACE_CHARS = "\t\n\u000B\f\r\u001C\u001D\u001E\u001F\u00A0\u2007\u202F";

	/** String of digits. */
    public static final int MAX_LONG_DIGITS = String.valueOf(Long.MAX_VALUE).length();

	/** Check if a string is null or zero-length. */
	public static boolean isEmpty(String value) {
		return value == null || value.length() == 0;
	}

	/** Check if a string is null or zero-length or all whitespace. */
	public static boolean isBlank(String value) {
		return value == null || value.length() == 0 || value.trim().length() == 0;
	}

	/** Substring method that returns null if given null. */
	public static String substring(String str, int beginIndex) {
		if (str == null) return null;
		else {
			return str.substring(beginIndex);
		}
	}

	/** Substring method that returns null if given null. */
	public static String substring(String str, int beginIndex, int endIndex) {
		if (str == null) return null;
		else if (endIndex <= str.length()){
			return str.substring(beginIndex,endIndex);
		} else {
			return str.substring(beginIndex);
		}
	}
	
	/** Limit the length of the string and use elipses for the last 3 characters if longer than the given max. */
	public static String limitLengthWithEllipsis(String str, int max) {
		if (str != null && str.length() > max) {
			return str.substring(0, max-3) + "...";
		} else {
			return str;
		}		
	}

	/** Append newVal to the baseStr if the newVal is not empty (null or zero-length). */
	public static void appendNotEmpty(Appendable sb, String newVal) {
		if (!isEmpty(newVal)) {
			try { sb.append(newVal); }
			catch (IOException ex) { throw new IllegalStateException(ex); }
		}
	}

	/** Append newVal to the baseStr if the newVal is not empty (null or zero-length) using a delimiter. */
	public static <T extends Appendable & CharSequence> void appendNotEmpty(T sb, String newVal, char delimit) {
		if (!isEmpty(newVal)) {
			try {
				if (sb.length() > 0) sb.append(delimit);
				sb.append(newVal);
			}
			catch (IOException ex) { throw new IllegalStateException(ex); }
		}
	}

	/** Append newVal to the baseStr if the newVal is not empty (null or zero-length). */
	public static void appendNotEmpty(StringBuilder sb, String newVal) {
		if (!isEmpty(newVal)) {
			sb.append(newVal);
		}
	}

	/** Append newVal to the baseStr if the newVal is not empty (null or zero-length) using a delimiter. */
	public static void appendNotEmpty(StringBuilder sb, String newVal, char delimit) {
		if (!isEmpty(newVal)) {
			if (sb.length() > 0) sb.append(delimit);
			sb.append(newVal);
		}
	}

	/**
	 * @return true if either both strings are null or if they are
	 * equal, false otherwise.
	 */
	public static boolean equal(String value1, String value2) {

	  if ( (value1 == null) && (value2 == null)) {
		return true;
	  }

	  if ( (value1 == null) || (value2 == null)) {
		return false;
	  }

	  return value1.equals(value2);
	}

	/** Count the occurances of a character in a string. */
	public static int count(String str, char chr) {
		return count(str, chr, 0);
	}

	/** Count the occurances of a character in a string from the start index. */
	public static int count(String str, char chr, int start) {
		if (str == null || str.length() == 0) return 0;
		int n = 0;
		for (int i = str.indexOf(chr,start); i != -1; i = str.indexOf(chr,i+1)) {
			n++;
		}
		return n;
	}

	/** Count the occurances of a substring in a string. */
	public static int count(String str, String sub) {
		return count(str, sub, 0);
	}

	/** Count the occurances of a substring in a string. */
	public static int count(String str, String sub, int start) {
		if (str == null || sub == null || str.length() == 0 || sub.length() == 0) return 0;
		int n = 0;
		for (int i = str.indexOf(sub,start); i != -1; i = str.indexOf(sub,i+sub.length())) {
			n++;
		}
		return n;
	}

    /** 
	 * Pad the last two digits of a positive integer value with zeros to length 2 (high-efficiency).
	 * e.g. <code>pos += StringUtil.padZeroLeftFastMax2(result, pos, cal.get(Calendar.MINUTE));</code>
	 * 
	 * @return the constant 2 as the digits added.
	 */
	public static int padZeroLeftFastMax2(final char[] result, final int start, final int value) {
		if (value < 0) throw new IllegalArgumentException(value + LESS_THAN_ZERO);
		result[start] = (char) (value/10%10 +'0');
		result[start+1] = (char) (value%10 +'0');
		return 2;
	}

	/** 
	 * Pad the positive value with zeros to at most the given length (high-efficiency).
	 * e.g. <code>pos += StringUtil.padZeroLeftFastMaxLength(result, pos, cal.get(Calendar.YEAR), 4);</code>
	 * 
	 * @return the given length.
	 */
	public static int padZeroLeftFastMaxLength(final char[] result, final int start, int value, final int length) {
		if (value < 0) throw new IllegalArgumentException(value + LESS_THAN_ZERO);
		for (int i=length; i-- > 0;) { result[start+i] = (char) (value%10 + '0'); value /= 10; }
		return length;
	}

	/** 
	 * Pad the positive value with zeros to at most the given length (high-efficiency).
	 * e.g. <code>pos += StringUtil.padZeroLeftFastMaxLength(result, pos, cal.get(Calendar.YEAR), 4);</code>
	 * 
	 * @return the given length.
	 */
	public static int padZeroLeftFastMaxLength(final char[] result, final int start, long value, final int length) {
		if (value < 0) throw new IllegalArgumentException(value + LESS_THAN_ZERO);
		for (int i=length; i-- > 0;) { result[start+i] = (char) (value%10 + '0'); value /= 10; }
		return length;
	}

    /** 
     * Pad an integer value with zeros to the given length (high-efficiency). 
     */
	public static String padZeroLeft(long value, int length) {
		return String.valueOf(padZeroLeftFast(value,length));
	}

    /** 
     * Pad an integer value with zeros to the given length (high-efficiency). 
     */
    public static char[] padZeroLeftFast(long value, int length) {
        int negSign = 0;
        if (value < 0) {
            negSign = 1;
            value = -value;
        }
        char[] result = new char[length + negSign];

        // 1. pad up to the given length
        for (int i = result.length - 1; i >= negSign; i--) {
            if (value > 0) {
                result[i] = (char)((long)value % 10 + '0');
                value /= 10;
            }
            else {
                result[i] = '0';
            }
        }

        // 2. if there are still digits remaining, prepend the extra digits
        if (value > 0) {
            char[] result2 = new char[MAX_LONG_DIGITS + negSign];
            int i = result2.length - length;
            System.arraycopy(result, negSign, result2, i, length);
            while (value > 0) {
                i--;
                result2[i] = (char)((long)value % 10 + '0');
                value /= 10;
            }
            int len2 = result2.length - i;
            result = new char[len2 + negSign];
            System.arraycopy(result2, i, result, negSign, len2);
        }
        if (negSign != 0)
            result[0] = '-';
        return result;
    }

	/** 
	 * Pad an integer value with zeros to the given length (high-efficiency).
	 * Use the given buffer at the given start position and return the length appended for convenience.
	 * e.g. <code>int pos = 0; pos += StringUtil.padZeroLeftFast(result,pos,cal.get(Calendar.YEAR),4);</code>
	 */
	public static int padZeroLeftFast(char[] result, int start, long value, int length) {
		int negSign = 0;
		if (value < 0) {
			negSign = 1;
			value = -value;
		}
		// pad up to the given length
		for (int i = length - 1; i >= negSign; i--) {
			if (value > 0) {
				result[start+i] = (char)((long)value % 10 + '0');
				value /= 10;
			}
			else {
				result[start+i] = '0';
			}
		}
		return length;
	}
    
	/** 
	 * Pad an integer value with zeros to the given length, appending to the given StringBuffer (medium-efficiency). 
	 * This is actually slightly slower than returning a char[] to be appended to a StringBuffer.
	 */
	public static void padZeroLeftAppend(StringBuffer result, long value, int length) {
		int negSign = 0;
		if (value < 0) {
			negSign = 1;
			value = -value;
		}
		int resultLength = length + negSign;
		int resultStart = result.length() + negSign;
		result.setLength( resultStart + resultLength );
		
		// 1. pad up to the given length
		for (int i = resultLength - 1; i >= negSign; i--) {
			if (value > 0) {
				result.setCharAt(resultStart + i, (char)((long)value % 10 + '0'));
				value /= 10;
			}
			else {
				result.setCharAt(resultStart + i, '0');
			}
		}

		// 2. if there are still digits remaining, prepend the extra digits
		if (value > 0) {
			char[] result2 = new char[MAX_LONG_DIGITS - length + negSign];
			int i = result2.length;
			while (value > 0) {
				i--;
				result2[i] = (char)((long)value % 10 + '0');
				value /= 10;
			}
			int len2 = result2.length - i;
			result.insert(resultStart,result2,i,len2);
		}
		if (negSign != 0)
			result.setCharAt(resultStart - negSign, '-');
	}

    /**
     * Delete specified characters from the source string.
     *
     * @param str source string
     * @param stripChars characters to be deleted
     * @return string with characters deleted
     */
    public static String stripCharsInString(String str, String stripChars) {
		if (str == null) {
		  return str;
		}
		char[] result = str.toCharArray();
		int n = 0;
		for (int i=0; i<result.length; i++) {
			char c = result[i];
			if (stripChars.indexOf(c) == -1) {
				if (n != i) {
					result[n] = c;
				}
				n++;
			}
		}
		return String.valueOf(result, 0, n);
    }

    /**
     * Only keep certain characters in the source string.
     *
     * @param str source string
     * @param keepChars characters to be kept
     * @return string with characters deleted
     */
    public static String stripCharsNotInString(String str, String keepChars) {
		if (str == null) {
		  return str;
		}
		char[] result = str.toCharArray();
		int n = 0;
		for (int i=0; i<result.length; i++) {
			char c = result[i];
			if (keepChars.indexOf(c) != -1) {
				if (n != i) {
					result[n] = c;
				}
				n++;
			}
		}
		return String.valueOf(result, 0, n);
    }

    /**
     * Stamp/overwrite characters at the start of the string.
     *
     * @param str source string
     * @param stamp character to stamp from start of string
     * @param leaveAtEnd length to leave untouched from end of string
     */
    public static String stampCharStartUntil(String str, char stamp, int leaveAtEnd) {
    	if (str == null) return null;
    	if (leaveAtEnd >= str.length()) return str;
 		char[] result = str.toCharArray();
    	Arrays.fill(result, 0, result.length-leaveAtEnd, stamp);
    	return String.valueOf(result);
    }
	    
	/** 
	 * Returns the position in src of the first search string found, or string length() if not found.
	 */
	public static int indexOfOne(String src, int start, String[] search) {
		int pos = 0;
		int min = src.length();
		for (int i=0; i<search.length; i++) {
			pos = src.indexOf(search[i],start);
			if (pos != -1 && pos < min) {
				min = pos;
			}
		}
		return min;
	} 

	/**
	 * Code shared by String and StringBuffer to do searches. The
	 * source is the character array being searched, and the target
	 * is the string being searched for.
	 *
	 * @param   source       the characters being searched.
	 * @param   sourceOffset offset of the source string.
	 * @param   sourceCount  count of the source string.
	 * @param   target       the characters being searched for.
	 * @param   targetOffset offset of the target string.
	 * @param   targetCount  count of the target string.
	 * @param   fromIndex    the index to begin searching from.
	 */
	public static int indexOf(char[] source, int sourceOffset, int sourceCount,
					   char[] target, int targetOffset, int targetCount,
					   int fromIndex) {
	if (fromIndex >= sourceCount) {
			return (targetCount == 0 ? sourceCount : -1);
	}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
	if (targetCount == 0) {
		return fromIndex;
	}

		char first  = target[targetOffset];
		int i = sourceOffset + fromIndex;
		int max = sourceOffset + (sourceCount - targetCount);

	startSearchForFirstChar:
		while (true) {
		/* Look for first character. */
		while (i <= max && source[i] != first) {
		i++;
		}
		if (i > max) {
		return -1;
		}

		/* Found first character, now look at the rest of v2 */
		int j = i + 1;
		int end = j + targetCount - 1;
		int k = targetOffset + 1;
		while (j < end) {
		if (source[j++] != target[k++]) {
			i++;
			/* Look for str's first char again. */
			continue startSearchForFirstChar;
		}
		}
		return i - sourceOffset;	/* Found whole string. */
		}
	}

	/**
	 * Code shared by String and StringBuffer to do searches. The
	 * source is the character array being searched, and the target
	 * is the string being searched for.
	 *
	 * @param   source       the characters being searched.
	 * @param   sourceOffset offset of the source string.
	 * @param   sourceCount  count of the source string.
	 * @param   target       the characters being searched for.
	 * @param   targetOffset offset of the target string.
	 * @param   targetCount  count of the target string.
	 * @param   fromIndex    the index to begin searching from.
	 */
	public static int lastIndexOf(char[] source, int sourceOffset, int sourceCount,
						   char[] target, int targetOffset, int targetCount,
						   int fromIndex) {
		/*
	 * Check arguments; return immediately where possible. For
	 * consistency, don't check for null str.
	 */
		int rightIndex = sourceCount - targetCount;
	if (fromIndex < 0) {
		return -1;
	}
	if (fromIndex > rightIndex) {
		fromIndex = rightIndex;
	}
	/* Empty string always matches. */
	if (targetCount == 0) {
		return fromIndex;
	}

		int strLastIndex = targetOffset + targetCount - 1;
	char strLastChar = target[strLastIndex];
	int min = sourceOffset + targetCount - 1;
	int i = min + fromIndex;

	startSearchForLastChar:
	while (true) {
		while (i >= min && source[i] != strLastChar) {
		i--;
		}
		if (i < min) {
		return -1;
		}
		int j = i - 1;
		int start = j - (targetCount - 1);
		int k = strLastIndex - 1;

		while (j > start) {
			if (source[j--] != target[k--]) {
			i--;
			continue startSearchForLastChar;
		}
		}
		return start - sourceOffset + 1;
	}
	}
	
	/** 
	 * Returns a array of positions in src of search strings in the order they are found.
	 * This is built for efficiency, remembering where it found things and keeping object creation to a minimum.
	 * 
	 * @param src The string to be searched.
	 * @param search The array of search criteria.
	 * @return Array of positions in src of search strings in the order they are found. 
	 * @see findAll for even more efficiency.
	 */
	public static int[] indexOfAll(String src, String[] search) {
		IntList result = new IntList();
		int[] idxAry = new int[search.length];
		int len = src.length();
		int pos = 0;
		while (pos != -1) {
			int minIdx = -1;
			int minPos = len;
			for (int i=0; i<search.length; i++) {
				pos = idxAry[i];
				if (pos == -1) continue;
				pos = src.indexOf(search[i],pos);
				idxAry[i] = pos;
				if (pos != -1 && pos < minPos) {
					minPos = pos;
					minIdx = i;
				}
			}
			if (minIdx != -1) {
				result.add(minPos);
				pos = minPos + search[minIdx].length();
				if (pos >= len) pos = -1;
				idxAry[minIdx] = pos;
			} else {
				pos = -1;
			}
		}
		return result.toArray();
	} 
	    
	/** 
	 * Sets resultPosAndIndex [0] and [1] with the position in src and search array index of first search string found.
	 */
	public static void findOne(int[] resultPosAndIndex, String src, int start, String[] search) {
		int pos = 0;
		int min = src.length();
		int minIdx = -1;
		for (int i=0; i<search.length; i++) {
			pos = src.indexOf(search[i],start);
			if (pos != -1 && pos < min) {
				min = pos;
				minIdx = i;
			}
		}
		resultPosAndIndex[0] = minIdx;
		resultPosAndIndex[1] = min;
	}
	
	/** 
	 * Returns a list of int[2] posAndIndex with the position and search array index of the string that was found.
	 * This results are ordered by position found.
	 * This is built for efficiency, remembering where it found things and keeping object creation to a minimum.
	 * 
	 * @param src The string to be searched.
	 * @param search The array of search criteria.
	 * @return List of int[2] posAndIndex where posAndIndex[0] is the position within src and posAndIndex[1] is the index of the search string found. 
	 */
	public static List<int[]> findAll(String src, String[] search) {
		List<int[]> result = new ArrayList<int[]>();
		int[] idxAry = new int[search.length];
		int len = src.length();
		int pos = 0;
		while (pos != -1) {
			int minIdx = -1;
			int minPos = len;
			for (int i=0; i<search.length; i++) {
				pos = idxAry[i];
				if (pos == -1) continue;
				pos = src.indexOf(search[i],pos);
				idxAry[i] = pos;
				if (pos != -1 && pos < minPos) {
					minPos = pos;
					minIdx = i;
				}
			}
			if (minIdx != -1) {
				result.add(new int[] {minPos,minIdx});
				pos = minPos + search[minIdx].length();
				if (pos >= len) pos = -1;
				idxAry[minIdx] = pos;
			} else {
				pos = -1;
			}
		}
		return result;
	}

	/**
	 * Search and replace one occurence within the text string.
	 *
     * @param text Source string to be changed.
     * @param search Search string to be found.
     * @param replace Replacement string for search string.
     * @return Resulting string.
	 */
	public static String replaceOne(String text, String search, String replace) {
		int idx = (text != null) ? text.indexOf(search) : -1;
		if (idx != -1) {
			return text.substring(0,idx) + replace + text.substring(idx+search.length());
		} else {
			return text;
		}
	}

    /**
     * Search and replace all occurences of one string with another string.
     * This is optimized to produce only one intermediate working object (StringBufferFast).
     *
	 * For example:
	 * <code><pre>
	 *    replaceAll( "ABC #V# DEF #V#", "#V", "x" );
	 * </pre></code>
	 * leaves s with the value "ABC x DEF x".
	 *
     * @param str Source string to be changed.
     * @param search Search string to be found.
     * @param replace Replacement string for search string.
     * @return Resulting string.
     */
	public static String replaceAll(String str, String search, String replace) {
		int start = 0;
		int end = (str != null) ? str.indexOf(search) : -1;
		if (end == -1) return str;
		StringBuilder buf = new StringBuilder(str.length());
		while (end != -1) {
			buf.append(str, start, end);
			buf.append(replace);
			start = end + search.length();
			end = str.indexOf(search, start);
		}
		if (start != str.length()) {
			buf.append(str, start, str.length());
		}
		return buf.toString();
	}

	/**
	 * Replace special characters in the given String value.
	 * This will only create intermediary objects if necessary.
	 *
	 * @param value Value with characters to be replaced.
	 * @param search Characters to match.
	 * @param replace Replacements for the associated search characters.
	 * @return String with replacements.
	 */
	public static String replaceAll(String value, char[] search, String[] replace) {
		if (value == null || value.length() == 0) return value;
		StringBuilder sb = null;
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			int j = 0;
			do {
				if (c == search[j]) {
					if (sb == null) {
						sb = new StringBuilder(value.length());
						sb.append(value,0,i);
					}
					sb.append(replace[j]);
					break; // break the loop
				}
				j++;
			} while (j < search.length);
			if (!(j < search.length) && sb != null) {
				// the character was not replaced and the result has been initialized
				sb.append(c);
			}
		}
		return (sb != null) ? sb.toString() : value;
	}
	
	/**
	 * Replace special characters in the given String value.
	 * This will only create intermediary objects if necessary.
	 *
	 * @param value Value with characters to be replaced.
	 * @param search Strings to match.
	 * @param replace Replacement characters for the associated search strings.
	 * @return String with replacements.
	 */
	public static String replaceAll(final String value, final String[] search, final char[] replace) {
		if (isEmpty(value) || search == null || replace == null) return value;
		final int oldlen = value.length();
		StringBuilder sb = null;
		int[] pos = new int[search.length];
		for (int j=0; j<search.length; j++) pos[j] = -1;
		for (int pos1 = 0, pos2 = 0; pos1 < oldlen; pos1 = pos2) {
			int repidx = 0;
			boolean found = false;
			for (int j=0; j<search.length; j++) {
				pos2 = pos[j];
				if (pos2 == oldlen) continue;
				if (pos2 == -1 || pos2 < pos1) {
					pos2 = value.indexOf(search[j],pos1);
					if (pos2 == -1) pos2 = oldlen;
					pos[j] = pos2;
				}
				if (pos2 != oldlen) {
					found = true;
					if (pos2 < pos[repidx]) repidx = j;
				}
			}
			if (found) {
				if (sb == null)	sb = new StringBuilder(oldlen);
				pos2 = pos[repidx];
				sb.append(value.substring(pos1,pos2));
				sb.append(replace[repidx]);
				pos2 += search[repidx].length();
			} else {
				if (sb != null) sb.append(value.substring(pos1,oldlen));
				pos2 = oldlen; 
			}
		}
		return (sb != null) ? sb.toString() : value;
	}
	
	public static void replaceAll(StringBuffer result, String value, char[] search, String[] replace) {
		if (value == null || value.length() == 0) return;
		StringBuffer sb = null;
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			int j = 0;
			do {
				if (c == search[j]) {
					if (sb == null) {
						sb = result;
						sb.append(value.substring(0,i));
					}
					sb.append(replace[j]);
					break; // break the loop
				}
				j++;
			} while (j < search.length);
			if (!(j < search.length) && sb != null) {
				// the character was not replaced and the result has been initialized
				sb.append(c);
			}
		}
		if (sb == null) result.append(value);
	}

	public static void replaceAll(StringBuilder result, String value, char[] search, String[] replace) {
		if (value == null || value.length() == 0) return;
		StringBuilder sb = null;
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			int j = 0;
			do {
				if (c == search[j]) {
					if (sb == null) {
						sb = result;
						sb.append(value,0,i);
					}
					sb.append(replace[j]);
					break; // break the loop
				}
				j++;
			} while (j < search.length);
			if (!(j < search.length) && sb != null) {
				// the character was not replaced and the result has been initialized
				sb.append(c);
			}
		}
		if (sb == null) result.append(value);
	}

	/**
	 * Replace search strings in the given string value with replacement strings.
	 * This will only create intermediary objects if necessary and uses an efficient algorithm.
	 * 
	 * The algorithm works as follows: 
	 * (a) search for each search string, remembering their found positions;
	 * (b) replace the search string closest to the current position with its replacement;
	 * (c) increase the current position to the next closest found position;
	 * (d) repeat until no search strings are found from the current position.
	 * 
	 * This algorithm is 2x to 3x faster, or more, than doing multiple searches at one character increments,
	 * based on typical use of a long (dozens to hundres of chars) source string with a few short (word) replacements.
	 *
	 * @param value Value that may have search strings to be replaced.
	 * @param search Strings to find.
	 * @param replace Replacements for the associated search strings.
	 * @return String with replacements.
	 */
	public static String replaceAll(final String value, final String[] search, final String[] replace) {
		if (isEmpty(value) || search == null || replace == null) return value;
		final int oldlen = value.length();
		StringBuilder sb = null;
		int[] pos = new int[search.length];
		for (int j=0; j<search.length; j++) pos[j] = -1;
		for (int pos1 = 0, pos2 = 0; pos1 < oldlen; pos1 = pos2) {
			int repidx = 0;
			boolean found = false;
			for (int j=0; j<search.length; j++) {
				pos2 = pos[j];
				if (pos2 == oldlen) continue;
				if (pos2 == -1 || pos2 < pos1) {
					pos2 = value.indexOf(search[j],pos1);
					if (pos2 == -1) pos2 = oldlen;
					pos[j] = pos2;
				}
				if (pos2 != oldlen) {
					found = true;
					if (pos2 < pos[repidx]) repidx = j;
				}
			}
			if (found) {
				if (sb == null)	sb = new StringBuilder(oldlen);
				pos2 = pos[repidx];
				sb.append(value,pos1,pos2);
				sb.append(replace[repidx]);
				pos2 += search[repidx].length();
			} else {
				if (sb != null) sb.append(value,pos1,oldlen);
				pos2 = oldlen; 
			}
		}
		return (sb != null) ? sb.toString() : value;
	}
	
	public static StringBuffer replaceAll(final StringBuffer value, final String[] search, final String[] replace) {
		if (value == null || search == null || replace == null) return value;
		int[] pos = new int[search.length];
		for (int j=0; j<search.length; j++) pos[j] = -1;
		int oldlen = value.length();
		for (int pos1 = 0, pos2 = 0; pos1 < oldlen; pos1 = pos2) {
			int repidx = 0;
			boolean found = false;
			for (int j=0; j<search.length; j++) {
				pos2 = pos[j];
				if (pos2 == oldlen) continue;
				if (pos2 == -1 || pos2 < pos1) {
					pos2 = value.indexOf(search[j],pos1);
					if (pos2 == -1) pos2 = oldlen;
					pos[j] = pos2;
				}
				if (pos2 != oldlen) {
					found = true;
					if (pos2 < pos[repidx]) repidx = j;
				}
			}
			if (found) {
				pos2 = pos[repidx];
				value.replace(pos1,pos2,replace[repidx]);
				pos2 += replace[repidx].length();
			}
		}
		return value;
	}
	
	public static StringBuilder replaceAll(final StringBuilder value, final String[] search, final String[] replace) {
		if (value == null || search == null || replace == null) return value;
		int[] pos = new int[search.length];
		for (int j=0; j<search.length; j++) pos[j] = -1;
		int oldlen = value.length();
		for (int pos1 = 0, pos2 = 0; pos1 < oldlen; pos1 = pos2) {
			int repidx = 0;
			boolean found = false;
			for (int j=0; j<search.length; j++) {
				pos2 = pos[j];
				if (pos2 == oldlen) continue;
				if (pos2 == -1 || pos2 < pos1) {
					pos2 = value.indexOf(search[j],pos1);
					if (pos2 == -1) pos2 = oldlen;
					pos[j] = pos2;
				}
				if (pos2 != oldlen) {
					found = true;
					if (pos2 < pos[repidx]) repidx = j;
				}
			}
			if (found) {
				pos2 = pos[repidx];
				value.replace(pos1,pos2,replace[repidx]);
				pos2 += replace[repidx].length();
			}
		}
		return value;
	}
	
    /**
     * Replace end-of-line (EOL) with a different string.
     * 
     * @param value To have {"\r","\n","\r\n"} replaced.
     * @param replace Replacement value.
     * @return Same string or new string with values replaced.
     */
    public static String replaceAllEndOfLine(String value, String replace) {
		if (isEmpty(value)) return value;
		StringBuilder result = null;
		boolean skipLF = false;
		for (int i=0; i<value.length(); i++) {
			char c = value.charAt(i);
			if (skipLF && c == '\n') continue;
			skipLF = false;
			if (c == '\r' || c == '\n') {
				if (c == '\r') skipLF = true;
				if (result == null) {
					result = new StringBuilder(value.length());
					result.append(value,0,i);
				}
				result.append(replace);
			 }
			else {
				if (result != null) result.append(c);
			}
		}
		return (result != null) ? result.toString() : value;
    }

	/**
     * Convert an object, if null or empty, to the given default String.
     *
     * @param obj object to be converted
     * @param def default to use if given obj is null or empty
     * @return either the String value of the obj or the def
     */
    public static String toNotEmptyString(Object obj, String def) {
        if (obj == null) {
            return def;
	    } else {
	    	final String s = obj.toString();
            return (s == null || s.length() == 0) ? def : s;
        }
    }

    /**
     * Convert an object, if null or empty after trimming, to the given default String.
     *
     * @param obj object to be converted
     * @param def default to use if given obj is null or empty
     * @return either the String value of the obj or the def
     */
    public static String toNotEmptyStringTrim(Object obj, String def) {
        if (obj == null) {
            return def;
	    } else {
	    	String s = obj.toString();
	    	if (s != null) s = s.trim();
            return (s == null || s.length() == 0) ? def : s;
        }
    }

	/**
	 * Convert an object to a nullable String, converting an emtpy string to null,
	 * i.e. a null object or empty string gives a null String, not 4-letters "null".
	 *
	 * @param obj object to be converted
	 * @return result of <code>obj.toString()</code> or <code>null</code> if <code>obj</code> was null
	 */
	public static String toNullableString(Object obj) {
		return toNotEmptyString(obj, null);
	}

	/**
	 * Convert an object to a nullable String, converting an emtpy string to null,
	 * i.e. a null object or empty string gives a null String, not 4-letters "null".
	 *
	 * @param obj object to be converted
	 * @return result of <code>obj.toString()</code> or <code>null</code> if <code>obj</code> was null
	 */
    public static String toNullableStringTrim(Object obj) {
    	return toNotEmptyStringTrim(obj, null);
    }
    
    /**
     * Convert an object to a non-null String.
     *
     * @param obj object to be converted
     * @return result of <code>obj.toString()</code> or <code>""</code> if <code>obj</code> was null
     */
    public static String toNotNullString(Object obj) {
    	if (obj == null) {
    		return EMPTY_STRING;
    	}
    	else {
    		final String str = obj.toString();
    		return (str == null) ? EMPTY_STRING : str;
		}
    }

    /**
     * Convert an object to a non-null String, trimmed of leading and trailing spaces.
     *
     * @param obj object to be converted
     * @return result of <code>obj.toString()</code> or <code>""</code> if <code>obj</code> was null
     */
    public static String toNotNullStringTrim(Object obj) {
		final String str = toNotNullString(obj);
		return (str.length() == 0) ? str : str.trim();
    }

    /**
	 * Convert the String list to an array of strings using the delimiters, only with non-empty values.
	 * For example splitting "x, ,y" by "," returns {"x","y"} not {"x","","y"}.
	 *
	 * @param list the String list that will be converted
	 * @return the result
	 */
	public static String[] splitArrayTokensNotEmpty(String list, String delim) {
		if (list == null) return null;
		String[] items = null;
    	StringTokenizer toker = new StringTokenizer(list, delim);
    	int n = toker.countTokens();
    	if (n > 0) {
			items = new String[n];
			for (int i=0; toker.hasMoreElements(); i++) {
				String item = toker.nextToken();
				items[i] = item.trim();
			}
    	}
    	return items;
    }

    /**
     * Converts list to an array of strings using the delimiter.
	 * For example splitting "x,,y" by "," returns {"x","y"} not {"x","","y"}.
     *
     * @param list the String list that will be converted
     * @return the result
     */
    public static String[] splitArrayTokensNotEmptyNoTrim(String list, String delim) {
        if (list == null) return null;
        String[] items = null;
        StringTokenizer toker = new StringTokenizer(list, delim);
        int n = toker.countTokens();
        if (n > 0) {
            items = new String[n];
            for (int i=0; toker.hasMoreElements(); i++) {
                String item = toker.nextToken();
                items[i] = item;
            }
        }
        return items;
    }

    /**
	 * Converts list to an array of strings using the delimiter.
	 *
	 * @param list the String list that will be converted
	 * @return the result
	 */
	public static String[] splitArray(String list, String delim) {
		if (list == null) return null;
		int n = count(list,delim) + 1;
		String[] items = new String[n];
		int end = list.length();
		for (int i = list.lastIndexOf(delim,end); i != -1; i = list.lastIndexOf(delim,i)) {
			items[--n] = list.substring(i+1,end).trim();
			end = i;
			i--;
		}
		items[--n] = list.substring(0,end);
    	return items;
    }

    /**
     * Converts list to an array of strings using the delimiter.
     *
     * @param list the String list that will be converted
     * @return the result
     */
    public static String[] splitArrayNoTrim(String list, String delim) {
        if (list == null) return null;
        int n = count(list,delim) + 1;
		String[] items = new String[n];
		int end = list.length();
		for (int i = list.lastIndexOf(delim,end); i != -1; i = list.lastIndexOf(delim,i)) {
			items[--n] = list.substring(i+1,end);
			end = i;
			i--;
		}
		items[--n] = list.substring(0,end);
        return items;
    }

    /**
     * Split an array based on a delimiter character,
     * trimming leading/trailing space from each value.
     *
     * @param list  The String list to be split.
     * @param delim  The delimiter separating values in the list.
     * @return  The array of separate list values.
     */
	public static String[] splitArray(String list, char delim) {
		if (list == null) return null;
		int n = count(list,delim) + 1;
		String[] items = new String[n];
		int end = list.length();
		for (int i = list.lastIndexOf(delim,end); i != -1; i = list.lastIndexOf(delim,i)) {
			items[--n] = list.substring(i+1,end).trim();
			end = i;
			i--;
		}
		items[--n] = list.substring(0,end);
		return items;
	}

    /**
     * Split an array based on a delimiter character
     * without trimming leading/trailing spaces from each value.
     *
     * @param list  The String list to be split.
     * @param delim  The delimiter separating values in the list.
     * @return  The array of separate list values.
     */
	public static String[] splitArrayNoTrim(String list, char delim) {
		if (list == null) return null;
		int n = count(list,delim) + 1;
		String[] items = new String[n];
		int end = list.length();
		for (int i = list.lastIndexOf(delim,end); i != -1; i = list.lastIndexOf(delim,i)) {
			items[--n] = list.substring(i+1,end);
			end = i;
			i--;
		}
		items[--n] = list.substring(0,end);
		return items;
	}

	/**
	 * Split a CSV (comma separated value) string by the given character,
	 * handling quoted commas appropriately.
	 * <code>1,Jane Smith,"Toronto, Ontario, Canada"</code> 
	 * @see http://en.wikipedia.org/wiki/Comma-separated_values
	 */
    public static final List<String> splitCsv(final String str, final char chr) {
    	final ArrayList<String> list = new ArrayList<String>();
    	for (int pos1 = 0, posC = 0, posQ = 0; pos1 >= 0;) {
    		if (posC != -1) posC = str.indexOf(chr, pos1);
    		if (posQ != -1) posQ = str.indexOf('\"', pos1);
    		if (posQ != -1 && posQ < posC) {
    			//System.out.println("quote "+ posQ);
    			pos1 = posQ+1;
    			posQ = str.indexOf('\"', pos1);
    			int dq = 0;
    			while (posQ != -1 && posQ+1 < str.length() && str.charAt(posQ+1) == '\"') {
    				dq++;
    				posQ = posQ+2 < str.length() ? str.indexOf('\"', posQ+2) : -1;
    			}
    			String val = posQ != -1 ? str.substring(pos1, posQ) : str.substring(pos1);
    			if (dq != 0) val = replaceAll(val, "\"\"", "\"");
    			list.add(val);
    			if (posQ != -1  && posQ+1 < str.length()) posC = str.indexOf(chr, posQ+1);
    			else posC = -1;
    		} else {
    			//System.out.println("comma "+ posC);
    			list.add(posC != -1 ? str.substring(pos1, posC) : str.substring(pos1));
    		}
    		pos1 = posC != -1 ? posC+1 : -1;
    	}
        return list;
    }
    
    /**
	 * Convert items (objects) to list by using delimiter.
	 *
	 * @param items the objects that will be converted to list
	 * @return the result
	 */
	public static String joinString(Object[] items, char delim) {
    	if (items == null || items.length == 0) {
    		return null;
    	}
    	StringBuilder list = new StringBuilder(String.valueOf(items[0]));
    	for (int i=1; i < items.length; i++) {
			list.append(delim);
			list.append(String.valueOf(items[i]));
    	}
    	return list.toString();
    }

    /**
	 * Convert items (objects) to list by using delimiter.
	 *
	 * @param items the objects that will be converted to list
	 * @return the result
	 */
	public static String joinString(Object[] items, String delim) {
    	if (items == null || items.length == 0) {
    		return null;
    	}
    	StringBuilder list = new StringBuilder(String.valueOf(items[0]));
    	for (int i=1; i < items.length; i++) {
			list.append(delim);
			list.append(String.valueOf(items[i]));
    	}
    	return list.toString();
    }

    /**
	 * Convert items (objects) to list by using delimiter.
	 *
	 * @param items the objects that will be converted to list
	 * @return the result
	 */
	public static String joinStringNotNull(Object[] items, char delim) {
    	if (items == null || items.length == 0) {
    		return EMPTY_STRING;
    	}
    	StringBuilder list = new StringBuilder(toNotNullString(items[0]));
    	for (int i=1; i < items.length; i++) {
			list.append(delim);
			list.append(toNotNullString(items[i]));
    	}
    	return list.toString();
    }

    /**
	 * Convert items (objects) to list by using delimiter.
	 *
	 * @param items the objects that will be converted to list
	 * @return the result
	 */
	public static String joinStringNotNull(Object[] items, String delim) {
    	if (items == null || items.length == 0) {
    		return EMPTY_STRING;
    	}
    	StringBuilder list = new StringBuilder(toNotNullString(items[0]));
    	for (int i=1; i < items.length; i++) {
			list.append(delim);
			list.append(toNotNullString(items[i]));
    	}
    	return list.toString();
    }

	/**
	 * Convert first character of the string to uppercase.
	 */
	public static String firstCharToUpperCase(String str) {
	  if (str == null) {
		  return null;
	  }
	  StringBuilder buf = new StringBuilder(str);
	  buf.setCharAt(0, Character.toUpperCase(buf.charAt(0)));
	  return buf.toString();
	}

	/**
	 * Convert first character of the string to uppercase and the rest to lowercase.
	 */
	public static String firstUpperThenLower(String str) {
	  if (str == null) {
		  return null;
	  }
	  StringBuilder buf = new StringBuilder(str.toLowerCase());
	  buf.setCharAt(0, Character.toUpperCase(buf.charAt(0)));
	  return buf.toString();
	}

	/**
	 * Create a new array of characters with the given count of filler character.
	 */
	public static char[] repeatChars(char fillChar, int count) {
		char[] result = new char[count];
		Arrays.fill(result, fillChar);
		return result;
	}

	/**
	 * Create a new array of characters with the given count of filler and interspersed separator characters.
	 * e.g. returns char[] with "?,?,?" when called with ('?',',',3)
	 */
	public static char[] repeatChars(char fillChar, char separateChar, int count) {
		char[] result = new char[count * 2 - 1];
		result[0] = fillChar;
		for (int i=1; i<result.length; i+=2) {
			result[i] = separateChar;
			result[i+1] = fillChar;
		}
		return result;
	}

	/**
	 * Create a String with the given count of filler character.
	 */
	public static String repeat(char fillChar, int count) {
		return new String(repeatChars(fillChar, count));
	}

	/**
	 * Create a String with the given count of filler and interspersed separator characters.
	 * e.g. returns char[] with "?,?,?" when called with ('?',',',3)
	 */
	public static String repeat(char fillChar, char separateChar, int count) {
		return new String(repeatChars(fillChar, separateChar, count));
	}

}
