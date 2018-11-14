package akme.core.io;

import java.io.IOException;

import akme.core.util.StringUtil;


/**
 * Simple, fast buffer to create XML.
 * It keeps minimal state and serializes to XML as methods are called.
 * Be careful if you getBuffer() and write to it yourself.
 *
 * @author Original code by AKME Solutions 
 * @author $Author: keith.mashinter $
 * @version $Date: 2007/01/11 02:50:54 $
 * $NoKeywords: $
 */
public class XmlBuffer implements XmlSerializer {

	/** Internal buffer. */
	private StringBufferFast buffer;

	/** State of line-wrapping. */
	private boolean lineWrapping = false;

	/** Current level of indentation after linewrap. */
	private int indent = 0;

	/** Flag if tag is incomplete and needs final angle bracket (&gt;). */
	private boolean tagOpen = false;


	/**
	 * Construct a new XmlBuffer, by default using a <code>StringBuffer</code>.
	 */
	public XmlBuffer() {
		this.buffer = new StringBufferFast();
	}

	/**
	 * Construct a new XmlBuffer appending to a pre-existing buffer.
	 */
	public XmlBuffer(StringBufferFast sb) {
		this.buffer = (sb != null) ? sb : new StringBufferFast();
	}

	/**
	 * @see XmlSerializer
	 */
	public boolean isLineWrapping() {
		return lineWrapping;
	}

	/**
	 * @see XmlSerializer
	 */
	public void setLineWrapping(boolean lineWrapping) {
		this.lineWrapping = lineWrapping;
	}

	/**
	 * @see XmlSerializer
	 */
	public boolean isTagOpen() {
		return tagOpen;
	}

	/**
	 * Return the internal fast buffer.
	 *
	 * @return Internal fast buffer.
	 */
	public StringBufferFast getBuffer() {
		return buffer;
	}

	/**
	 * @see XmlSerializer
	 */
	public void openTag(String name) {
		if (tagOpen) {
			buffer.append('>');
			if (lineWrapping) {
				buffer.append('\n');
			}
		}
		if (lineWrapping) {
			buffer.padLength(indent,'\t');
		}
		++indent;
		buffer.append('<');
		buffer.append(name);
		tagOpen = true;
	}

	/**
	 * @see XmlSerializer
	 */
	public void openTag(String name, String[] attrs) {
		openTag(name);
		if (attrs != null) {
			for (int i = 0; i < attrs.length; i++) {
				if (i%2 == 0) {
					buffer.append(' ');
					buffer.append(attrs[i]);
				} else {
					String attr = attrs[i];
					if (attr != null) {
						buffer.append('=');
						buffer.append('\"');
						buffer.append(XmlUtil.encodeValue(attr));
						buffer.append('\"');
					}
				}
			}
		}
	}

	/**
	 * @see XmlSerializer
	 */
	public void addAttribute(String name, Object value) {
		buffer.append(' ');
		buffer.append(name);
		if (value != null) {
			buffer.append('=');
			buffer.append('\"');
			buffer.append(XmlUtil.encodeValue(String.valueOf(value)));
			buffer.append('\"');
		}
	}

	/**
	 * @see XmlSerializer
	 */
	public void closeTag() {
		if (indent > 0) --indent;
		buffer.append('/');
		buffer.append('>');
		if (lineWrapping) {
			buffer.append('\n');
		}
		tagOpen = false;
	}
	
	/**
	 * @see XmlSerializer
	 */
	public void closeTag(String name) {
		if (indent > 0) --indent;
		if (tagOpen) {
			buffer.append('>');
		} else if (lineWrapping) {
			buffer.padLength(indent,'\t');
		}

		buffer.append('<');
		buffer.append('/');
		buffer.append(name);
		buffer.append('>');
		if (lineWrapping) {
			buffer.append('\n');
		}
		tagOpen = false;
	}

