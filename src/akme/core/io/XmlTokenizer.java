package akme.core.io;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import akme.core.util.StringUtil;

/**
 * Easily and efficiently parse XML by element tag names and attribute names.
 * This is optimised for speed, not thread-safety, and is forward-only.
 * e.g. 
 * <code><pre>
 * findTag("xElem"); getAttributeValue("xAttr1"); getAttributeValue("xAttr2"); getElementValue();
 * </pre></code>
 * 
 * @author <br/> Original code by AKME Solutions.
 * @author $Author: keith.mashinter $
 * @version $Date: 2007/04/16 15:53:38 $
 * $NoKeywords: $
 */
public class XmlTokenizer implements XmlDeserializer {
	
	static Log logger = LogFactory.getLog(XmlTokenizer.class);
	
	private final String xml;
	private final int xmlLen;
	private int idx;
	private String tag;
	private int tagLen;
	private int attrIdx;
	private final HashMap<String, String> attrMap;
	private boolean throwIfNotFound;
	
	/**
	 * Prepare to parse the given XML.
	 */
	public XmlTokenizer(final String xml) {
		this.xml = xml;
		this.xmlLen = xml != null ? xml.length() : -1;
		this.idx = xml != null ? 0 : -1;
		this.tag = null;
		this.tagLen = -1;
		this.attrIdx = -1;
		this.attrMap = new HashMap<String, String>(4);
		this.throwIfNotFound = false;
	}
	
