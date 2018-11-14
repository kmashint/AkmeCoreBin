package akme.core.util;



/**
 * Utility class for manipulating Number values.
 *
 * @author Copyright(c) 2003 AKME Solutions
 * @author $Author: keith.mashinter $
 * @version $Date: 2007/08/14 18:14:33 $
 * $NoKeywords: $
 */
public abstract class NumberUtil {

	public static final Byte ZERO_BYTE = Byte.valueOf((byte)0);
	public static final Short ZERO_SHORT = Short.valueOf((short)0);
	public static final Integer ZERO_INTEGER = Integer.valueOf((int)0);
	public static final Long ZERO_LONG = Long.valueOf((long)0);
	public static final Float ZERO_FLOAT = Float.valueOf((float)0);
	public static final Double ZERO_DOUBLE = Double.valueOf((double)0);

	/** String of digits. */
	public static final String DIGITS = "0123456789";

    /**
     * Convert a String to a Double while suppressing the exception.
     * @param str string to be converted without throwing an exception
     * @return value of the string, otherwise null if there was an exception
     */
    public static Double toDouble(String str) {
        try {
            return (str != null) ? Double.valueOf(str.trim()) : null;
        }
        catch (NumberFormatException ex) {
            return null;
        }
    }

	/**
	 * Convert a String to a Double while suppressing the exception.
	 * @param str string to be converted without throwing an exception
	 * @param def default to use in case of null or exception
	 * @return value of the string, otherwise def
	 */
	public static Double toDouble(String str, Double def) {
		try {
			return (str != null) ? Double.valueOf(str.trim()) : def;
		}
		catch (NumberFormatException ex) {
			return def;
		}
	}

	public static Double toDouble(Number num, Double def) {
		try {
			return (num != null) ? Double.valueOf(num.doubleValue()) : def;
		}
		catch (NumberFormatException ex) {
			return def;
		}
	}
	
	/**
	 * Convert a String to a primitive double while suppressing the exception.
	 * @param str string to be converted without throwing an exception
	 * @param def default to use in case of null or exception
	 * @return value of the string, otherwise def
	 */
	public static double toDoublePrimitive(String str, double def) {
		try {
			return (str != null) ? Double.valueOf(str.trim()).doubleValue() : def;
		}
		catch (NumberFormatException ex) {
			return def;
		}
	}

	/**
	 * Convert a String to a Float while suppressing the exception.
	 * @param str string to be converted without throwing an exception
	 * @return value of the string, otherwise null if there was an exception
	 */
	public static Float toFloat(String str) {
		try {
			return (str != null) ? Float.valueOf(str.trim()) : null;
		}
		catch (NumberFormatException ex) {
			return null;
		}
	}

	/**
	 * Convert a String to a Float while suppressing the exception.
	 * @param str string to be converted without throwing an exception
	 * @param def default to use in case of null or exception
	 * @return value of the string, otherwise def
	 */
	public static Float toFloat(String str, Float def) {
		Float result = toFloat(str);
		return (result != null) ? result : def;
	}

	public static Float toFloat(Number num, Float def) {
		try {
			return (num != null) ? Float.valueOf(num.floatValue()) : def;
		}
		catch (NumberFormatException ex) {
			return def;
		}
	}
	
	/**
	 * Convert a String to a primitive float while suppressing the exception.
	 * @param str string to be converted without throwing an exception
	 * @param def default to use in case of null or exception
	 * @return value of the string, otherwise null if there was an exception
	 */
	public static float toFloatPrimitive(String str, float def) {
		Float result = toFloat(str);
		return (result != null) ? result.floatValue() : def;
	}

    /**
     * Convert a String to a Long while suppressing the exception.
     * @param str string to be converted without throwing an exception
     * @return value of the string, otherwise null if there was an exception
     */
    public static Long toLong(String str) {
        try {
            return (str != null) ? Long.valueOf(str.trim()) : null;
        }
        catch (NumberFormatException ex) {
            return null;
        }
    }

	/**
	 * Convert a String to a Long while suppressing the exception.
	 * @param str string to be converted without throwing an exception
	 * @param def default to use in case of null or exception
	 * @return value of the string, otherwise def
	 */
	public static Long toLong(String str, Long def) {
		Long result = toLong(str);
		return (result != null) ? result : def;
	}

	public static Long toLong(Number num, Long def) {
		try {
			return (num != null) ? Long.valueOf(num.longValue()) : def;
		}
		catch (NumberFormatException ex) {
			return def;
		}
	}
	
	/**
	 * Convert a String to a primitive long while suppressing the exception.
	 * @param str string to be converted without throwing an exception
	 * @param def default to use in case of null or exception
	 * @return value of the string, otherwise def
	 */
	public static long toLongPrimitive(String str, long def) {
		try {
			return (str != null) ? Long.parseLong(str.trim()) : def;
		}
		catch (NumberFormatException ex) {
			return def;
		}
	}

    /**
     * Convert a String to a Integer while suppressing the exception.
     * @param str string to be converted without throwing an exception
     * @return value of the string, otherwise null if there was an exception
     */
    public static Integer toInteger(String str) {
        try {
            return (str != null) ? Integer.valueOf(str.trim()) : null;
        }
        catch (NumberFormatException ex) {
            return null;
        }
    }

	/**
	 * Convert a String to a Integer while suppressing the exception.
	 * @param str string to be converted without throwing an exception
	 * @param def Default value if it cannot be converted.
	 * @return value of the string, otherwise def
	 */
	public static Integer toInteger(String str, Integer def) {
		try {
			return (str != null) ? Integer.valueOf(str.trim()) : def;
		}
		catch (NumberFormatException ex) {
			return def;
		}
	}

