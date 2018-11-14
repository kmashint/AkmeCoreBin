package akme.core.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.regex.Pattern;

import akme.core.lang.NameValue;
import akme.core.util.StringUtil;

/**
 * Utilities for XML.  Also recommend www.dom4j.org.
 * JDOM doesn't have simple methods, only object-purist ones.
 * <p>
 * From the XML recommendation:
<pre>
 [Definition: A parsed entity contains text, a sequence of characters,
 which may represent markup or character data.]
 [Definition: A character is an atomic unit of text as specified by
 ISO/IEC 10646 [ISO/IEC 10646] (see also [ISO/IEC 10646-2000]).
 Legal characters are tab, carriage return, line feed, and the legal
 characters of Unicode and ISO/IEC 10646. The versions of these standards
 cited in A.1 Normative References were current at the time this document
 was prepared. New characters may be added to these standards by
 amendments or new editions. Consequently, XML processors must accept
 any character in the range specified for Char. The use of
 "compatibility characters", as defined in section 6.8 of
 [Unicode] (see also D21 in section 3.6 of [Unicode3]), is discouraged.]

 Character Range
  [2]    Char    ::=    #x9 | #xA | #xD | [#x20-#xD7FF] |
                        [#xE000-#xFFFD] | [#x10000-#x10FFFF]
         // any Unicode character, excluding the surrogate blocks,
            FFFE, and FFFF. //

 The mechanism for encoding character code points into bit patterns may
 vary from entity to entity. All XML processors must accept the UTF-8
 and UTF-16 encodings of 10646; the mechanisms for signaling which of
 the two is in use, or for bringing other encodings into play, are
 discussed later, in 4.3.3 Character Encoding in Entities.

 ...

 The ampersand character (&) and the left angle bracket (<)
 may appear in their literal form only when used as markup delimiters, or
 within a comment, a processing instruction, or a CDATA section. If they
 are needed elsewhere, they must be escaped using either numeric
 character references or the strings "&amp;" and "&lt;"
 respectively. The right angle bracket (>) may be represented using the
 string "&gt;", and must, for compatibility, be escaped using
 "&gt;" or a character reference when it appears in the string
 "]]>" in content, when that string is not marking the end of a CDATA
 section.
 To allow attribute values to contain both single and double quotes, the
 apostrophe or single-quote character (&apos;) may be represented as
 "&apos;" or "&#39;" for older IE, and the double-quote character (") as "&quot;".
</pre>
 * </p>
 * @author <br/> Original code by AKME Solutions
 * @author $Author: keith.mashinter $
 * @version $Date: 2007/04/16 15:53:38 $
 * $NoKeywords: $
 */
public abstract class XmlUtil {

	/** Special XML characters to be escaped. */
	static final String[] ESCAPE_CHARS = new String[] { "&", "<", ">", "\"", "\'" };
	static final Pattern ESCAPE_CHARS_REGEXP = Pattern.compile("(&)|(<)|(>)|(\")|(')");

	/** Replacements for the associated XML characters to be escaped, IE6 does not understand &apos; so use &#39; instead. */
	static final String[] REPLACE_CHARS = new String[] { "&amp;", "&lt;", "&gt;", "&quot;", "&#39;", "&apos;" };
	
	/** Special XML characters for use with decoding. */
	static final char[] UNESCAPE_CHARS = new char[] { '&', '<', '>', '\"', '\'', '\'' };

	/** Special XML entities for use with decoding. */
	static final String[] UNREPLACE_AMP = new String[] { "&" };
	static final String[] UNREPLACE_CHARS = new String[] { "&amp;", "&lt;", "&gt;", "&quot;", "&#", "&apos;" };
	static final Pattern UNREPLACE_CHARS_REGEXP = Pattern.compile("(&amp;)|(&lt;)|(&gt;)|(&quot;)|(&#[0-9];)|(&apos;)");
	
	/** Padding for indentation. */
	static final char[] indentPad = new char[32];

	static {
		Arrays.fill(indentPad,'\t');
	}

