package akme.core.io;

import java.util.ArrayList;
import java.util.List;


/**
 * Convert JSON to XML on the fly with useAttributes(true):<br />
 * <code>{"list":[{"x":1,"y":2},{"x":3,"y":4}]} -> &lt;list>&lt;array x="1" y="2"/>&lt;array x="3" y="4"/>&lt;/list></code>
 * <br />or with useAttributes(false):<br />
 * <code> -> &lt;list>&lt;array>&lt;x>1&lt;/x>&lt;y>2&lt;/y>&lt;/array>&lt;array>&lt;x>3&lt;/x>&lt;y>4&lt;/y>&lt;/array>&lt;/list>
 * 
 * Ideal would be to have simple properties first and then collections, 
 * but source JSON may not be that way, e.g. Facebook.
 */
public class JsonXmlTransformer {
	
	public static final String ARRAY = "array";
	
	public static final int DEFAULT_ARRAY_SIZE = 8;
	public static final int DEFAULT_OBJECT_SIZE = 4;
	
	/* Using attributes may cause confusion since it only works until a non-trivial sub-object forces a nested tag. */
	boolean useAttributes = false;
	
	/**
	 * Convert JSON to XML on the fly, stream-friendly, no intermediate objects.
	 * 
	 * This is handled properly with useAttributes(true):<br />
	 * {"list":[{"x":1,"y":2},{"x":3,"y":4}]} -> &lt;list>&lt;array x="1" y="2"/>&lt;array x="3" y="4"/>&lt;/list>
	 * <br />
	 * <br />This ends up not being able to use an attribute for homeCity:<br />
     * {"person":{"name":"Jack","addressList":[{"city":"Toronto"},{"city":"Montreal"}],homeCity:"Toronto"}
     *  -> &lt;person name="Jack">&lt;addressList>&lt;array city="Toronto"/>&lt;array city="Montreal"/>&lt;/addressList>&lt;homeCity>Toronto&lt;/homeCity>&lt;/person>
     */
	public void transform(JsonDeserializer json, XmlSerializer xml) {
		final List<String> nest = new ArrayList<String>();
		char c = json.enterArrayOrObject();
		if (c == '\0' || !json.findItemNext()) return;
		int lastLevel = json.getNestLevel();
		while (json.getIndex() != -1) {
			if (c == '{') {
				final String name = json.getName();
				if (nest.size() != 0 && ARRAY.equals(nest.get(nest.size()-1))) {
					xml.openTag(ARRAY);
				} else if (lastLevel == json.getNestLevel()) {
					xml.openTag(name);
					nest.add(name);
				}
				lastLevel = json.getNestLevel();
				c = json.enterArrayOrObject();
			} else if (c == '[') {
				nest.add(ARRAY);
				lastLevel = json.getNestLevel();
				c = json.enterArrayOrObject();
			} else if (json.findItemNext()) {
				lastLevel = json.getNestLevel();
				c = json.enterArrayOrObject();
				if (c == '\0') {
					if (useAttributes && xml.isTagOpen()) xml.addAttribute(json.getName(), json.getValue());
					else xml.addTag(json.getName(), json.getValue());
				} else {
					final String name = json.getName();
					xml.openTag(name);
					nest.add(name);
				}
			} else if ((c = json.leaveArrayOrObject()) != '\0' && nest.size() != 0) {
				final String name = nest.get(nest.size()-1);
				if (c == '}' && ARRAY.equals(name)) {
					if (xml.isTagOpen()) xml.closeTag();
					else xml.closeTag(ARRAY);
				} else {
					if (c == ']' && ARRAY.equals(name)) nest.remove(nest.size()-1);
					xml.closeTag(nest.remove(nest.size()-1));
				}
				lastLevel = json.getNestLevel();
				c = json.enterArrayOrObject();
			}
		}
		while (nest.size() != 0) xml.closeTag(nest.remove(nest.size()-1));
	}

	public boolean isUseAttributes() {
		return useAttributes;
	}

	public JsonXmlTransformer setUseAttributes(boolean useAttributes) {
		this.useAttributes = useAttributes;
		return this;
	}
	
}
