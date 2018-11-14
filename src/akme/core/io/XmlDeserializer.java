package akme.core.io;

/**
 * General interface to de-serialize/parse XML.  
 * 
 * @author <br/> Original code by AKME Solutions.
 *
 */
public interface XmlDeserializer {

	public void setThrowIfNotFound(boolean throwIfNotFound) ;
	
	public boolean isThrowIfNotFound() ;

	public boolean findTagNext() ;
	
	public boolean findTag(final String tagName) ;
	
	public int findTagIn(final String[] tagNames) ;
	
	public String findElementValue(final String tagName) ;
	
	public String getAttributeValue(final String attrName) ;
	
	public String getElementValue() ;
	
	public String getElementValue(final String tagName) ;
	
	public String getTagName() ;
	
}
