package akme.core.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import akme.core.lang.NameValue;
import akme.core.lang.NameValueFast;
import akme.core.lang.NameValueTreeFast;
import akme.core.lang.NameValueTree;

/**
 * Simple object to create XML.
 * It builds the tree structure in objects and only serializes to XML with toXml().
 *
 * @author Original code by AKME Solutions
 * @author $Author: keith.mashinter $
 * @version $Date: 2007/01/11 02:50:54 $
 * $NoKeywords: $
 */
public class XmlNode {

	/** Name of the XML tag for use when creating the node tag itself. */
	private String name;

	/** XML tag attributes for use when creating the node tag itself. */
	private NameValue[] attributes;

	/** List of child elements. */
	private List<Object> children = null;

	/**
	 * Create a new XmlNode.
	 *
	 * @param name Name of the node.
	 */
	public XmlNode(String name) {
		this.name = name;
		this.attributes = null;
	}

	/**
	 * Create a new XmlNode.
	 *
	 * @param name Name of the node.
	 * @param attributes Array of name/value attribute pairs.
	 */
	public XmlNode(String name, NameValue[] attributes) {
		this.name = name;
		this.attributes = attributes != null ? attributes.clone() : null;
	}

	/**
	 * Add a simple element to this node.
	 *
	 * @param name Name of XML element.
	 * @param value Value to place within start/end tags (may be null).
	 * @param attributes Array of name/value attribute pairs.
	 */
	public void addElement(String name, Object value, NameValue[] attributes) {
		if (children == null) {
			children = new ArrayList<Object>();
		}
		if (attributes != null) {
			children.add(new NameValueTreeFast(name, value, attributes));
		}
		else {
			children.add(new NameValueFast(name, value));
		}
	}

	/**
	 * Add a simple element to this node.
	 *
	 * @param name Name of XML element.
	 * @param value Value to place within start/end tags (may be null).
	 */
	public void addElement(String name, Object value) {
		addElement(name, value, null);
	}

	/**
	 * Add a simple element to this node as a CDATA value.
	 *
	 * @param name Name of XML element.
	 * @param value Value to place within CDATA start/end tags (may be null).
	 * @param attributes Array of name/value attribute pairs.
	 */
	public void addElementCDATA(String name, Object value, NameValue[] attributes) {
		if (children == null) {
			children = new ArrayList<Object>();
		}
		// Wrap actual objects in Object[] to remember CDATA encoding.
		if (attributes != null) {
			children.add(new Object[] {new NameValueTreeFast(name, value, attributes)});
		}
		else {
			children.add(new Object[] {new NameValueFast(name, value)});
		}
	}

	/**
	 * Add a simple element to this node as a CDATA value.
	 *
	 * @param name Name of XML element.
	 * @param value Value to place within CDATA start/end tags (may be null).
	 */
	public void addElementCDATA(String name, Object value) {
		addElementCDATA(name, value, null);
	}

	/**
	 * Add a simple element to this node.
	 *
	 * @param name Name of XML element.
	 */
	public void addElement(String name) {
		addElement(name, null, null);
	}

	/**
	 * Add a child node to this node.
	 *
	 * @param node Name of child XML node.
	 * @param value Value to place within start/end tags (may be null).
	 * @param attributes Array of name/value attribute pairs.
	 */
	public void addNode(XmlNode node) {
		if (node == this) {
			throw new IllegalArgumentException("Infinite loop detected trying to add a node to itself.");
		}
		if (node != null) {
			if (children == null) {
				children = new ArrayList<Object>();
			}
			children.add(node);
		}
	}

	/**
	 * Check if any content has been added (child nodes or elements).
	 *
	 * @return <code>true</code> if this node has no content.
	 */
	public boolean isEmpty() {
		return (children == null);
	}

	/**
	 * Serialize the tree of XmlNodes into a String.
	 *
	 * @return String respresentation of the tree of XML nodes.
	 */
	public void toXml(XmlSerializer buffer) {
		buffer.openTag(name);
		XmlUtil.toXml(buffer,attributes);
		if (isEmpty()) {
			buffer.closeTag(name, null);
		}
		else {
			if (children != null) {
				for (Iterator<?> it = children.iterator(); it.hasNext();) {
					Object obj = it.next();
					if (obj instanceof XmlNode) {
						((XmlNode) obj).toXml(buffer);
					}
					else if (obj instanceof NameValue) {
						NameValue pair = (NameValue) obj;
						buffer.openTag(pair.getName());
						buffer.closeTag(pair.getName(), pair.getValue());
					}
					else if (obj instanceof NameValueTree) {
						NameValueTree tree = (NameValueTree) obj;
						buffer.openTag(tree.getName());
						XmlUtil.toXml(buffer,tree.getAttributes());
						buffer.closeTag(tree.getName(), tree.getValue());
					}
					else if (obj instanceof Object[] && ((Object[])obj).length == 1) {
						// Use CDATA if wrapped in an Object[].
						Object innerObj = ((Object[])obj)[0];
						if (innerObj instanceof NameValue) {
							NameValue pair = (NameValue) innerObj;
							buffer.openTag(pair.getName());
							buffer.closeTagCDATA(pair.getName(), pair.getValue());
						}
						else if (innerObj instanceof NameValueTree) {
							NameValueTree tree = (NameValueTree) innerObj;
							buffer.openTag(tree.getName());
							XmlUtil.toXml(buffer,attributes);
							buffer.closeTagCDATA(tree.getName(), tree.getValue());
						}
						else {
						throw new IllegalStateException(
							"Found "
								+ innerObj
								+ " within <"
								+ name
								+ ">;"
								+ "CDATA objects must only contain NameValue or NameValueTree classes.");
						}
					}
					else {
						throw new IllegalStateException(
							"Found "
								+ obj
								+ " within <"
								+ name
								+ ">;"
								+ "Child list must only contain Object[1], XmlNode, NameValue, or NameValueTree classes.");
					}
				}
			}
			buffer.closeTag(name);
		}
	}

	/**
	 * Serialize the tree of XmlNodes into a String.
	 *
	 * @return String respresentation of the tree of XML nodes.
	 */
	public String toXml() {
		XmlBuffer result = new XmlBuffer();
		toXml(result);
		return result.toXml();
	}

}