	public static final String EMPTY_STRING = "";

	public static final String TAG_START = "<";
	
	public static final char TAG_START_CHAR = TAG_START.charAt(0);

	public static final String TAG_END = ">";

	public static final char TAG_END_CHAR = TAG_END.charAt(0);

	public static final String TAG_END_SLASH = "/";

	public static final char TAG_END_SLASH_CHAR = TAG_END_SLASH.charAt(0);

	public static final String ELEMENT_START = "/> \t\r\n";

	public static final String ELEMENT_END = "</";
	 
	public static final String PROCESS_START = "<?";

	public static final String PROCESS_END = "?>";
		 
	/** Start of comment. */
	public static final String COMMENT_START = "<!--";

	/** End of comment. */
	public static final String COMMENT_END = "-->";

	/** Mark start of CDATA. */
	public static final String CDATA_START = "<"+"![CDATA[";

	/** Mark end of CDATA. */
	public static final String CDATA_END = "]]"+">";

	/** Replace premature end of CDATA within the CDATA value by ending it, adding the ]]>, and starting CDATA again. */
	public static final String CDATA_END_REPLACE = "]]"+">]]&gt;<"+"![CDATA[";

	/** Xml declaration piece for encoding="ISO-8859-1" */
	public static final String ENCODING_ISO_8859_1 = "ISO-8859-1";

	/** Xml declaration piece for encoding="ISO-8859-15" */
	public static final String ENCODING_ISO_8859_15 = "ISO-8859-15";

	/** Xml declaration piece for encoding="UTF-8" */
	public static final String ENCODING_UTF_8 = "UTF-8";

	/** Xml declaration piece for encoding="UTF-16" */
	public static final String ENCODING_UTF_16 = "UTF-16";

	/** Xml declaration piece for encoding="WINDOWS-1252" */
	public static final String ENCODING_WINDOWS_1252 = "WINDOWS-1252";

	/** Start of DOCTYPE declaration. */
	public static final String DOCTYPE_START = "<!DOCTYPE ";

	/** End of DOCTYPE declaration. */
	public static final String DOCTYPE_END = ">";
	
	public static final boolean isNameStartChar(final char c) {
		switch (c) {
		case ':': case '_': 
			return true;
		default:
			return ((c >= 'A' && c <= 'Z') || 
					(c >= 'a' && c <= 'z') ||
					(c >= '\u00c0' && c <= '\u00d6') ||
					(c >= '\u00d8' && c <= '\u00f6') ||
					(c >= '\u00f8' && c <= '\u02ff') ||
					(c >= '\u0370' && c <= '\u037d') ||
					(c >= '\u037f' && c <= '\u1fff') ||
					(c >= '\u200c' && c <= '\u200d') ||
					(c >= '\u2070' && c <= '\u218f') ||
					(c >= '\u2c00' && c <= '\u2fef') ||
					(c >= '\u3001' && c <= '\ud7ff') ||
					(c >= '\uf900' && c <= '\ufdcf') ||
					(c >= '\ufdf0' && c <= '\ufffd'));
		}
	}
	
	public static final boolean isNameChar(final char c) {
		switch (c) {
		case '<': case '>': case '/': case ' ': case '\t': case '\r': case '\n':
			return false;
		case '-': case '.': case '\u00b7':
			return true;
		default:
			return (c >= '0' && c <= '9') ||
				isNameStartChar(c) ||
				(c >= '\u0300' && c <= '\u036f') ||
				(c >= '\u203f' && c <= '\u2040');
		}
	}
	
