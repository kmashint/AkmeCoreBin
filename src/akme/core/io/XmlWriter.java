package akme.core.io;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;

import akme.core.util.StringUtil;

/**
 * Simple, fast writer to create XML.
 * It keeps minimal state and serializes to XML as methods are called.
 * The Writer methods throw <code>IOException</code> so this is best used when
 * the intent is to write to disk or network, not to memory.
 * <b>Be careful if you getWriter() and write to it yourself.</b>
 *
 * @author Original code by AKME Solutions
 * @author $Author: keith.mashinter $
 * @version $Date: 2007/01/11 02:50:54 $
 * $NoKeywords: $
 */
public class XmlWriter extends Writer implements XmlSerializer {

	/** Internal buffer. */
	private Writer buffer;

	/** State of line-wrapping. */
	private boolean lineWrapping = false;

	/** Current level of indentation after linewrap. */
	private int indent = 0;

	/** Flag if tag is incomplete and needs final angle bracket (&gt;). */
	private boolean tagOpen = false;


	/**
	 * Construct a new XmlWriter, by default using a <code>CharArrayWriter</code>.
	 */
	public XmlWriter() {
		this.buffer = new CharArrayWriter();
	}

	/**
	 * Construct a new XmlBuffer appending to a pre-existing writer.
	 */
	public XmlWriter(Writer w) {
		this.buffer = (w != null) ? w : new CharArrayWriter();
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
	 * Return the internal writer.
	 *
	 * @return Internal writer.
	 */
	public Writer getWriter() {
		return buffer;
	}

	/**
	 * @see XmlSerializer
	 */
	public void openTag(String name) {
		try {
			if (tagOpen) {
				buffer.write((int)'>');
				if (lineWrapping) {
					buffer.write((int)'\n');
				}
			}
			if (lineWrapping) {
				buffer.write(XmlUtil.indentPad, 0, (indent < XmlUtil.indentPad.length) ? indent : XmlUtil.indentPad.length);
			}
			++indent;
			buffer.write((int)'<');
			buffer.write(name);
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
		tagOpen = true;
	}

	/**
	 * @see XmlSerializer
	 */
	public void openTag(String name, String[] attrs) {
		openTag(name);
		try {
			if (attrs != null) {
				for (int i = 0; i < attrs.length; i++) {
					if (i%2 == 0) {
						buffer.write((int)' ');
						buffer.write(attrs[i]);
					} else {
						String attr = attrs[i];
						if (attr != null) {
							buffer.write('=');
							buffer.write('\"');
							buffer.write(XmlUtil.encodeValue(attr));
							buffer.write((int)'\"');
						}
					}
				}
			}
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * @see XmlSerializer
	 */
	public void addAttribute(String name, Object value) {
		try {
			buffer.write((int)' ');
			buffer.write(name);
			if (value != null) {
				buffer.write((int)'=');
				buffer.write((int)'\"');
				buffer.write(XmlUtil.encodeValue(String.valueOf(value)));
				buffer.write((int)'\"');
			}
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * @see XmlSerializer
	 */
	public void closeTag() {
		try {
			if (indent > 0) --indent;
			buffer.write('/');
			buffer.write('>');
			if (lineWrapping) {
				buffer.write('\n');
			}
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		tagOpen = false;
	}
	
	/**
	 * @see XmlSerializer
	 */
	public void closeTag(String name) {
		try {
			if (indent > 0) --indent;
			if (tagOpen) {
				buffer.write((int)'>');
			} else if (lineWrapping) {
				buffer.write(XmlUtil.indentPad, 0, (indent < XmlUtil.indentPad.length) ? indent : XmlUtil.indentPad.length);
			}
			buffer.write('<');
			buffer.write('/');
			buffer.write(name);
			buffer.write((int)'>');
			if (lineWrapping) {
				buffer.write('\n');
			}
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
		tagOpen = false;
	}

	/**
	 * @see XmlSerializer
	 */
	public void closeTag(String name, Object value) {
		try {
			if (indent > 0) --indent;
			if (value != null) {
				if (tagOpen) {
					buffer.write((int)'>');
				} else if (lineWrapping) {
					buffer.write(XmlUtil.indentPad, 0, (indent < XmlUtil.indentPad.length) ? indent : XmlUtil.indentPad.length);
				}
				buffer.write(XmlUtil.encodeValue(String.valueOf(value)));
				buffer.write('<');
				buffer.write('/');
				buffer.write(name);
				buffer.write((int)'>');
			}
			else {
				buffer.write('/');
				buffer.write('>');
			}
			if (lineWrapping) {
				buffer.write('\n');
			}
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
		tagOpen = false;
	}

	/**
	 * @see XmlSerializer
	 */
	public void closeTagCDATA(String name, Object value) {
		try {
			if (indent > 0) --indent;
			if (value != null) {
				if (tagOpen) {
					buffer.write((int)'>');
				} else if (lineWrapping) {
					buffer.write(XmlUtil.indentPad, 0, (indent < XmlUtil.indentPad.length) ? indent : XmlUtil.indentPad.length);
				}
				buffer.write(XmlUtil.CDATA_START);
				buffer.write(StringUtil.replaceAll(String.valueOf(value),XmlUtil.CDATA_END,XmlUtil.CDATA_END_REPLACE));
				buffer.write(XmlUtil.CDATA_END);
				buffer.write('<');
				buffer.write('/');
				buffer.write(name);
				buffer.write((int)'>');
			}
			else {
				buffer.write('/');
				buffer.write('>');
			}
			if (lineWrapping) {
				buffer.write('\n');
			}
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
		tagOpen = false;
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
	public void addTag(String name, Object value) {
		openTag(name);
		closeTag(name, value);
	}

	/**
	 * @see XmlSerializer
	 */
	public void addTagCDATA(String name, Object value, String[] attrs) {
		openTag(name, attrs);
		closeTagCDATA(name, value);
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
	public void addComment(String comment) {
		try {
			if (lineWrapping) {
				buffer.write(XmlUtil.indentPad, 0, (indent < XmlUtil.indentPad.length) ? indent : XmlUtil.indentPad.length);
			}
			buffer.write("<!-- ");
			buffer.write(comment);
			buffer.write(" -->");
			if (lineWrapping) {
				buffer.write('\n');
			}
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * @see XmlSerializer
	 */
	public void addXmlDeclaration(String version, String encoding) {
		try {
			buffer.write("<?xml version=\"");
			buffer.write(XmlUtil.encodeValue(version));
			buffer.write("\"");
			if (!StringUtil.isEmpty(encoding)) {
				buffer.write(" encoding=\"");
				buffer.write(XmlUtil.encodeValue(encoding));
				buffer.write("\"");
			}
			buffer.write("?>");
			if (lineWrapping) {
				buffer.write('\n');
			}
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
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
		try {
			buffer.write(XmlUtil.DOCTYPE_START);
			buffer.write(declare);
			buffer.write(XmlUtil.DOCTYPE_END);
			if (lineWrapping) {
				buffer.write('\n');
			}
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * @see XmlSerializer
	 */
	public void addPreparedXml(String xml) {
		try {
			buffer.write(xml);
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * @see Writer
	 */
	public void write(char cbuf[], int off, int len) throws IOException {
		buffer.write(cbuf,off,len);
	}

	/**
	 * @see Writer
	 */
	public void flush() throws IOException {
		buffer.flush();
	}

	/**
	 * @see Writer
	 */
	public void close() throws IOException {
		buffer.close();
	}

}