	public static Integer toInteger(Number num, Integer def) {
		try {
			return (num != null) ? Integer.valueOf(num.intValue()) : def;
		}
		catch (NumberFormatException ex) {
			return def;
		}
	}

	/**
	 * Convert a String to a primitive int while suppressing the exception.
	 * @param str String to be converted
	 * @param def Default value if it cannot be converted.
	 * @return value of the string, otherwise null if there was an exception
     */
	public static int toIntPrimitive(String str, int def) {
		try {
			return (str != null) ? Integer.parseInt(str.trim()) : def;
		}
		catch (NumberFormatException ex) {
			return def;
		}
	}

	/**
	 * Convert a String to a Short while suppressing the exception.
	 * @param str string to be converted without throwing an exception
	 * @param def default to use in case of null or exception
	 * @return value of the string, otherwise def
	 */
	public static Short toShort(String str, Short def) {
		try {
			return (str != null) ? Short.valueOf(str.trim()) : def;
		}
		catch (NumberFormatException ex) {
			return def;
		}
	}

	/**
	 * Convert a String to a primitive int while suppressing the exception.
	 * @param str String to be converted
	 * @param def Default value if it cannot be converted.
	 * @return value of the string, otherwise null if there was an exception
	 */
	public static short toShortPrimitive(String str, short def) {
		try {
			return (str != null) ? Short.parseShort(str.trim()) : def;
		}
		catch (NumberFormatException ex) {
			return def;
		}
	}

	public static Short toShort(Number num, Short def) {
		try {
			return (num != null) ? Short.valueOf(num.shortValue()) : def;
		}
		catch (NumberFormatException ex) {
			return def;
		}
	}
	
	/**
	 * Convert a String to a Byte while suppressing the exception.
	 * @param str string to be converted without throwing an exception
	 * @return value of the string, otherwise null if there was an exception
	 */
	public static Byte toByte(String str) {
		try {
			return (str != null) ? Byte.valueOf(str.trim()) : null;
		}
		catch (NumberFormatException ex) {
			return null;
		}
	}

	public static Byte toByte(Number num, Byte def) {
		try {
			return (num != null) ? Byte.valueOf(num.byteValue()) : def;
		}
		catch (NumberFormatException ex) {
			return def;
		}
	}
	
	/**
	 * Convert a String to a primitive int while suppressing the exception.
	 * @param str String to be converted
	 * @param def Default value if it cannot be converted.
	 * @return value of the string, otherwise null if there was an exception
	 */
	public static byte toBytePrimitive(String str, byte def) {
		try {
			return (str != null) ? Byte.parseByte(str.trim()) : def;
		}
		catch (NumberFormatException ex) {
			return def;
		}
	}

	/**
	 * Round (0.5 rounds up) to the nearest precision specified.
	 * <br>e.g. <code>round( 123.45, 0.1  )<code> will return 123.5.
	 * <br>e.g. <code>round( 12.345, 0.01 )<code> will return 12.35.
	 *
	 * @param number The number to be rounded.
	 * @param ratio The ratio to use in rounding to the nearest precision.
	 * @return The rounded number.
	 */
	public static double round( double number, double precision ) {
		if (precision == 0.0) {
			return Math.floor( number * 10d + 0.5d ) / 10d;
		}
		else {
			return Math.floor( number * 10d / precision + 0.5d) * precision / 10d;
		}
	}

	/**
	 * @see round( double, double )
	 */
	public static Double round( Double number, double precision ) {
		return new Double(round( number.doubleValue(), precision ) );
	}

	/**
	 * Round (0.5 rounds up) to two (2) decimal places.
	 * <br>e.g. <code>round( 12.345 )<code> will return 12.35.
	 *
	 * @param number The number to be rounded.
	 * @return The rounded number.
	 */
	public static double round2( double number ) {
		return Math.floor( number * 1000.0d + 0.5d ) / 1000.0d;
	}

	/**
	 * @see round2( double )
	 */
	public static Double round2( Double number ) {
		return new Double(round2( number.doubleValue() ) );
	}

	/**
	 * Round a value using long modulo arithmetic while converting to a string.
	 * This should only be used for values which won't overflow a long when shifted by the number of decimals.
	 * 
	 * @param value
	 * @param decimals Number of decimals place to round
	 * @return String value rounding to the given number of decimals.
	 */
	public static String roundToStringFast(double value, int decimals) {
		if (decimals >= StringUtil.MAX_LONG_DIGITS) {
			throw new IllegalArgumentException("decimals must be less than "+ StringUtil.MAX_LONG_DIGITS +" but was given as "+ decimals);
		}
	    int precision = 1;
	    for (int i=0; i<decimals; i++) precision *= 10;
	    long x = (long) (Math.floor(value * precision + 0.5d)); 
	    if (decimals == 0) {
	    	return String.valueOf(x);
	    } else {
	    	final char[] result = new char[StringUtil.MAX_LONG_DIGITS];
	    	int pos = 0;
	    	final String x1 = Long.toString(x / precision);
	    	pos = x1.length();
	    	x1.getChars(0, pos, result, 0);
	    	result[pos++] = '.';
	    	pos += StringUtil.padZeroLeftFastMaxLength(result, pos, (x&0x7fffffffffffffffL) % precision, decimals);
	    	return String.valueOf(result, 0, pos);
	    }
	}

}