	/**
	 * @see XmlSerializer
	 */
	public void closeTag(String name, Object value) {
		if (indent > 0) --indent;
		if (value != null) {
			if (tagOpen) {
				buffer.append('>');
			} else if (lineWrapping) {
				buffer.padLength(indent,'\t');
			}
			buffer.append(XmlUtil.encodeValue(String.valueOf(value)));
			buffer.append('<');
			buffer.append('/');
			buffer.append(name);
			buffer.append('>');
		}
		else {
			buffer.append('/');
			buffer.append('>');
		}
		if (lineWrapping) {
			buffer.append('\n');
		}
		tagOpen = false;
	}

	/**
	 * @see XmlSerializer
	 */
	public void closeTagCDATA(String name, Object value) {
		if (indent > 0) --indent;
		if (value != null) {
			if (tagOpen) {
				buffer.append('>');
			} else if (lineWrapping) {
				buffer.padLength(indent,'\t');
			}
			buffer.append(XmlUtil.CDATA_START);
			buffer.append(StringUtil.replaceAll(String.valueOf(value),XmlUtil.CDATA_END,XmlUtil.CDATA_END_REPLACE));
			buffer.append(XmlUtil.CDATA_END);
			buffer.append('<');
			buffer.append('/');
			buffer.append(name);
			buffer.append('>');
		}
		else {
			buffer.append('/');
			buffer.append('>');
		}
		if (lineWrapping) {
			buffer.append('\n');
		}
		tagOpen = false;
	}

	/**
	 * @see XmlSerializer
	 */
	public void addTag(String name, Object value) {
		openTag(name);
		closeTag(name, value);
	}

	/**
	 * @see XmlSerializer
	 */
	public void addTag(String name, Object value, String[] attrs) {
		openTag(name, attrs);
		closeTag(name, value);
	}

	/**
	 * @see XmlSerializer
	 */
	public void addTagCDATA(String name, Object value) {
		openTag(name);
		closeTagCDATA(name, value);
	}

	/**
	 * @see XmlSerializer
	 */
	public void addTagCDATA(String name, Object value, String[] attrs) {
		openTag(name, attrs);
		closeTagCDATA(name, value);
	}

	/**
	 * Return the serialized XML string.
	 *
	 * @return Serialized XML.
	 */
	public String toXml() {
		return buffer.toString();
	}

	/**
	 * Append XML to the given object.
	 * @throws IOException
	 */
	public void toXml(Appendable append) throws IOException {
		append.append(buffer);
	}

	/**
	 * @see XmlSerializer
	 */
	public void addComment(String comment) {
		if (lineWrapping) {
			buffer.padLength(indent,'\t');
		}
		buffer.append("<!-- ");
		buffer.append(comment);
		buffer.append(" -->");
		if (lineWrapping) {
			buffer.append('\n');
		}
	}

	/**
	 * @see XmlSerializer
	 */
	public void addXmlDeclaration(String version, String encoding) {
		buffer.append("<?xml version=\"");
		buffer.append(XmlUtil.encodeValue(version));
		buffer.append("\"");
		if (!StringUtil.isEmpty(encoding)) {
			buffer.append(" encoding=\"");
			buffer.append(XmlUtil.encodeValue(encoding));
			buffer.append("\"");
		}
		buffer.append("?>");
		if (lineWrapping) {
			buffer.append('\n');
		}
	}

	/**
	 * @see XmlSerializer
	 */
	public void addXmlDeclaration(String encoding) {
		addXmlDeclaration("1.0", encoding);
	}

	/**
	 * @see XmlSerializer
	 */
	public void addXmlDeclaration() {
		addXmlDeclaration(null);
	}

	/**
	 * @see XmlSerializer
	 */
	public void addDocTypeDeclaration(String declare) {
		buffer.append(XmlUtil.DOCTYPE_START);
		buffer.append(declare);
		buffer.append(XmlUtil.DOCTYPE_END);
		if (lineWrapping) {
			buffer.append('\n');
		}
	}

	/**
	 * @see XmlSerializer
	 */
	public void addPreparedXml(String xml) {
		buffer.append(xml);
	}

}