	/**
	 * Skip over an entity at the given position, returning the position after it.
	 * If the given position does not look like a valid entity, this returns the same/given position.
	 * This does not check again actual entity values, just that it matches one of these within 10 characters:
	 *   &#99999; or &#xFFFF; or &name;
	 * http://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references
	 */
	public static final int skipIfEntityAt(final String str, final int pos) {
		if (str.charAt(pos) != '&') return pos;
		int pos2 = str.length();
		if (pos+3 >= pos2) return pos;
		int i = pos+1;
		char c = str.charAt(i);
		char pos1c = '\0';
		if (c == '#') {
			pos1c = c;
			i++;
			c = str.charAt(i);
			if (c == 'x') {
				pos1c = c;
				i++;
				c = str.charAt(i);
			}
		}
		if (pos+9 < pos2) pos2 = pos+9; // Only try to find ; for up to 9 characters.
		for (; i<pos2; i++) {
			c = str.charAt(i);
			if (c == ';') break;
			if (c >= '0' && c <= '9') continue;
			else {
				if (pos1c == '#') break;
				if (pos1c == 'x' && !( (c >= 'A' & c <= 'F') || (c >= 'a' & c <= 'f') )) break;
				if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) continue;
			}
			break;
		}
		if (c == ';') return i+1;
		else return pos;
	}
	
	/**
	 * Get the character encoding if specified in the xml declaration of an XML string.
	 * 
	 * @param xml An existing XML string that may start with the &lt;?xml ... ?> declaration.
	 * @return The encoding if specified in the declaration, e.g. UTF-8 in &lt;?xml version="1.0" encoding="UTF-8"?>, or null if unspecified.
	 */
	public static String getCharacterEncoding(final String xml) {
		String result = null;
		int pos = xml.startsWith("<?xml ") ? 0 : -1;
		if (pos != -1) pos = xml.indexOf("?>", pos);
		if (pos != -1) pos = xml.lastIndexOf(" encoding=", pos);
		if (pos != -1) {
			pos += " encoding=".length() + 1;
			char quote = xml.charAt(pos - 1);
			result = xml.substring(pos, xml.indexOf(quote, pos));
		}
		return result;
	}
	
	/**
	 * Encode special XML characters (&amp;&lt;&gt;&quot;&#39;&apos;) for content of a node or attribute.
	 * This does not compensate for &lt;![CDATA[...]]> sections, that needs to be done elsewhere.
	 * This will only create intermediary objects if necessary.
	 *
	 * @param value Value to encode.
	 * @return Safe for XML.
	 */
	public static String encodeValue(final String value) {
		if (value == null || value.length() == 0) return value;
		final int len = value.length();
		final int[] found = StringUtil.indexOfAll(value, ESCAPE_CHARS);
		if (found.length == 0) return value;
		int pos = 0;
		StringBuilder result = new StringBuilder(len/3 + 1 + len);
		for (int i=0; i<found.length; i++) {
			result.append(value, pos, found[i]);
			pos = found[i];
			switch (value.charAt(pos)) {
			case '&': result.append(REPLACE_CHARS[0]); break;
			case '<': result.append(REPLACE_CHARS[1]); break;
			case '>': result.append(REPLACE_CHARS[2]); break;
			case '"': result.append(REPLACE_CHARS[3]); break;
			case '\'': result.append(REPLACE_CHARS[4]); break;
			default: break;
			}
			pos++;
		}
		result.append(value, pos, len);
		return result.toString();
	}
	
	/*// Using StringUtil.indexOfAll/One() is still over 2x as fast as Pattern Matcher on Java 6.0.24.
	public static String encodePattern(final String value) {
		if (value == null || value.length() == 0) return value;
		final int len = value.length();
		Matcher m = ESCAPE_CHARS_REGEXP.matcher(value);
		boolean found = m.find();
		if (!found) return value;
		int pos = 0;
		StringBuilder result = new StringBuilder(len/3 + 1 + len);
		while (found) {
			result.append(value, pos, m.start());
			pos = m.end();
			switch (value.charAt(m.start())) {
			case '&': result.append(REPLACE_CHARS[0]); break;
			case '<': result.append(REPLACE_CHARS[1]); break;
			case '>': result.append(REPLACE_CHARS[2]); break;
			case '"': result.append(REPLACE_CHARS[3]); break;
			case '\'': result.append(REPLACE_CHARS[4]); break;
			default: break;
			}
			found = m.find();
		}
		result.append(value, pos, len);
		return result.toString();		
	}
	*/

	/**
	 * Encode special XML characters (&amp;&lt;&gt;&quot;&#39;&apos;) for content of a node or attribute.
	 * This does not compensate for &lt;![CDATA[...]]> sections, that needs to be done elsewhere.
	 * This will only create intermediary objects if necessary.
	 *
	 * @param out Stream to which the value will be encoded.
	 * @param value Value to encode.
	 */
	public static void encodeValue(final Appendable out, final String value) throws IOException {
		//return StringUtil.replaceAll(value, ESCAPE_CHARS, REPLACE_CHARS);
		if (value == null || value.length() == 0) return;
		final int len = value.length();
		final int[] found = StringUtil.indexOfAll(value, ESCAPE_CHARS);
		if (found.length == 0) { out.append(value); return; }
		int pos = 0;
		for (int i=0; i<found.length; i++) {
			out.append(value, pos, found[i]);
			pos = found[i];
			switch (value.charAt(pos)) {
			case '&': out.append(REPLACE_CHARS[0]); break;
			case '<': out.append(REPLACE_CHARS[1]); break;
			case '>': out.append(REPLACE_CHARS[2]); break;
			case '"': out.append(REPLACE_CHARS[3]); break;
			case '\'': out.append(REPLACE_CHARS[4]); break;
			default: break;
			}
			pos++;
		}
		out.append(value, pos, len);
	}

	/**
	 * Encode special XML characters (&amp;&lt;&gt;&quot;&#39;&apos;) for content of a node or attribute.
	 * This does not compensate for &lt;![CDATA[...]]> sections, that needs to be done elsewhere.
	 * This will only create intermediary objects if necessary.
	 *
	 * @param out Stream to which the value will be encoded.  Writer is separate for performance.
	 * @param value Value to encode.
	 */
	public static void encodeValue(final Writer out, final String value) throws IOException {
		//return StringUtil.replaceAll(value, ESCAPE_CHARS, REPLACE_CHARS);
		if (value == null || value.length() == 0) return;
		final int len = value.length();
		final int[] found = StringUtil.indexOfAll(value, ESCAPE_CHARS);
		if (found.length == 0) { out.write(value); return; }
		int pos = 0;
		for (int i=0; i<found.length; i++) {
			out.write(value, pos, found[i]-pos);
			pos = found[i];
			switch (value.charAt(pos)) {
			case '&': out.write(REPLACE_CHARS[0]); break;
			case '<': out.write(REPLACE_CHARS[1]); break;
			case '>': out.write(REPLACE_CHARS[2]); break;
			case '"': out.write(REPLACE_CHARS[3]); break;
			case '\'': out.write(REPLACE_CHARS[4]); break;
			default: break;
			}
			pos++;
		}
		// stream write takes count as 3rd parameter rather than string end
		out.write(value, pos, len-pos);
	}

	/**
	 * Encode most characters for safety against cross-site scripting,
	 * while allowing the given set of tags to pass through as-is.
	 * For example, &lt;b&gt;bold&lt;/b&gt; (<b>bold</b>) may be allowed but not &lt;script/&gt; (<script/>).
	 * This is optimised to only create new objects as necessary.
	 */
	public static String encodeExceptTagsIn(final String value, final String[] tags) {
		if (value == null || value.length() == 0) return value;
		final int[] found = StringUtil.indexOfAll(value, ESCAPE_CHARS);
		if (found.length == 0) return value;
		int pos = -1;
		// Check if all tags are OK and if so return the given value.
		for (int i=0; i<found.length && pos == -1; i++) {
			switch (value.charAt(found[i])) {
			case '&': pos = i; break;
			case '<': if (!isTagIn(value, found[i], tags)) pos = i; break;
			case '>': if (!isTagIn(value, found[i], tags)) pos = i; break;
			case '"': pos = i; break;
			case '\'': pos = i; break;
			default: break;
			}
		}
		if (pos == -1) return value;
		pos = 0;
		final int len = value.length();
		final StringBuilder result = new StringBuilder(len/3 + 1 + len);
		for (int i=0; i<found.length; i++) {
			result.append(value, pos, found[i]);
			pos = found[i];
			switch (value.charAt(pos)) {
			case '&': result.append(REPLACE_CHARS[0]); break;
			case '<': result.append(isTagIn(value, pos, tags) ? '<' : REPLACE_CHARS[1]); break;
			case '>': result.append(isTagIn(value, pos, tags) ? '>' : REPLACE_CHARS[2]); break;
			case '"': result.append(REPLACE_CHARS[3]); break;
			case '\'': result.append(REPLACE_CHARS[4]); break;
			default: break;
			}
			pos++;
		}
		result.append(value, pos, len);
		return result.toString();
	}
	
	/**
	 * Check if the given position in xml is one of the given tags.
	 */
	public static boolean isTagIn(final String xml, int pos, final String[] tags) {
		return indexOfTagIn(xml, pos, tags, false) != -1;
	}
	public static int indexOfTagIn(final String xml, int pos, final String[] tags, final boolean allowAttributes) {
		if (xml.charAt(pos) != '<') pos = pos > 0 ? xml.lastIndexOf('<', pos-1) : -1;
		if (pos == -1) return -1;
		int found = -1;
		final int len = xml.length();
		for (int j=0; j<tags.length; j++) {
			final String keepTag = tags[j];
			if (keepTag == null || keepTag.length() == 0) continue;
			final int keepLen = keepTag.length();
			if ((len-pos >= keepLen && xml.regionMatches(pos+1, keepTag, 0, keepLen)) || 
					(len-pos+1 >= keepLen && xml.regionMatches(pos+2, keepTag, 0, keepLen)
							&& xml.charAt(pos+1) == '/')) {
				// Tags match up to their length so check next character as non-name character.
				// Fast-path the typical ">/ " characters.
				final char c = xml.charAt(pos + keepLen + (xml.charAt(pos+1) == '/' ? 2 : 1));
				if (c == '>' || c == '/' || c == ' ' || !isNameChar(c)) {
					found = j;
					break;
				}
			}
		}
		if (found != -1 && !allowAttributes) {
			for (int i=pos + (xml.charAt(pos+1) == '/' ? 2 : 1) + tags[found].length(); i<len; i++) {
				final char c = xml.charAt(i);
				if (c == '/' || c == ' ' || c == '\t' || c == '\r' || c == '\n') continue;
				if (c != '>') found = -1;
				break;
			}
		}
		/*// KM: For another time, could allow certain attributes but this is a larger re-write of the caller. 
		if (found == -1) return false;
		pos += tags[found].length() + (xml.charAt(pos+1) == '/' ? 2 : 1);
		int tagEnd = xml.indexOf(TAG_END_CHAR, pos);
		if (tagEnd == -1) tagEnd = xml.indexOf(TAG_START_CHAR, pos);
		if (tagEnd == -1) tagEnd = len;
		while (found != -1) {
			for (char c; pos < tagEnd && 
				((c = xml.charAt(pos)) == '>' || c == '/' || c == ' ' || !isNameChar(c)); pos++) ;
			// Break if no name character found.
			if (pos == tagEnd) break;
			// Get the name.
			int pos2 = pos+1;
			for (; pos2 < tagEnd && isNameChar(xml.charAt(pos2)); pos2++) ;
			boolean afound = false;
			for (int j=0; j<attributes.length; j++) {
				if (xml.regionMatches(pos, attributes[j], 0, pos2-pos)) {
					afound = true;
					break;
				}
			}
			if (!afound) found = -1;
			else {
				// Get the value, handling equals or not, no quotes, single quotes, or double quotes.
				boolean efound = false;
				for (char c; pos2 < tagEnd && 
					((c = xml.charAt(pos2)) == '=' || c == ' ' || Character.isWhitespace(c)); pos2++) {
					if (c == '=') efound = true;
				}
				if (efound && pos2 < tagEnd) {
					char c = xml.charAt(pos2); 
					if (c == '\'' || c == '\"') {
						pos2 = xml.indexOf(c, pos2+1);
						if (pos2 == -1) pos2 = tagEnd;
					} else {
						for (; pos2 < tagEnd && 
							!((c = xml.charAt(pos2)) == ' ' || c == '\t' || c == '\r' || c == '\n' || Character.isWhitespace(c)); pos2++) ;
					}
				}
				pos = pos2;
			}
		}
		*/
		return found;
	}
	
	/**
	 * Decode special XML characters (&amp;&lt;&gt;&quot;&#39;&apos;) for content of a node or attribute.
	 * This does not compensate for &lt;![CDATA[...]]&gt; sections, that needs to be done elsewhere.
	 */
	public static String decodeValue(final String value) {
		//return StringUtil.replaceAll(value, UNREPLACE_CHARS, UNESCAPE_CHARS);
		if (value == null || value.length() == 0) return value;
		final int len = value.length();
		int found = value.indexOf('&');
		if (found == -1) return value;
		int pos = 0;
		StringBuilder result = new StringBuilder(len/3 + 1 + len);
		while (found != -1) {
			result.append(value, pos, found);
			pos = found;
			switch (pos+1 < len ? value.charAt(pos+1) : '&') {
			case 'a': // &amp; or &apos;
				pos += appendIfMatch(result, value, pos, 0);
				if (pos == found) pos += appendIfMatch(result, value, pos, 5);
				break;
			case 'l': // &lt;
				pos += appendIfMatch(result, value, pos, 1);
				break;
			case 'g': // &gt;
				pos += appendIfMatch(result, value, pos, 2);
				break;
			case 'q': // &quot;
				pos += appendIfMatch(result, value, pos, 3);
				break;
			case '#': // &#39; or other &#...;
				pos += appendIfMatch(result, value, pos, 4);
				if (pos != found) break;
				int pos2 = value.indexOf(';', pos+2);
				if (pos2 != -1) {
					result.append((char)Integer.parseInt(value.substring(pos+2, pos2)));
					if (pos2 < len) pos = pos2 + 1;
				}
				break;
			case '&': // pass-through to default
			default: 
				break;
			}
			if (pos == found) {
				// Did not match a known entity, just emit as-is.
				pos++;
				result.append('&'); 
			}
			found = value.indexOf('&', pos);
		}
		result.append(value, pos, len);
		return result.toString();
		/*// Using StringUtil.indexOfAll/One() is still over 2x as fast as Pattern Matcher on Java 6.0.24.
		Matcher m = REPLACE_CHARS_REGEXP.matcher(value);
		boolean found = m.find();
		if (!found) return value;
		final int len = value.length();
		int pos = 0;
		StringBuilder result = new StringBuilder(len/3 + 1 + len);
		while (found) {
			result.append(value, pos, m.start());
			pos = m.end();
			switch (value.charAt(m.start()+1)) {
			// ...
			}
			found = m.find();
		}
		result.append(value, pos, len);
		*/
	}
	
	/** 
	 * Append the value to the result related to the given entity if matched.
	 * 
	 * @return the length of characters replaced or 0 if none. 
	 */
	private static final int appendIfMatch(final StringBuilder result, final String value, final int pos, final int entityIdx) {
		if (value.regionMatches(pos+2, REPLACE_CHARS[entityIdx], 2, REPLACE_CHARS[entityIdx].length()-2)) {
			result.append(UNESCAPE_CHARS[entityIdx]);
			return REPLACE_CHARS[entityIdx].length();
		} else {
			return 0;
		}
	}
	

	/**
	 * Serialize the XML tag attributes into the given StringBuffer.
	 *
	 * @param sb Buffer to which attributes will be appended.
	 * @param attrs Attributes to be appended.
	 */
	public static void toXml(final XmlSerializer buffer, final NameValue[] attrs) {
		if (attrs == null) return;
		for (int i = 0; i < attrs.length; i++) {
			NameValue attr = attrs[i];
			if (attr != null) {
				buffer.addAttribute(attr.getName(),attr.getValue());
			}
		}
	}

}
