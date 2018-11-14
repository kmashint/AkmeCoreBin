package akme.core.test;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;
import akme.core.io.StringBufferFast;
import akme.core.io.XmlBuffer;
import akme.core.io.XmlNode;
import akme.core.io.XmlReader;
import akme.core.io.XmlTokenizer;
import akme.core.io.XmlUtil;
import akme.core.lang.NameValue;
import akme.core.lang.NameValueFast;

/** 
 * JUnit TestCase for XML classes.
 * 
 * @author Copyright(c) 2009 Frameworks.ca
 * @author $Author: Keith.mashinter $ 
 * @version $Date: 10/17/03 2:17a $ 
 * $NoKeywords: $ 
 */
public class CoreXmlTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(CoreXmlTest.class);
	}
	
	public CoreXmlTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * Using txt.contentEquals(cmp.delete(,).append(buf,,)) is more efficient than txt.equals(buf.substring(,))
	 * since buf.substring() creates a new String, but only after 1,000,000 iterations of 5-char comparisons,
	 * or sooner for larger substrings that have larger overhead for new String.
	 * buf.regionMatches(,txt,,) is twice as fast or more than either contentEquals or equals(substring).
	 */
	public void test0perform1() {
		StringBuilder buf = new StringBuilder(".4.4.<![CDATA[.d.d.]]>");
		StringBuilder cmp = new StringBuilder();
		StringBufferFast buff = new StringBufferFast(buf.length());
		buff.append(buf);
		final String find = "<!CDATA["; 
		final int n = 1000000;
		int i1 = 5, i2 = 10;
		long t0 = System.currentTimeMillis();
		for (int i=0; i<n; i++) {
			if (find.equals(buf.substring(i1,i2)));
		}
		System.out.println("allow new using substring "+ (System.currentTimeMillis()-t0));
		t0 = System.currentTimeMillis();
		for (int i=0; i<n; i++) {
			if (find.contentEquals(cmp.delete(0, cmp.length()).append(buf,i1,i2)));
		}
		System.out.println("avoid new using contentEquals "+ (System.currentTimeMillis()-t0));
		t0 = System.currentTimeMillis();
		for (int i=0; i<n; i++) {
			if (buff.regionMatches(i1, find, 0, i2-i1));
		}
		System.out.println("avoid new using regionMatches "+ (System.currentTimeMillis()-t0));
	}
	
	public void test1SimpleXT() {
		String xml = "<x c='3'><y b='2'><z a='1'>content</z></y></x>";
		XmlTokenizer xt = new XmlTokenizer(xml);
		assertEquals("x should be at index 0", true, xt.findTag("x"));
		assertEquals("x[c] should be 3", "3", xt.getAttributeValue("c"));
		assertEquals("y should be at index 9", true, xt.findTag("y"));
		assertEquals("y[b] should be 2", "2", xt.getAttributeValue("b"));
		assertEquals("z should be at index 18", true, xt.findTag("z"));
		assertEquals("z[a] should be 1", "1", xt.getAttributeValue("a"));
		assertEquals("z value should be content", "content", xt.getElementValue());
	}
	
	public void test2ComplexXT() {
		String xml = "<x dd = 4 cc = 	'3'> <y b='2'><yy/><z a='1'> z-content </z> y-content	</y></x>";
		XmlTokenizer xt = new XmlTokenizer(xml);
		assertEquals("x should be at index 0", true, xt.findTag("x"));
		assertEquals("x[dd] should be 4", "4", xt.getAttributeValue("dd"));
		assertEquals("x[cc] should be 3", "3", xt.getAttributeValue("cc"));
		assertEquals("y should be at index 21", true, xt.findTag("y"));
		assertEquals("y[b] should be 2", "2", xt.getAttributeValue("b"));
		assertEquals("yy should be at index 30", true, xt.findTag("yy"));
		assertEquals("yy[b] should be null", null, xt.getAttributeValue("b"));
		assertEquals("z should be at index 35", true, xt.findTag("z"));
		assertEquals("z[a] should be 1", "1", xt.getAttributeValue("a"));
		assertEquals("z value should be z-content", "z-content", xt.getElementValue());
		assertEquals("y value should be y-content", "y-content", xt.getElementValue("y"));
	}

	public void test3CdataXT() {
		String xml = "<y b='2'><z a='1'> z-content </z> <![CDATA[<y-content/> ]]>]]&gt;<![CDATA[ END ]]>	</y>";
		XmlTokenizer xt = new XmlTokenizer(xml);
		assertEquals("y should be at index 0", true, xt.findTag("y"));
		assertEquals("y[b] should be 2", "2", xt.getAttributeValue("b"));
		assertEquals("z should be at index 9", true, xt.findTag("z"));
		assertEquals("z[a] should be 1", "1", xt.getAttributeValue("a"));
		assertEquals("z value should be z-content", "z-content", xt.getElementValue());
		assertEquals("y value should be '<y-content/> ]]> END'", "<y-content/> ]]> END", xt.getElementValue("y"));
	}
	
	public void test4XR() throws IOException {
		StringReader sr = new StringReader("<y b='2'><z a='1'> z-content </z> <![CDATA[<y-content/> ]]>]]&gt;<![CDATA[ END ]]>	</y>");
		XmlReader xr = new akme.core.io.XmlReader(sr);
		assertTrue("y should be found", xr.findTag("y"));
		assertEquals("y[b] should be 2", "2", xr.getAttributeValue("b"));
		assertTrue("z should be found", xr.findTag("z"));
		assertEquals("z[a] should be 1", "1", xr.getAttributeValue("a"));
		assertEquals("z value should be z-content", "z-content", xr.getElementValue());
		assertEquals("y value should be '<y-content/> ]]> END'", "<y-content/> ]]> END", xr.getElementValue("y"));

		sr = new StringReader("<x><y b='2'>2</y><y b='3'>3</y><z/></x>");
		xr = new XmlReader(sr);
		assertTrue("x", xr.findTag("x"));
		assertTrue("y", xr.findTag("y"));
		assertEquals("y[b]", "2", xr.getAttributeValue("b"));
		assertTrue("y", xr.findTag("y"));
		assertEquals("y[b]", "3", xr.getAttributeValue("b"));
		
		sr.reset();
		xr = new XmlReader(sr);
		final String[] yTags = new String[] {"z", "y"};
		assertTrue("x", xr.findTag("x"));
		for (int i=xr.findTagIn(yTags); i > 0; i=xr.findTagIn(yTags)) {
			String name = xr.getAttributeValue("b");
			String value = xr.getElementValue();
			if ("y".equals(yTags[i])) assertEquals("y[b]", name, value);
		}
	}
	
	public void testXmlBuffer() {
		final String nodeName = "CUSTOMER";
		XmlBuffer xb = new XmlBuffer();
		xb.setLineWrapping(true);
		xb.openTag(nodeName);
		xb.addTag("ID", "010101010");
		xb.addTag("COMMON_ID", null);
		xb.openTag("LOCALE");
		xb.addTag("COUNTRY_CODE", "\"&<>", new String[] { "ID","010" });
		xb.closeTag("LOCALE");
		xb.addTagCDATA("MEMO","<>STUFF]]><>");
		xb.closeTag(nodeName);
		//System.out.println(xb.toXml());
		assertEquals(
			"<CUSTOMER>\n\t<ID>010101010</ID>\n\t<COMMON_ID/>\n\t<LOCALE>\n\t\t<COUNTRY_CODE ID=\"010\">&quot;&amp;&lt;&gt;</COUNTRY_CODE>\n\t</LOCALE>\n\t<MEMO><![CDATA[<>STUFF]]>]]&gt;<![CDATA[<>]]></MEMO>\n</CUSTOMER>\n",
			xb.toXml()
			);
	}
	
	public void testXmlDecode() {
		assertEquals("a&<>\"''b", XmlUtil.decodeValue("a&amp;&lt;&gt;&quot;&#39;&apos;b"));
		assertEquals("a&b", XmlUtil.decodeValue("a&amp;b"));
		assertEquals("&", XmlUtil.decodeValue("&amp;"));
		assertEquals("&b", XmlUtil.decodeValue("&amp;b"));
		assertEquals("&b", XmlUtil.decodeValue("&#38;b"));
		assertEquals("ab", XmlUtil.decodeValue("ab"));
		assertEquals("&#b", XmlUtil.decodeValue("&#b"));
		assertEquals("a&ampb", XmlUtil.decodeValue("a&ampb"));
		assertEquals("a&amp", XmlUtil.decodeValue("a&amp"));
		assertEquals("a&m", XmlUtil.decodeValue("a&m"));
		assertEquals("a&", XmlUtil.decodeValue("a&"));
	}

	public void testXmlNodeSimple() {
		XmlNode node1 = new XmlNode("Name");
		node1.addElement("First", "Keith&");
		node1.addElement("Last", "Mashinter");
		assertEquals("<Name><First>Keith&amp;</First><Last>Mashinter</Last></Name>", node1.toXml());
	}

	public void testXmlNodeNested() {
		XmlNode node1 = new XmlNode("Person", new NameValue[] { new NameValueFast("id", "123") } );
		node1.addElement("First", "Keith&");
		node1.addElement("Last", "Mashinter");
		XmlNode node2 = new XmlNode("Address");
		node2.addElement("City", "Toronto");
		node2.addElement("Country", null);
		node1.addNode(node2);
		assertEquals(
			"<Person id=\"123\"><First>Keith&amp;</First><Last>Mashinter</Last>"
				+ "<Address><City>Toronto</City><Country/></Address></Person>",
			node1.toXml());
	}
	
	public void testEncodeTagsNotIn() {
		final String[] tags = {"b", "i", "ins", "del", "br", "p", "li", "ol", "ul",
				"strong", "em" /* prefer shorter b and i rather than strong and em */};
		String xml;
		assertEquals("&lt;script&gt;&lt;x/&gt;<b>x</b>&lt;/script&gt;<i>i</i><br/>", XmlUtil.encodeExceptTagsIn("<script><x/><b>x</b></script><i>i</i><br/>", tags));
		xml = "<b>x<br/></b><i>i</i>";
		assertSame(xml, XmlUtil.encodeExceptTagsIn(xml, tags));
		assertEquals("&amp;&lt;&gt;&quot;&#39;", XmlUtil.encodeExceptTagsIn("&<>\"\'", tags));
		assertEquals("&lt;&gt;", XmlUtil.encodeExceptTagsIn("<>", tags));
		assertEquals("&lt;", XmlUtil.encodeExceptTagsIn("<", tags));
		xml = "b";
		assertSame(xml, XmlUtil.encodeExceptTagsIn(xml, tags));
		xml = "";
		assertSame(xml, XmlUtil.encodeExceptTagsIn(xml, tags));
		xml = null;
		assertSame(xml, XmlUtil.encodeExceptTagsIn(xml, tags));
		//KM: For another time, could allow certain attributes but this is a larger re-write of the caller.
		//final String[] attrs = {"id", "class", "title"};
		//xml = "<b>x<br class='foo'/></b><i>i</i>";
		//assertEquals(xml.replace("\'", "&#39;"), XmlUtil.encodeExceptTagsIn(xml, tags, attrs));
	}
	
	public void testSkipIfEntityAt() {
		String str = "&#39;&lt;script&#bad;&nametoolong;";
		assertEquals("should skip over &#39;", 5, XmlUtil.skipIfEntityAt(str, 0));
		assertEquals("should skip over &lt;", 9, XmlUtil.skipIfEntityAt(str, 5));
		assertEquals("should stay at &#bad;", 15, XmlUtil.skipIfEntityAt(str, 15));
		assertEquals("should stay at &nametoolong;", 21, XmlUtil.skipIfEntityAt(str, 21));
		assertEquals("should stay at &", 0, XmlUtil.skipIfEntityAt("&", 0));
		assertEquals("should stay at &a", 0, XmlUtil.skipIfEntityAt("&a", 0));
		assertEquals("should stay at &a;", 0, XmlUtil.skipIfEntityAt("&a;", 0));
		assertEquals("should skip over &ab;", 4, XmlUtil.skipIfEntityAt("&ab;", 0));
		assertEquals("should skip over &#1;", 4, XmlUtil.skipIfEntityAt("&#1;", 0));
		assertEquals("should stay at &#a;", 0, XmlUtil.skipIfEntityAt("&#a;", 0));
		assertEquals("should skip over &#x0;", 5, XmlUtil.skipIfEntityAt("&#x0;", 0));
		assertEquals("should skip over &#xa;", 5, XmlUtil.skipIfEntityAt("&#xa;", 0));
		assertEquals("should skip over &#xA;", 5, XmlUtil.skipIfEntityAt("&#xA;", 0));
		assertEquals("should stay at &#xg;", 0, XmlUtil.skipIfEntityAt("&#xg;", 0));
	}
	
	static final char[] DANGER_CHARS = "&<>\"\';#".toCharArray();
	
	public void XtestXmlRandom() {
		for (int t=0; t<10240; t++) {
			double randy = Math.random();
			int len = (int)(randy*1024.0D);
			StringBuilder sb = new StringBuilder(len);
			for (int i=0; i<len; i++) {
				randy = Math.random();
				sb.append( (randy < 0.8D) ?
						DANGER_CHARS[(int)(randy*DANGER_CHARS.length)] :
						(char)(randy*128.0D) );
			}
			String str = sb.toString();
			assertEquals(str, XmlUtil.decodeValue(XmlUtil.encodeValue(str)));
		}
	}

}
