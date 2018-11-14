package akme.core.test;

import java.io.StringReader;

import junit.framework.TestCase;
import akme.core.io.JsonReader;
import akme.core.io.JsonTokenizer;
import akme.core.io.JsonXmlTransformer;
import akme.core.io.XmlBuffer;

/** 
 * JUnit TestCase for XML classes.
 * 
 * @author Copyright(c) 2009 Frameworks.ca
 * @author $Author: Keith.mashinter $ 
 * @version $Date: 10/17/03 2:17a $ 
 * $NoKeywords: $ 
 */
public class JsonXmlTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(JsonXmlTest.class);
	}
	
	public JsonXmlTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void test1simple() {
		JsonTokenizer json;
		XmlBuffer xml;
		String result;
		
		json = new JsonTokenizer("{'list':[{'x':1,'y':2},{'x':3,'y':4}]}".replace('\'', '\"'));
		xml = new XmlBuffer();
		new JsonXmlTransformer().setUseAttributes(true).transform(json, xml);
		result = xml.toXml();
		System.out.println(result);
		assertEquals("<list><array x='1' y='2'/><array x='3' y='4'/></list>".replace('\'', '\"'), result);
		
		json = new JsonTokenizer("{'person':{'name':'Jack','addressList':[{'city':'Toronto'},{'city':'Montreal'}],homeCity:'Toronto'}".replace('\'', '\"'));
		xml = new XmlBuffer();
		new JsonXmlTransformer().transform(json, xml);
		result = xml.toXml();
		System.out.println(xml.toXml());
		assertEquals("<person><name>Jack</name><addressList><array><city>Toronto</city></array><array><city>Montreal</city></array></addressList><homeCity>Toronto</homeCity></person>".replace('\'', '\"'), result);
		
	}

	public void test2facebook() {
		JsonTokenizer json;
		XmlBuffer xml;
		String result;
		
		json = new JsonTokenizer("{\"data\":["+
				"{\"id\":\"90790343096_10150251767018097\",\"type\":\"status\",\"created_time\":\"2011-07-18T01:09:18+0000\",\"updated_time\":\"2011-07-18T01:09:18+0000\",\"from\":{\"name\":\"Mary Nicol\",\"id\":\"100001076589689\"},\"message\":\"I LOVE THIS COFFEE!! The Best!!\"},"+
				"{\"id\":\"90790343096_10150251749698097\",\"type\":\"status\",\"created_time\":\"2011-07-18T00:40:20+0000\",\"updated_time\":\"2011-07-18T00:40:20+0000\",\"from\":{\"name\":\"Kelly STuart\",\"id\":\"594744178\"},\"message\":\"I wish more Tim horton off the ice cream, us in the burbs do without\r\n\"},"+
				"{\"id\":\"90790343096_10150266054078097\",\"type\":\"status\",\"created_time\":\"2011-08-05T01:55:15+0000\",\"updated_time\":\"2011-08-05T01:55:15+0000\",\"from\":{\"name\":\"Johanne Grenier Lapointe\",\"id\":\"520923011\"},\"message\":\"Jadore mon caf\u00e9 Tim Horton\"}"+
				"]}");
		xml = new XmlBuffer();
		new JsonXmlTransformer().setUseAttributes(false).transform(json, xml);
		result = xml.toXml();
		System.out.println(result);
		//assertEquals("<list><array x='1' y='2'/><array x='3' y='4'/></list>".replace('\'', '\"'), result);
		
		json = new JsonTokenizer(
				"{\"data\":[{\"id\":\"90790343096_10150282490948097\",\"type\":\"status\",\"created_time\":\"2011-08-25T11:55:50+0000\",\"updated_time\":\"2011-08-25T11:55:50+0000\",\"from\":{\"name\":\"Anke D\u00f6ring\",\"id\":\"100000074458544\"},\"message\":\"best Kaffee wo gibt, greetings from Germany :))))\",\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282456933097\",\"type\":\"status\",\"created_time\":\"2011-08-25T10:45:19+0000\",\"updated_time\":\"2011-08-25T10:45:19+0000\",\"from\":{\"name\":\"Ivan Lino Silva\",\"id\":\"505545013\"},\"message\":\"Home of the super addictive coffee! :)\",\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282410243097\",\"type\":\"status\",\"created_time\":\"2011-08-25T08:32:48+0000\",\"updated_time\":\"2011-08-25T08:32:48+0000\",\"from\":{\"name\":\"Tammy Thomas-Gaudreault\",\"id\":\"522730223\"},\"message\":\"Tim Horton's, i have been to several tim horton's across quebec and I've noticed something, none of the coffees taste the same.. some tim hortons have better coffee then others, yet i remember 10 years ago you couldnt tell a different but lately there are big differences from one tim to the next. \",\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282289413097\",\"type\":\"status\",\"created_time\":\"2011-08-25T03:47:14+0000\",\"updated_time\":\"2011-08-25T03:47:14+0000\",\"from\":{\"name\":\"Alanna Landymore\",\"id\":\"539192993\"},\"message\":\"Dear Timmy....my Dr. said I have to break up with you right away. I am definitely not impressed and will still sneak over \",\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282281728097\",\"type\":\"status\",\"created_time\":\"2011-08-25T03:31:32+0000\",\"updated_time\":\"2011-08-25T03:31:32+0000\",\"from\":{\"name\":\"John Russell\",\"id\":\"1417666178\"},\"message\":\"Dear Tim Hortons....you guys need to expand into the greater Kansas City area....that would be really awesome...and then everyone here can see for themselves how amazing you guys are....best coffee shop ever....seriously.\",\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282278173097\",\"type\":\"status\",\"created_time\":\"2011-08-25T03:24:55+0000\",\"updated_time\":\"2011-08-25T03:24:55+0000\",\"from\":{\"name\":\"Meryaam Marokiina\",\"id\":\"100001050667536\"},\"message\":\"Vive les timbits :)\n\",\"likes\":{\"data\":[{\"name\":\"Omaima Jouti\",\"id\":\"573957381\"}],\"count\":1},\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282269398097\",\"type\":\"link\",\"created_time\":\"2011-08-25T03:08:55+0000\",\"updated_time\":\"2011-08-25T03:08:55+0000\",\"from\":{\"name\":\"DobbernationLOVES\",\"category\":\"Personal blog\",\"id\":\"219117594774467\"},\"message\":\"Tim Hortons breakfast http://wp.me/pGU5A-5YD\",\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282190378097\",\"type\":\"status\",\"created_time\":\"2011-08-25T01:20:09+0000\",\"updated_time\":\"2011-08-25T01:21:38+0000\",\"from\":{\"name\":\"Anne Hartmann\",\"id\":\"100001056694602\"},\"message\":\"thank you for the delicious cinnomon roll, the banana nut muffin and the great oatmeal! :D i wish we would have tim hortons in germany :) \",\"comments\":{\"data\":[{\"id\":\"90790343096_10150282190378097_18207899\",\"from\":{\"name\":\"Anne Hartmann\",\"id\":\"100001056694602\"},\"message\":\"i forgot to say that tim hortons is thousand times better than starbucks :)\",\"created_time\":\"2011-08-25T01:21:38+0000\"}],\"count\":1}},{\"id\":\"90790343096_10150282180723097\",\"type\":\"status\",\"created_time\":\"2011-08-25T01:01:54+0000\",\"updated_time\":\"2011-08-25T01:01:54+0000\",\"from\":{\"name\":\"Leila Desiree Curry\",\"id\":\"629284591\"},\"message\":\"I love the TimBits from Tim Hortons!! :)))\n\",\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282174113097\",\"type\":\"status\",\"created_time\":\"2011-08-25T00:51:05+0000\",\"updated_time\":\"2011-08-25T00:51:05+0000\",\"from\":{\"name\":\"Angel Bakhuyzen\",\"id\":\"613806511\"},\"message\":\"please bring back the dutchie\r\n\",\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282165723097\",\"type\":\"status\",\"created_time\":\"2011-08-25T00:34:28+0000\",\"updated_time\":\"2011-08-25T00:34:28+0000\",\"from\":{\"name\":\"Tim McDougall\",\"id\":\"725866065\"},\"message\":\"I want a Toasted coconut crunch donut so baddddd...not every store carries it. \",\"comments\":{\"count\":0}}}"
				);
		xml = new XmlBuffer();
		new JsonXmlTransformer().setUseAttributes(false).transform(json, xml);
		result = xml.toXml();
		System.out.println(result);
		
		JsonReader toker = new JsonReader(new StringReader("{\"data\":["+
			"{\"id\":\"90790343096_237110609699434\",\"type\":\"link\",\"created_time\":\"2012-01-20T06:14:08+0000\",\"updated_time\":\"2012-01-20T06:14:08+0000\",\"from\":{\"name\":\"Fazila Ibrahimovic\",\"id\":\"100001243024418\"},\"message\":\"CLICK HERE TO SEE WHO IS STALKING YOU: http:\\/\\/bit.ly\\/wAROhF\",\"comments\":{\"count\":0}},"+
			"{\"id\":\"90790343096_10150505755333097\",\"type\":\"status\",\"created_time\":\"2012-01-20T05:55:21+0000\",\"updated_time\":\"2012-01-20T05:55:21+0000\",\"from\":{\"name\":\"Jason Mann\",\"id\":\"688375060\"},\"message\":\"Tim Hortons has become part of our and my culture! 7 days per week and very much appreciate what Tim's does for community sports!. One question, pet peeve of mine and I saw another fast f. coffee place discontinued reusable cups due to contamination. I have seen people bring in cups for refill and person at counter handles fills then continues handling other cups... will you guys also discontinue or ?\",\"comments\":{\"count\":0}}"+
			"]}"));
		if (toker.enterObject() && toker.findItem("data")) {
			if (!toker.enterArray()) return ;
			while (toker.enterObject()) {
				while (toker.findItemNext()) {
					System.out.println(toker.getName() +"="+ toker.getValueString());
				}
				toker.leaveObject();
			}
			toker.leaveArray();
			toker.leaveObject();
		}

	}
}
