package akme.core.io;

import java.io.IOException;
import java.io.Reader;
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
 * @version $Date: 2009/05/05 15:53:38 $
 * $NoKeywords: $
 */
public class XmlReader implements XmlDeserializer {
	
	static Log logger = LogFactory.getLog(XmlTokenizer.class);
	
	private final Reader reader;
	private final StringBufferFast buf;
	private int idx;
	private String tag;
	private int tagLen;
	private int attrIdx;
	private final HashMap<String, String> attrMap;
	private boolean throwIfNotFound;
	
	/**
	 * Construct a new XmlBuffer based on a pre-existing reader.
	 */
	public XmlReader(Reader r) {
		this(r, 128);
	}
	
	/**
	 * Construct a new XmlBuffer based on a pre-existing reader and buffer size (to cover longest tag name + 2).
	 */
	public XmlReader(Reader r, int bufferSize) { 
		this.reader = r;
		this.buf = new StringBufferFast(bufferSize);
		this.idx = reader != null ? 0 : -1;
		this.tag = null;
		this.tagLen = -1;
		this.attrIdx = -1;
		this.attrMap = new HashMap<String, String>(4);
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
	 * Return the underlying reader.
	 */
	public Reader getReader() {
		return reader;
	}
	
	/** 
	 * Get the current index within the XML buffer (not the entire XML stream), -1 if past the end or invalid.
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
				while (i++ < this.buf.length()) {
					// Break on a non-name character.
					if (-1 != XmlUtil.ELEMENT_START.indexOf(this.buf.charAt(i))) break;
				}
				if (i >= this.buf.length()) i = -1;
			}
			if (-1 == i) {
				this.tag = null;
				this.tagLen = -1;
			} else {
				this.tag = this.buf.substring(this.idx+1, i);
				this.tagLen = this.tag.length();
			}
		}
		return -1 != this.idx;
	}
	
	/**
	 * Find and position the internal index at the given tag, or -1 if not found.
	 * Skip to the end of any current tag.
	 */
	public boolean findTag(final String tagName) {
		if (this.idx != -1) {
			// Skip to the end of any current tag.
			this.idx = this.findTagEnd();
			this.tag = tagName;
			this.tagLen = (tagName != null) ? tagName.length() : -1;
			int i = this.findTagStart(this.idx);
			while (i != -1 && !(this.buf.regionMatches(i+1, tagName, 0, tagLen)
					&& XmlUtil.ELEMENT_START.indexOf(this.buf.charAt(i+1+tagLen)) != -1)) {
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
	 * Skip past any current element value before finding the next tag.
	 * This is useful for switch (int) case statements. 
	 */
	public int findTagIn(final String[] tagNames) {
		int j = -1;
		if (this.idx != -1) {
			// Skip to the end of any current tag.
			this.idx = this.findTagEnd();
			int i = this.findTagStart(this.idx);
			String tagName = null;
			while (i != -1) {
				int i2;
				for (j=0; j<tagNames.length; j++) {
					tagName = tagNames[j];
					i2 = i+1+tagName.length();
					if (this.buf.regionMatches(i+1, tagName, 0, i2-(i+1))
							&& XmlUtil.ELEMENT_START.indexOf(this.buf.charAt(i2)) != -1) {
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
		int i = startIdx;
		while (i != -1) try {
			while (i >= this.buf.length() || (i = this.buf.indexOf(XmlUtil.TAG_START_CHAR, i)) == -1) {
				// Read until we've found a TAG_START or end-of-stream.
				if (this.buf.readFrom(reader) == -1) break;
			}
			if (i == -1) break;
			this.buf.replace(0, i, XmlUtil.EMPTY_STRING);
			i = 0;
			// Ensure we've at least read enough to find the longest tag name.
			for (int n = this.buf.readFrom(reader); n != -1 && this.buf.length() < this.buf.capacity(); n = this.buf.readFrom(reader)) ;
			int i2;
			final int xmlLen = this.buf.length();
			
			if ((i2=i+XmlUtil.CDATA_START.length()) < xmlLen && this.buf.regionMatches(i, XmlUtil.CDATA_START, 0, i2-i)) {
				i = this.buf.indexOf(XmlUtil.CDATA_END, i+XmlUtil.CDATA_START.length());
			}
			else if ((i2=i+XmlUtil.COMMENT_START.length()) < xmlLen && this.buf.regionMatches(i, XmlUtil.CDATA_START, 0, i2-i)) {
				i = this.buf.indexOf(XmlUtil.COMMENT_END, i+XmlUtil.COMMENT_START.length());
			}
			else if ((i2=i+XmlUtil.PROCESS_START.length()) < xmlLen && this.buf.regionMatches(i, XmlUtil.CDATA_START, 0, i2-i)) {
				i = this.buf.indexOf(XmlUtil.PROCESS_END, i+XmlUtil.PROCESS_START.length());
			}
			else {
				break; // not a special case so break
			}
	  	}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	  	return i;
	}

	
	/**
	 * Find the index of the end of a non-special tag (>), i.e. not CDATA or comment or special processing. 
	 */
	protected int findTagEnd() {
		if (this.tag == null) return this.idx;
		int i = this.idx;
		if (i != -1 && this.buf.charAt(i) == XmlUtil.TAG_START_CHAR && this.buf.regionMatches(i+1, this.tag, 0, tagLen)) try {
			i = i+1+tagLen; 
			while ((i = this.buf.indexOf(XmlUtil.TAG_END_CHAR, i)) == -1) {
				// Read until we've found a TAG_END.
				if (this.buf.readFrom(reader) == -1) break;
			}
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return i;
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
			if (i == -1) i = this.buf.length();
			int i9 = i;
			//String attrs = this.buf.substring(i0+1+tagLen,i);
			StringBufferFast attrs = this.buf;
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
		if (this.idx != -1) try {
		  int i0 = findTagEnd();
		  // Handle self-closing tag (/>).
		  if (i0 > 0 && XmlUtil.TAG_END_SLASH_CHAR == this.buf.charAt(i0-1)) {
			  this.buf.setLength(0);
			  this.idx = 0;
			  this.tag = null;
			  this.tagLen = -1;
			  return XmlUtil.EMPTY_STRING;
		  }
		  i0++;
		  int i = i0;
		  int d = 0;
		  int xmlLen = this.buf.length();
		  while ((i = this.buf.indexOf(XmlUtil.TAG_START_CHAR, i)) == -1) {
			i = xmlLen; 
			if (this.buf.readFrom(reader) == -1) break;
			else xmlLen = this.buf.length();
		  }
		  while (i != -1) {
		    int i2;
			// Ensure we've at least read enough to find the longest string.
			for (int n = this.buf.readFrom(reader); n != -1 && this.buf.length() < this.buf.capacity(); n = this.buf.readFrom(reader)) ;
			xmlLen = this.buf.length();
		    if ((i2=i+XmlUtil.CDATA_START.length()) <= xmlLen && this.buf.regionMatches(i, XmlUtil.CDATA_START, 0, i2-i)) {
		      if (i>i0) a.append( XmlUtil.decodeValue(this.buf.substring(i0,i)) );
		      i = i2;
		      i0 = this.buf.indexOf(XmlUtil.CDATA_END, i);
		      if (i0 != -1) {
		    	  a.append( this.buf.substring(i,i0) );
		    	  i0 += XmlUtil.CDATA_END.length();
		      } else {
		    	  a.append( this.buf.substring(i,xmlLen) );
		    	  i0 = xmlLen;
		      }
		      i = i0;
		    } else if ((i2=i+1+tagLen) <= xmlLen && this.buf.regionMatches(i+1, this.tag, 0, tagLen)
		      && XmlUtil.ELEMENT_START.indexOf(this.buf.charAt(i2)) != -1) {
		      ++d;
		    } else if ((i2=i+2+tagLen) <= xmlLen && this.buf.regionMatches(i+2, this.tag, 0, tagLen)
		      && XmlUtil.ELEMENT_START.indexOf(this.buf.charAt(i2)) != -1
		      && XmlUtil.TAG_END_SLASH_CHAR == this.buf.charAt(i+1)) {
		      --d;
		      if (d<=0) {
		        if (i>i0) a.append( XmlUtil.decodeValue(this.buf.substring(i0,i)) );
			    this.idx = i2;
			    this.tag = null;
			    this.tagLen = -1;
		        break;
		      }
		      i = i2;
		    }
		    i = this.buf.indexOf(XmlUtil.TAG_START_CHAR,i+1);
		  }
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		this.buf.replace(0, this.idx, XmlUtil.EMPTY_STRING);
		this.idx = 0;
		this.tag = null;
		this.tagLen = -1;
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
}
