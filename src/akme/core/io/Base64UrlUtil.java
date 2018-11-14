package akme.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;

/**
 * Support for URL-friendy Base64 encoding/decoding, RFC 4648.
 */
public abstract class Base64UrlUtil {

	/**
	 * This array is a lookup table that translates 6-bit positive integer
	 * index values into their "Base64 Alphabet" equivalents as specified 
	 * in Table 1 of RFC 4648.
	 */
	static final byte intToBase64[] = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 
		'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 
		'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 
		'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
	};

	/**
	 * This array is a lookup table that translates unicode characters
	 * drawn from the "Base64 Alphabet" (as specified in Table 1 of RFC 4648)
	 * into their 6-bit positive integer equivalents.  Characters that
	 * are not in the Base64 alphabet but fall within the bounds of the
	 * array are translated to -1.
	 */
	static final byte base64ToInt[] = {
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, //  0 - 15
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 16 - 31
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, // 32 - 47
		52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, // 48 - 63
		-1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, // 64 - 79
		15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, // 80 - 95
		-1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, // 96 - 111
		41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 // 112 - 122
	};

	/**
	 * Translates the specified character, which is assumed to be in the
	 * "Base 64 Alphabet" into its equivalent 6-bit positive integer.
	 *
	 * @throw IllegalArgumentException or ArrayOutOfBoundsException if
	 *        c is not in the Base64 Alphabet.
	 */
	static int base64toInt(char c, byte[] alphaToInt) {
		final int result = alphaToInt[c];
		if (result < 0)
			throw new IllegalArgumentException("Illegal character (int)" + ((int)c));
		return result;
	}

	/**
	 * Translates the specified byte array into a Base64 string.
	 */
	public static String encodeBase64(byte[] a, int len) {
		final int resultLen = 4*((len + 2)/3);
		final int numBytesInPartialGroup = len % 3;
//System.err.println(resultLen + " : "+ numBytesInPartialGroup);
		final byte[] resultAry = new byte[resultLen - (numBytesInPartialGroup != 0 ? 3 - numBytesInPartialGroup : 0)];
		ByteBuffer result = ByteBuffer.wrap(resultAry);
		try { encodeBase64(result, a, len); }
		catch (IOException ex) { ex.printStackTrace(); }
		return new String(resultAry);
	}
	
	public static String encodeBase64(byte[] a) {
		return encodeBase64(a, a.length);
	}

	public static void encodeBase64(ByteBuffer result, byte[] a, int len) throws IOException {
		final int aLen = len;
		final int numFullGroups = aLen/3;
		final int numBytesInPartialGroup = aLen - 3*numFullGroups;
		//int resultLen = 4*((aLen + 2)/3);
		final byte[] intToAlpha = intToBase64;

		// Translate all full groups from byte array elements to Base64
		int inCursor = 0;
		for (int i=0; i<numFullGroups; i++) {
			int byte0 = a[inCursor++] & 0xff;
			int byte1 = a[inCursor++] & 0xff;
			int byte2 = a[inCursor++] & 0xff;
			result.put(intToAlpha[byte0 >> 2]);
			result.put(intToAlpha[(byte0 << 4)&0x3f | (byte1 >> 4)]);
			result.put(intToAlpha[(byte1 << 2)&0x3f | (byte2 >> 6)]);
			result.put(intToAlpha[byte2 & 0x3f]);
		}

		// Translate partial group if present
		if (numBytesInPartialGroup != 0) {
			int byte0 = a[inCursor++] & 0xff;
			result.put(intToAlpha[byte0 >> 2]);
			if (numBytesInPartialGroup == 1) {
				result.put(intToAlpha[(byte0 << 4) & 0x3f]);
				//result.put((byte)'='); // not in URL-friendly RFC 4648 
				//result.put((byte)'='); // not in URL-friendly RFC 4648 
			} else {
				// assert numBytesInPartialGroup == 2;
				int byte1 = a[inCursor++] & 0xff;
				result.put(intToAlpha[(byte0 << 4)&0x3f | (byte1 >> 4)]);
				result.put(intToAlpha[(byte1 << 2)&0x3f]);
				//result.put((byte)'='); // not in URL-friendly RFC 4648 
			}
		}
		// assert inCursor == a.length;
		// assert result.length() == resultLen;
	}

	/**
	 * Translates the specified byte array into a Base64 string.
	 */
	public static void encodeBase64(OutputStream result, byte[] a, int len) throws IOException {
		final int aLen = len;
		final int numFullGroups = aLen/3;
		final int numBytesInPartialGroup = aLen - 3*numFullGroups;
		//int resultLen = 4*((aLen + 2)/3);
		final byte[] intToAlpha = intToBase64;

		// Translate all full groups from byte array elements to Base64
		int inCursor = 0;
		for (int i=0; i<numFullGroups; i++) {
			int byte0 = a[inCursor++] & 0xff;
			int byte1 = a[inCursor++] & 0xff;
			int byte2 = a[inCursor++] & 0xff;
			result.write(intToAlpha[byte0 >> 2]);
			result.write(intToAlpha[(byte0 << 4)&0x3f | (byte1 >> 4)]);
			result.write(intToAlpha[(byte1 << 2)&0x3f | (byte2 >> 6)]);
			result.write(intToAlpha[byte2 & 0x3f]);
		}

		// Translate partial group if present
		if (numBytesInPartialGroup != 0) {
			int byte0 = a[inCursor++] & 0xff;
			result.write(intToAlpha[byte0 >> 2]);
			if (numBytesInPartialGroup == 1) {
				result.write(intToAlpha[(byte0 << 4) & 0x3f]);
				// result.write('='); // not in URL-friendly RFC 4648 
				// result.write('='); // not in URL-friendly RFC 4648 
			} else {
				// assert numBytesInPartialGroup == 2;
				int byte1 = a[inCursor++] & 0xff;
				result.write(intToAlpha[(byte0 << 4)&0x3f | (byte1 >> 4)]);
				result.write(intToAlpha[(byte1 << 2)&0x3f]);
				// result.write('='); // not in URL-friendly RFC 4648 
			}
		}
		// assert inCursor == a.length;
		// assert result.length() == resultLen;
	}

	/**
	 * Translates the specified byte array into a Base64 string.
	 */
	public static void encodeBase64(Writer result, byte[] a, int len) throws IOException {
		final int aLen = len;
		final int numFullGroups = aLen/3;
		final int numBytesInPartialGroup = aLen - 3*numFullGroups;
		//int resultLen = 4*((aLen + 2)/3);
		final byte[] intToAlpha = intToBase64;

		// Translate all full groups from byte array elements to Base64
		int inCursor = 0;
		for (int i=0; i<numFullGroups; i++) {
			int byte0 = a[inCursor++] & 0xff;
			int byte1 = a[inCursor++] & 0xff;
			int byte2 = a[inCursor++] & 0xff;
			result.write(intToAlpha[byte0 >> 2]);
			result.write(intToAlpha[(byte0 << 4)&0x3f | (byte1 >> 4)]);
			result.write(intToAlpha[(byte1 << 2)&0x3f | (byte2 >> 6)]);
			result.write(intToAlpha[byte2 & 0x3f]);
		}

		// Translate partial group if present
		if (numBytesInPartialGroup != 0) {
			int byte0 = a[inCursor++] & 0xff;
			result.write(intToAlpha[byte0 >> 2]);
			if (numBytesInPartialGroup == 1) {
				result.write(intToAlpha[(byte0 << 4) & 0x3f]);
				// result.write('='); // not in URL-friendly RFC 4648 
				// result.write('='); // not in URL-friendly RFC 4648 
			} else {
				// assert numBytesInPartialGroup == 2;
				int byte1 = a[inCursor++] & 0xff;
				result.write(intToAlpha[(byte0 << 4)&0x3f | (byte1 >> 4)]);
				result.write(intToAlpha[(byte1 << 2)&0x3f]);
				// result.write('='); // not in URL-friendly RFC 4648 
			}
		}
		// assert inCursor == a.length;
		// assert result.length() == resultLen;
	}

	/**
	 * Translates the specified Base64 string into a byte array.
	 * 
	 * @throw IllegalArgumentException if <tt>s</tt> is not a valid Base64
	 *        string.
	 */
	public static byte[] decodeBase64(String s) {
		final byte[] alphaToInt = base64ToInt;
		final int sLen = s.length();
		final int numGroups = sLen/4;
		int partialLen = sLen%4;
		if (partialLen != 0) partialLen--;
		int numFullGroups = numGroups;
		final byte[] result = new byte[3*numGroups + partialLen];

		// Translate all full groups from base64 to byte array elements
		int inCursor = 0, outCursor = 0;
		for (int i=0; i<numFullGroups; i++) {
			int ch0 = base64toInt(s.charAt(inCursor++), alphaToInt);
			int ch1 = base64toInt(s.charAt(inCursor++), alphaToInt);
			int ch2 = base64toInt(s.charAt(inCursor++), alphaToInt);
			int ch3 = base64toInt(s.charAt(inCursor++), alphaToInt);
			result[outCursor++] = (byte) ((ch0 << 2) | (ch1 >> 4));
			result[outCursor++] = (byte) ((ch1 << 4) | (ch2 >> 2));
			result[outCursor++] = (byte) ((ch2 << 6) | ch3);
		}

		// Translate partial group, if present
		if (partialLen != 0) {
			int ch0 = base64toInt(s.charAt(inCursor++), alphaToInt);
			int ch1 = base64toInt(s.charAt(inCursor++), alphaToInt);
			result[outCursor++] = (byte) ((ch0 << 2) | (ch1 >> 4));

			if (partialLen == 2) {
				int ch2 = base64toInt(s.charAt(inCursor++), alphaToInt);
				result[outCursor++] = (byte) ((ch1 << 4) | (ch2 >> 2));
			}
		}
		// assert inCursor == s.length()-missingBytesInLastGroup;
		// assert outCursor == result.length;
		return result;
	}

	/**
	 * Translates the specified Base64 ASCII InputStream into an OutputStream of bytes,
	 * outputing at most outLen bytes.  If outLen is zero then it converts the entire stream.
	 * Returns the actual number of resulting bytes decoded/written or -1 for end of stream.
	 * Using byte streams is faster than character streams.
	 * 
	 */
	public static int decodeBase64(OutputStream result, InputStream ins, int outLen) throws IOException {
		final byte[] bbuf = new byte[4];
		final int len = outLen - 3 + 1; // We write 3 at a time so make sure we don't overflow.
		int c;
		int i = 0;
		while ((c = ins.read()) != -1 && (outLen == 0 || i < len)) {
			// Continue past any whitespace.
			if (Character.isWhitespace((char) c)) continue;
			bbuf[0] = (byte) c;
			c = ins.read(bbuf, 1, 3);
			//if (c < 3) throw new IOException("Incomplete Base64 bundle, read only "+ (c+1) +" out of 4.");
			int ch0 = base64ToInt[bbuf[0]];
			int ch1 = base64ToInt[bbuf[1]];
			int ch2 = base64ToInt[bbuf[2]];
			int ch3 = base64ToInt[bbuf[3]];			
			result.write((byte) ((ch0 << 2) | (ch1 >> 4)));
			i++;
			// These last two may be '=', translating to -1, to indicate a partial group. 
			if (ch2 != -1) {
				result.write((byte) ((ch1 << 4) | (ch2 >> 2)));
				i++;
			}
			if (ch3 != -1) {
				result.write((byte) ((ch2 << 6) | ch3));
				i++;
			}
		}
		return (c != -1) ? i : c; 
	}
	
	public static int decodeBase64(OutputStream result, InputStream ins) throws IOException {
		return decodeBase64(result, ins, 0);
	}

	/**
	 * Translates the specified Base64 Reader into an OutputStream of bytes,
	 * outputing at most outLen bytes.  If outLen is zero then it converts the entire stream.
	 * Returns the actual number of resulting bytes decoded/written or -1 for end of stream.
	 * Using byte streams is faster than character streams.
	 */
	public static int decodeBase64(OutputStream result, Reader ins, int outLen) throws IOException {
		final char[] cbuf = new char[4];
		final int len = outLen - 3 + 1; // We write 3 at a time so make sure we don't overflow.
		int c;
		int i = 0;
		while ((c = ins.read()) != -1 && (outLen == 0 || i < len)) {
			// Continue past any whitespace.
			if (Character.isWhitespace((char) c)) continue;
			cbuf[0] = (char) c;
			c = ins.read(cbuf, 1, 3);
			//if (c < 3) throw new IOException("Incomplete Base64 bundle, read only "+ (c+1) +" out of 4.");
			int ch0 = base64ToInt[cbuf[0]];
			int ch1 = base64ToInt[cbuf[1]];
			int ch2 = base64ToInt[cbuf[2]];
			int ch3 = base64ToInt[cbuf[3]];			
			result.write((byte) ((ch0 << 2) | (ch1 >> 4)));
			i++;
			// These last two may be '=', translating to -1, to indicate a partial group. 
			if (ch2 != -1) {
				result.write((byte) ((ch1 << 4) | (ch2 >> 2)));
				i++;
			}
			if (ch3 != -1) {
				result.write((byte) ((ch2 << 6) | ch3));
				i++;
			}
		}
		return (c != -1) ? i : c; 
	}
	
	public static int decodeBase64(OutputStream result, Reader ins) throws IOException {
		return decodeBase64(result, ins, 0);
	}

}