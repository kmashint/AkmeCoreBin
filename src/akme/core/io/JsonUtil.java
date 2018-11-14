package akme.core.io;

import java.util.Arrays;

import akme.core.util.StringUtil;

/**
 * Utility methods and values for JSON, Javascript Object Notation.
 * JSON allows, but does not require, the escaping of forward slash.
 * Encoding a forward slash helps avoid "&lt;/" to mistakenly end a &lt;script>...&lt;/script>.
 * Really, though, it's best to wrap in CDATA: &lt;script>&lt;![CDATA[...]]&gt;&lt;/script>.
 * 
 * @see http://tools.ietf.org/html/rfc4627
 * @see http://en.wikipedia.org/wiki/JSON
 * @author <br/> Original code by AKME Solutions.
 * $NoKeywords: $
 */
public abstract class JsonUtil {
	
	static final String NULL = "null";
	static final String TRUE = "true";
	static final String FALSE = "false";
	
	static final String[] KEYWORDS = {NULL, FALSE, TRUE};

	static final String NON_NAME = ":,{}[]\\\" \t\r\n\f\b";
	static final String NON_VALUE = ":,{}[] \t\r\n\f\b";
	static final String NON_STARTER = ":,{}[]";

	static final String ESCAPE_STR = "\\\"trnfb/";
	static final String REPLACE_STR = "\\\"\t\r\n\f\b/";
	
	static final char[] ESCAPE_CHARS = {'\\', '\"', '\t', '\r', '\n', '\f', '\b'};
	static final String[] REPLACE_CHARS = {"\\\\", "\\\"", "\\t", "\\r", "\\n", "\\f", "\\b"};
	static final char[] ESCAPE_MORE_CHARS = Arrays.copyOf(ESCAPE_CHARS, ESCAPE_CHARS.length+1);
	static final String[] REPLACE_MORE_CHARS = Arrays.copyOf(REPLACE_CHARS, REPLACE_CHARS.length+1);
	static {
		ESCAPE_MORE_CHARS[ESCAPE_MORE_CHARS.length-1] = '/';
		REPLACE_MORE_CHARS[REPLACE_MORE_CHARS.length-1] = "\\/";
	}
	
	/**
	 * Return the JSON-encoded value WITHOUT encoding forward-slashes '/'.
	 */
	public static final String encodeValue(final String value) {
		return StringUtil.replaceAll(value, ESCAPE_CHARS, REPLACE_CHARS);
	}

	/**
	 * Return the JSON-encoded value, optionally encoding forward-slashes.
	 */
	public static final String encodeValueAndSlash(final String value, boolean encodeSlash) {
		return StringUtil.replaceAll(value, 
				encodeSlash ? ESCAPE_MORE_CHARS : ESCAPE_CHARS, 
				encodeSlash ? REPLACE_MORE_CHARS : REPLACE_CHARS);
	}
}
