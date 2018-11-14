package akme.core.io;

/**
 * Simple interface for creating XML.
 *
 * @author Original code by AKME Solutions
 * @author $Author: keith.mashinter $
 * @version $Date: 2007/01/08 23:25:56 $
 * $NoKeywords: $
 */
public interface XmlSerializer {

	/**
	 * Get the state of line-wrapping.
	 *
	 * @return true if wrapping.
	 */
	public boolean isLineWrapping();

	/**
	 * Set the state of line-wrapping.
	 *
	 * @param lineWrapping true if wrapping.
	 */
	public void setLineWrapping(boolean lineWrapping);

	/**
	 * Get the current start tag state. 
	 * @return True if the current start tag is unclosed, i.e. not yet appended ">" or "/>".
	 */
	public boolean isTagOpen();

	/**
	 * Open an XML tag.
	 *
	 * @param name Name of the XML tag.
	 */
	public void openTag(String name);

	/**
	 * Open an XML tag with the given attributes.
	 *
	 * @param name Name of the XML tag.
	 * @param attrs Name/Value attribute series (even elements are names, odd are values).
	 */
	public void openTag(String name, String[] attrs);

	/**
	 * Add an XML tag attribute.
	 *
	 * @param name Name of the attribute.
	 * @param value Value of the attribute.
	 */
	public void addAttribute(String name, Object value);

	/**
	 * Self-close an XML tag.
	 */
	public void closeTag();

	/**
	 * Close an XML tag.
	 *
	 * @param name Name of the XML tag.
	 */
	public void closeTag(String name);

	/**
	 * Close an XML tag, including the value between the tags.
	 * Null values will produce a self-closing tag.
	 * Empty string values will produce both the opening and closing tags with no value.
	 *
	 * @param name Name of the XML tag.
	 * @param value Value to encapsulate between the opening and closing tags.
	 */
	public void closeTag(String name, Object value);

	/**
	 * Close an XML tag, including the value between the tags as CDATA.
	 * Null values will produce a self-closing tag.
	 * Empty string values will produce both the opening and closing tags with no value.
	 *
	 * @param name Name of the XML tag.
	 * @param value Value to encapsulate as CDATA between the opening and closing tags.
	 */
	public void closeTagCDATA(String name, Object value);

	/**
	 * Add an XML tag with the given value.
	 * Null values will produce a self-closing tag.
	 * Empty string values will produce both the opening and closing tags with no value.
	 *
	 * @param name Name of the XML tag.
	 * @param value Value to encapsulate between the opening and closing tags.
	 */
	public void addTag(String name, Object value);

	/**
	 * Add an XML tag with the given value.
	 * Null values will produce a self-closing tag.
	 * Empty string values will produce both the opening and closing tags with no value.
	 *
	 * @param name Name of the XML tag.
	 * @param value Value to encapsulate between the opening and closing tags.
	 * @param attrs Name/Value attribute series (even elements are names, odd are values).
	 */
	public void addTag(String name, Object value, String[] attrs);

	/**
	 * Add an XML tag, including the value between the tags as CDATA.
	 * Null values will produce a self-closing tag.
	 * Empty string values will produce both the opening and closing tags with no value.
	 *
	 * @param name Name of the XML tag.
	 * @param value Value to encapsulate as CDATA between the opening and closing tags.
	 */
	public void addTagCDATA(String name, Object value);

	/**
	 * Add an XML tag, including the value between the tags as CDATA.
	 * Null values will produce a self-closing tag.
	 * Empty string values will produce both the opening and closing tags with no value.
	 *
	 * @param name Name of the XML tag.
	 * @param value Value to encapsulate as CDATA between the opening and closing tags.
	 * @param attrs Name/Value attribute series (even elements are names, odd are values).
	 */
	public void addTagCDATA(String name, Object value, String[] attrs);

    /**
     * Add an XML comment.
     *
     * @param comment Comment to be added as &lt;-- comment --&gt;.
     */
	public void addComment(String comment);

	/**
	 * Add the XML declaration which should be done as the first line.
	 *
	 * @param version Version number of the XML standard, default "1.0".
	 * @param encoding The character encoding, e.g. "UTF-8".
	 */
	public void addXmlDeclaration(String version, String encoding);

	/**
	 * Add the XML declaration which should be done as the first line.
	 * The default version="1.0" will automatically be added.
	 *
	 * @param encoding The character encoding, e.g. "UTF-8".
	 */
	public void addXmlDeclaration(String encoding);

	/**
	 * Add the default XML declaration: <code>&lt;?xml version="1.0"?&gt;</code>.
	 */
	public void addXmlDeclaration();

	/**
	 * Add the DOCTYPE declaration, e.g.
	 * <code>&lt;!DOCTYPE news SYSTEM "news.dtd"&gt;
	 * </code>
	 *
	 * @param declare The declaration, e.g. "news SYSTEM \"news.dtd\"".
	 */
	public void addDocTypeDeclaration(String declare);

	/**
	 * Add the prepared XML to the stream without any validation -- CAREFUL!
	 *
	 * @param xml Prepared XML to add.
	 */
	public void addPreparedXml(String xml);

}