	/**
	 * Reset to the start of the XML string.
	 * Does not change the throwIfNotFound setting.
	 */
	public void reset() {
		this.idx = xml != null ? 0 : -1;
		this.tag = null;
		this.tagLen = -1;
		this.attrIdx = -1;
		this.attrMap.clear();
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
	 * Return the XML string.
	 */
	public String getXml() {
		return this.xml;
	}

	/** 
	 * Get the current index within the XML string, -1 if past the end or invalid.
	 */
	public int getIndex() {
		return this.idx;
	}
	
	/**
	 * Get the current tag name or null. 
	 */
	public String getTagName() {
		return this.tag;
	}
	
	/**
	 * Find and position the internal index at the next tag, or -1 if not found.
	 * Skip to the end of any current tag.
	 */
	public boolean findTagNext() {
		if (-1 != this.idx) {
			// Skip to the end of any current tag.
			int i = this.findTagEnd();
			if (-1 != i) {
				i = this.findTagStart(i);
				this.idx = i;
				while (i++ < this.xml.length()) {
					// Break on a non-name character.
					if (-1 != XmlUtil.ELEMENT_START.indexOf(this.xml.charAt(i))) break;
				}
				if (i >= this.xml.length()) i = -1;
			}
			if (-1 == i) {
				this.tag = null;
				this.tagLen = -1;
			} else {
				this.tag = this.xml.substring(this.idx+1, i);
				this.tagLen = this.tag.length();
			}
		}
		return -1 != this.idx;
	}

	/**
	 * Find and position the internal index at the given tag, or -1 if not found. 
	 * Skip past any current element value before finding the next tag.
	 */
	public boolean findTag(final String tagName) {
		if (this.idx != -1) {
			// Skip to the end of any current tag.
			this.findTagEnd();
			this.tag = tagName;
			this.tagLen = (tagName != null) ? tagName.length() : -1;
			int i = this.findTagStart(this.idx);
			while (i != -1 && !(this.xml.regionMatches(i+1, tagName, 0, tagLen)
					&& XmlUtil.ELEMENT_START.indexOf(this.xml.charAt(i+1+tagLen)) != -1)) {
				i = this.findTagStart(i+1);
			}
			this.idx = i;
			if (i == -1) {
				this.tag = null;
				this.tagLen = -1;
			}
		}
		if (throwIfNotFound && -1 == this.idx) {
			throw new IllegalArgumentException("findTag("+ tagName +") return "+ this.idx);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("findTag("+ tagName +") return "+ this.idx);
		}
		return -1 != this.idx;
	}
	
	/**
	 * Find and position the internal index at the next of any of the given tags,
	 * returning the array index of tagNames that was found or -1 if none were found.
	 * Skip to the end of any current tag.
	 * This is useful for switch (int) case statements. 
	 */
	public int findTagIn(final String[] tagNames) {
		int j = -1;
		if (this.idx != -1) {
			// Skip to the end of any current tag.
			this.findTagEnd();
			int i = this.findTagStart(this.idx);
			String tagName = null;
			while (i != -1) {
				int i2;
				for (j=0; j<tagNames.length; j++) {
					tagName = tagNames[j];
					i2=i+1+tagName.length();
					if (this.xml.regionMatches(i+1, tagName, 0, i2-(i+1))
							&& XmlUtil.ELEMENT_START.indexOf(this.xml.charAt(i2)) != -1) {
						break;
					}
				}
				if (j < tagNames.length) {
					// Found it!
					break;
				} else {
					// Sorry, try again ...
					tagName = null;
					j = -1;
					i = this.findTagStart(i+1);
				}
			}
			this.idx = i;
			if (i != -1) {
				this.tag = tagName;
				this.tagLen = tagName.length();
			} else {
				this.tag = null;
				this.tagLen = -1;
			}
		}
		if (throwIfNotFound && -1 == j) {
			throw new IllegalArgumentException("findTagIn("+ StringUtil.joinString(tagNames, ',') +") return "+ j);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("findTagIn("+ StringUtil.joinString(tagNames, ',') +") return "+ this.idx);
		}
		return j;
	}
	
	/**
	 * Get an attribute value, may be null, by name after positioning to the tag using findTag.
	 * If needed, this must be call before getElement(String) for the same tag.
	 * e.g. findTag("xElem"); getAttributeValue("xAttr1"); getAttributeValue("xAttr2"); getElementValue();
	 */
	public String getAttributeValue(final String attrName) {
		if (this.tag == null) return null;
		if (this.attrIdx != this.idx) {
			int i0 = this.idx;
			int i = this.findTagEnd();
			if (i == -1) i = this.xmlLen;
			int i9 = i;
			//String attrs = this.xml.substring(i0+1+tagLen,i);
			String attrs = this.xml;
			// Move the current index to the end of the start tag,
			// and put the attribute name/value pairs in a Map.
			this.idx = i;
			this.attrIdx = i;
			this.attrMap.clear();
			i = attrs.indexOf('=', i0+1+tagLen);
			while (i != -1 && i < i9) {
				// Handle whitespace before equals (x = '1').
				int i1 = i;
				while (XmlUtil.ELEMENT_START.indexOf(attrs.charAt(i1-1)) != -1) i1--;
				// Find the start of the element name.
				i0 = i1-1;
				while (XmlUtil.ELEMENT_START.indexOf(attrs.charAt(i0-1)) == -1) i0--;
				// Handle whitespace after equals (x = '1').
				i++;
				while (XmlUtil.ELEMENT_START.indexOf(attrs.charAt(i)) != -1) i++;
				// Get the value, handling no quotes, single quotes, or double quotes.
				char c = attrs.charAt(i);
				int i2;
				if (c == '\'') {
					i++;
					i2 = attrs.indexOf('\'',i);
				}
				else if (c == '\"') {
					i++;
					i2 = attrs.indexOf('\"',i); 
				}
				else {
					i2 = i;
					while (XmlUtil.ELEMENT_START.indexOf(attrs.charAt(i2)) == -1) i2++;
				}
				this.attrMap.put(attrs.substring(i0,i1), attrs.substring(i,i2).trim());
				i = attrs.indexOf('=',i2+1);
			}
		}
		return (String) this.attrMap.get(attrName);
	}
	
	/**
	 * Get the value of the current element, may be null.
	 */
	public String getElementValue() {
		if (this.tag == null) return null;
		StringBuilder a = new StringBuilder();
		if (this.idx != -1) {
		  int i0 = this.findTagEnd();
		  // Handle self-closing tag (/>).
		  if (XmlUtil.TAG_END_SLASH_CHAR == this.xml.charAt(i0-1)) {
			this.idx = i0+1;
			this.tag = null;
			this.tagLen = -1;
			return XmlUtil.EMPTY_STRING;
		  }
		  i0++;
		  int i = this.xml.indexOf(XmlUtil.TAG_START_CHAR,i0);
		  int d = 0;
		  while (i != -1) {
			int i2;
		    if ((i2=i+XmlUtil.CDATA_START.length()) <= xmlLen && this.xml.regionMatches(i, XmlUtil.CDATA_START, 0, i2-i)) {
		      if (i>i0) a.append( XmlUtil.decodeValue(this.xml.substring(i0,i)) );
		      i = i2;
		      i0 = this.xml.indexOf(XmlUtil.CDATA_END, i);
		      if (i0 != -1) {
		    	  a.append( this.xml.substring(i,i0) );
		    	  i0 += XmlUtil.CDATA_END.length();
		      } else {
		    	  a.append( this.xml.substring(i) );
		    	  i0 = xmlLen;
		      }
		      i = i0;
		    } else if ((i2=i+1+tagLen) <= xmlLen && this.xml.regionMatches(i+1, this.tag, 0, tagLen)
		      && XmlUtil.ELEMENT_START.indexOf(this.xml.charAt(i2)) != -1) {
		      ++d;
		    } else if ((i2=i+2+tagLen) <= xmlLen && this.xml.regionMatches(i+2, this.tag, 0, tagLen)
		      && XmlUtil.ELEMENT_START.indexOf(this.xml.charAt(i2)) != -1
		      && XmlUtil.TAG_END_SLASH_CHAR == this.xml.charAt(i+1)) {
		      --d;
		      if (d<=0) {
		        if (i>i0) a.append( XmlUtil.decodeValue(this.xml.substring(i0,i)) );
			    this.idx = i2;
			    this.tag = null;
			    this.tagLen = -1;
		        break;
		      }
		      i = i2;
		    }
		    i = this.xml.indexOf(XmlUtil.TAG_START_CHAR,i+1);
		  }
		}
		return a.toString().trim();
	}

	/**
	 * Get the element value at the current position assuming the given tag name.
	 * This is to handle structures like &lt;a>&lt;b>2</b>&lt;c>3&lt;/c>1&lt;/a> where 
	 * <code>findTag("a"); return getElementValue("b")+","+getElementValue("c")+getElementValue("a");</code>
	 * should return 2,3,1.
	 */ 
	public String getElementValue(final String tagName) {
		if (this.idx != -1) {
			this.tag = tagName;
			this.tagLen = (tagName != null) ? tagName.length() : -1;
		}
		return this.getElementValue();
	}
	
	/**
	 * Find and position to the tag element by name and get its value.
	 */
	public String findElementValue(final String tagName) {
		this.findTag(tagName);
		return this.getElementValue();
	}

	/**
	 * Find the index of a non-special tag (<), i.e. not CDATA or comment or special processing.  
	 */
	protected int findTagStart(final int startIdx) {
		int i = this.xml.indexOf(XmlUtil.TAG_START_CHAR,startIdx);
		while (i != -1) {
			int i2;
			if ((i2=i+XmlUtil.CDATA_START.length()) < xmlLen && this.xml.regionMatches(i, XmlUtil.CDATA_START, 0, i2-i)) {
				i = this.xml.indexOf(XmlUtil.CDATA_END, i+XmlUtil.CDATA_START.length());
			}
			else if ((i2=i+XmlUtil.COMMENT_START.length()) < xmlLen && this.xml.regionMatches(i, XmlUtil.CDATA_START, 0, i2-i)) {
				i = this.xml.indexOf(XmlUtil.COMMENT_END, i+XmlUtil.COMMENT_START.length());
			}
			else if ((i2=i+XmlUtil.PROCESS_START.length()) < xmlLen && this.xml.regionMatches(i, XmlUtil.CDATA_START, 0, i2-i)) {
				i = this.xml.indexOf(XmlUtil.PROCESS_END, i+XmlUtil.PROCESS_START.length());
			}
			else {
				break; // not a special case so break
			}
			i = this.xml.indexOf(XmlUtil.TAG_START_CHAR,i+1);
	  	}
	  	return i;		
	}


	/**
	 * Find the index of the end of a non-special tag (>), i.e. not CDATA or comment or special processing. 
	 */
	protected int findTagEnd() {
		if (this.tag == null) return this.idx;
		int i = this.idx;
		if (i != -1 && this.xml.charAt(i) == XmlUtil.TAG_START_CHAR && this.xml.regionMatches(i+1, this.tag, 0, tagLen)) {
			// Move to the end of the tag (>) if at the beginning (<...).
			i = this.xml.indexOf(XmlUtil.TAG_END_CHAR,i);
		}
		return i;
	}
	
}

/*


var AkmeXmlUtil = {
 CDATA_BEGIN: "<"+"![CDATA[", // Avoid exact XML CDATA markers.
 CDATA_END: "]]"+">",
 COMMENT_BEGIN: "<!--",
 COMMENT_END: "-->",
 ELEMENT_BEGIN: "> \t\r\n",
 ELEMENT_END: "</",
 PROCESS_BEGIN: "<?",
 PROCESS_END: "?>",
 TAG_BEGIN: "<",
 TAG_END: ">",
 CDATA_END_REPLACE: "]]"+">]]&gt;<"+"![CDATA[",
 //ENCODE_CHARS: [ "&", "<", ">", "\'", "\"" ],
 //DECODE_CHARS: [ "&amp;", "&lt;", "&gt;", "&apos;", "&quot;" ],
 ATTRIBUTE_EQUAL: "=",
 ENCODE_REGEXP: new RegExp("[&<>'\"]","g"),
 ENCODE_MAP: { "&": "&amp;", "<": "&lt;", ">": "&gt;", "\'": "&apos;", "\"": "&quot;" },
 DECODE_REGEXP: new RegExp("&amp;|&lt;|&gt;|&apos;|&quot;","g"),
 DECODE_MAP: { "&lt;": "<", "&gt;": ">", "&apos;": "\'", "&quot;": "\"", "&amp;": "&" },
 ENCODING_ISO_8859_1: ["encoding","ISO-8859-1"],
 ENCODING_ISO_8859_15: ["encoding","ISO-8859-15"],
 ENCODING_UTF_8: ["encoding","UTF-8"],
 ENCODING_UTF_16: ["encoding","UTF-16"],
 ENCODING_WINDOWS_1252: ["encoding","WINDOWS-1252"],
 exists: function(o) {
  return !(typeof(o) == 'undefined' || typeof(o) == 'unknown' || o == null);
 },
 encodeXml: function(value) {
  return new String(value).replace(this.ENCODE_REGEXP, 
   function(m) {return AkmeXmlUtil.ENCODE_MAP[m];});
 },
 decodeXml: function(value) {
  return new String(value).replace(this.DECODE_REGEXP, 
   function(m) {return AkmeXmlUtil.DECODE_MAP[m];});
 },
 encodeCdata: function(value) {
  return AkmeUtil.replaceAll(value, this.CDATA_END, this.CDATA_END_REPLACE);
 }
}


function AkmeXmlReader(xml) {
 this.str = (xml || xml=="") ? xml : null;
 this.idx = 0;
 this.tag = null;
 return this;
}
AkmeXmlReader.prototype.reset = function(xml) {
 this.str = (xml || xml=="") ? xml : null;
 this.idx = 0;
 this.tag = null;
}
AkmeXmlReader.prototype.getIndex = function() {
 return this.idx;
}
AkmeXmlReader.prototype.findTagBegin = function(startIdx) {
 with (AkmeXmlUtil) {
  var i = this.str.indexOf(TAG_BEGIN,startIdx);
  while (i != -1) {
   if (CDATA_BEGIN == this.str.substring(i,CDATA_BEGIN.length)) {
    i = this.str.indexOf(CDATA_END, i+CDATA_BEGIN.length);
   }
   else if (COMMENT_BEGIN == this.str.substring(i,COMMENT_BEGIN.length)) {
    i = this.str.indexOf(COMMENT_END, i+COMMENT_BEGIN.length);
   }
   else if (PROCESS_BEGIN == this.str.substring(i,PROCESS_BEGIN.length)) {
    i = this.str.indexOf(PROCESS_END, i+PROCESS_BEGIN.length);
   }
   else {
    break; // not a special case so break
   }
   i = this.str.indexOf(TAG_BEGIN,i);
  }
 }
 return i;
}

AkmeXmlReader.prototype.moveTo = function(String tag) {
 if (tag) this.tag = tag;
 if (this.idx != -1 && this.str != null) {
  with (AkmeXmlUtil) {
   var i = this.findTagBegin(this.idx);
   while (i != -1 && !(this.tag == this.str.substring(i+1,i+1+this.tag.length)
     && ELEMENT_BEGIN.indexOf(this.str.charAt(i+1+this.tag.length)) != -1)) {
    i = this.findTagBegin(i+1);
   }
   this.idx = i;
  }
 }
 return this.idx;
}

AkmeXmlReader.prototype.getAttribute = function(String attr) {
 var i0 = this.idx;
 var a = [];
 with (AkmeXmlUtil) {
  var i = this.str.indexOf(TAG_END,i0);
  var attrs = this.str.substring(i0,i);
  var i = attrs.indexOf(ATTRIBUTE_EQUAL);
  var c = null;
  while (i != -1) {
   // should really be whitespace and not ELEMENT_BEGIN
   if (attr = attrs.substring(i-attr.length-1,i)
     && ELEMENT_BEGIN.indexOf(attrs.charAt(i-attr.length))) {
    i0 = i+2;
    c = attrs.charAt(i0-1);
    if (c == "\'") i = attrs.indexOf("\'",i0);
    else if (c == "\"") i = attrs.indexOf("\"",i0);
    else i = attrs.length;
    a[a.length] = attrs.substring(i0,i);
   }
   i = attrs.indexOf(ATTRIBUTE_EQUAL,i+1);
  }
 }
 return a.join("");
}

AkmeXmlReader.prototype.getElement = function(String tag) {
 this.moveTo(tag);
 var a = [];
 if (this.idx != -1 && this.str != null) {
  var d = 0;
  var i0 = this.idx;
  with (AkmeXmlUtil) {
   // Move to the end of the tag (>) if at the begining (<...).
   if (TAG_BEGIN+this.tag == this.str.substring(i0,i0+1+this.tag.length)) i0 = this.str.indexOf(TAG_END,i0)+1;
   var i = this.str.indexOf(TAG_BEGIN,i0);
   while (i != -1) {
    if (CDATA_BEGIN == this.str.substring(i,i+CDATA_BEGIN.length)) {
     if (i>i0) a[a.length] = decodeXml(this.str.substring(i0,i));
     i += CDATA_BEGIN.length;
     i0 = this.str.indexOf(CDATA_END, i);
     if (i0>i) a[a.length] = decodeXml(this.str.substring(i,i0));
     i0 += CDATA_END.length;
     i = i0;
    } else if (this.tag == this.str.substring(i+1,i+1+this.tag.length)
      && ELEMENT_BEGIN.indexOf(this.str.charAt(i+1+this.tag.length)) != -1) {
     ++d;
    } else if (this.tag == this.str.substring(i+2,i+2+this.tag.length)
      && ELEMENT_BEGIN.indexOf(this.str.charAt(i+2+this.tag.length)) != -1
      && "/" == this.str.charAt(i+1)) {
     --d;
     if (d<=0) {
      if (i>i0) a[a.length] = decodeXml(this.str.substring(i0,i));
      break;
     }
    }
    i = this.str.indexOf(TAG_BEGIN,i+1);
   }
  }
 }
 return a.join("");
}


*/