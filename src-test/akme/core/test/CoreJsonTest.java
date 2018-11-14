package akme.core.test;

import java.io.CharArrayWriter;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import akme.core.io.JsonBuffer;
import akme.core.io.JsonReader;
import akme.core.io.JsonTokenizer;
import akme.core.io.JsonWriter;
import akme.core.util.StringUtil;

/** 
 * JUnit TestCase for XML classes.
 * 
 * @author akme.org
 * @author $Author: Keith.mashinter $ 
 * @version $Date: 10/17/03 2:17a $ 
 * $NoKeywords: $ 
 */
public class CoreJsonTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(CoreJsonTest.class);
	}
	
	public CoreJsonTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * TODO: What about something that could be an Object/Map {} or Array []?  isNextObject? isNextArray?
	 * TODO: What about a null Array, e.g. myAry : null?
	 */
	public void test1data() {
		StringBuilder result = new StringBuilder("{\"data\":["+
			"{\"id\":\"90790343096_10150251767018097\",\"type\":\"status\",\"created_time\":\"2011-07-18T01:09:18+0000\",\"updated_time\":\"2011-07-18T01:09:18+0000\",\"from\":{\"name\":\"Mary Nicol\",\"id\":\"100001076589689\"},\"message\":\"I LOVE THIS COFFEE!! The Best!!\"},"+
			"{\"id\":\"90790343096_10150251749698097\",\"type\":\"status\",\"created_time\":\"2011-07-18T00:40:20+0000\",\"updated_time\":\"2011-07-18T00:40:20+0000\",\"from\":{\"name\":\"Kelly STuart\",\"id\":\"594744178\"},\"message\":\"I wish more Tim horton off the ice cream, us in the burbs do without\r\n\"},"+
			"{\"id\":\"90790343096_10150266054078097\",\"type\":\"status\",\"created_time\":\"2011-08-05T01:55:15+0000\",\"updated_time\":\"2011-08-05T01:55:15+0000\",\"from\":{\"name\":\"Johanne Grenier Lapointe\",\"id\":\"520923011\"},\"message\":\"Jadore mon caf\u00e9 Tim Horton\"}"+
			"]}");
		// This depends on correct ordering of fields.
		// id,type,created_time,updated_time
		JsonTokenizer toker = new JsonTokenizer(result.toString());
		Object[] row = new Object[5];
		if (toker.enterObject() && toker.findItem("data") && toker.enterArray()) while (toker.enterObject()) {
			//out.println("<br/>"+ toker.getNestChar() + toker.getNestLevel() + toker.getItemName());
			toker.findItem("id");
			row[0] = toker.getValueString();
			toker.findItem("type");
			row[1] = toker.getValueString();
			toker.findItem("created_time");
			row[2] = toker.getValueDate();
			toker.findItem("updated_time");
			row[3] = toker.getValueDate();
			//toker.findItem("from");
			//toker.findItemNext(); toker.enterObject(); toker.leaveObject();
			assertTrue(toker.findItem("message"));
			row[4] = toker.getValueString();
			System.out.println("<br/>"+ StringUtil.joinString(row, '\t'));
			toker.leaveObject();
			//System.out.println("<br/>"+ toker.getJson().substring(toker.getIndex()));
		}
	}
	
	public void test2data() {
		StringBuilder result = new StringBuilder(
			"{\"data\":[{\"id\":\"90790343096_10150282490948097\",\"type\":\"status\",\"created_time\":\"2011-08-25T11:55:50+0000\",\"updated_time\":\"2011-08-25T11:55:50+0000\",\"from\":{\"name\":\"Anke D\u00f6ring\",\"id\":\"100000074458544\"},\"message\":\"best Kaffee wo gibt, greetings from Germany :))))\",\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282456933097\",\"type\":\"status\",\"created_time\":\"2011-08-25T10:45:19+0000\",\"updated_time\":\"2011-08-25T10:45:19+0000\",\"from\":{\"name\":\"Ivan Lino Silva\",\"id\":\"505545013\"},\"message\":\"Home of the super addictive coffee! :)\",\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282410243097\",\"type\":\"status\",\"created_time\":\"2011-08-25T08:32:48+0000\",\"updated_time\":\"2011-08-25T08:32:48+0000\",\"from\":{\"name\":\"Tammy Thomas-Gaudreault\",\"id\":\"522730223\"},\"message\":\"Tim Horton's, i have been to several tim horton's across quebec and I've noticed something, none of the coffees taste the same.. some tim hortons have better coffee then others, yet i remember 10 years ago you couldnt tell a different but lately there are big differences from one tim to the next. \",\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282289413097\",\"type\":\"status\",\"created_time\":\"2011-08-25T03:47:14+0000\",\"updated_time\":\"2011-08-25T03:47:14+0000\",\"from\":{\"name\":\"Alanna Landymore\",\"id\":\"539192993\"},\"message\":\"Dear Timmy....my Dr. said I have to break up with you right away. I am definitely not impressed and will still sneak over \",\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282281728097\",\"type\":\"status\",\"created_time\":\"2011-08-25T03:31:32+0000\",\"updated_time\":\"2011-08-25T03:31:32+0000\",\"from\":{\"name\":\"John Russell\",\"id\":\"1417666178\"},\"message\":\"Dear Tim Hortons....you guys need to expand into the greater Kansas City area....that would be really awesome...and then everyone here can see for themselves how amazing you guys are....best coffee shop ever....seriously.\",\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282278173097\",\"type\":\"status\",\"created_time\":\"2011-08-25T03:24:55+0000\",\"updated_time\":\"2011-08-25T03:24:55+0000\",\"from\":{\"name\":\"Meryaam Marokiina\",\"id\":\"100001050667536\"},\"message\":\"Vive les timbits :)\n\",\"likes\":{\"data\":[{\"name\":\"Omaima Jouti\",\"id\":\"573957381\"}],\"count\":1},\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282269398097\",\"type\":\"link\",\"created_time\":\"2011-08-25T03:08:55+0000\",\"updated_time\":\"2011-08-25T03:08:55+0000\",\"from\":{\"name\":\"DobbernationLOVES\",\"category\":\"Personal blog\",\"id\":\"219117594774467\"},\"message\":\"Tim Hortons breakfast http://wp.me/pGU5A-5YD\",\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282190378097\",\"type\":\"status\",\"created_time\":\"2011-08-25T01:20:09+0000\",\"updated_time\":\"2011-08-25T01:21:38+0000\",\"from\":{\"name\":\"Anne Hartmann\",\"id\":\"100001056694602\"},\"message\":\"thank you for the delicious cinnomon roll, the banana nut muffin and the great oatmeal! :D i wish we would have tim hortons in germany :) \",\"comments\":{\"data\":[{\"id\":\"90790343096_10150282190378097_18207899\",\"from\":{\"name\":\"Anne Hartmann\",\"id\":\"100001056694602\"},\"message\":\"i forgot to say that tim hortons is thousand times better than starbucks :)\",\"created_time\":\"2011-08-25T01:21:38+0000\"}],\"count\":1}},{\"id\":\"90790343096_10150282180723097\",\"type\":\"status\",\"created_time\":\"2011-08-25T01:01:54+0000\",\"updated_time\":\"2011-08-25T01:01:54+0000\",\"from\":{\"name\":\"Leila Desiree Curry\",\"id\":\"629284591\"},\"message\":\"I love the TimBits from Tim Hortons!! :)))\n\",\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282174113097\",\"type\":\"status\",\"created_time\":\"2011-08-25T00:51:05+0000\",\"updated_time\":\"2011-08-25T00:51:05+0000\",\"from\":{\"name\":\"Angel Bakhuyzen\",\"id\":\"613806511\"},\"message\":\"please bring back the dutchie\r\n\",\"comments\":{\"count\":0}},{\"id\":\"90790343096_10150282165723097\",\"type\":\"status\",\"created_time\":\"2011-08-25T00:34:28+0000\",\"updated_time\":\"2011-08-25T00:34:28+0000\",\"from\":{\"name\":\"Tim McDougall\",\"id\":\"725866065\"},\"message\":\"I want a Toasted coconut crunch donut so baddddd...not every store carries it. \",\"comments\":{\"count\":0}}}"
			);
		JsonTokenizer toker = new JsonTokenizer(result.toString());
		List<String> fromNames = Arrays.asList(
				"Anke D\u00f6ring", 
				"Ivan Lino Silva",
				"Tammy Thomas-Gaudreault",
				"Alanna Landymore",
				"John Russell",
				"Meryaam Marokiina",
				"DobbernationLOVES",
				"Anne Hartmann",
				"Leila Desiree Curry",
				"Angel Bakhuyzen");
		Iterator<String> it = fromNames.iterator();
		if (toker.enterObject() && toker.findItem("data") && toker.enterArray()) while (toker.enterObject()) {
			//out.println("<br/>"+ toker.getNestChar() + toker.getNestLevel() + toker.getItemName());
System.out.println(result.substring(toker.getIndex(), toker.getIndex()+99));
			if (toker.findItem("id")) toker.getValueString();
			if (toker.findItem("type")) toker.getValueString();
			if (toker.findItem("created_time")) toker.getValueDate();
			if (toker.findItem("updated_time")) toker.getValueDate();
			String name = null;
			if (toker.findItem("from")) {
				// Need to allow for different ordering of fields, so use findItemNext.
				toker.enterObject();
				while (toker.findItemNext()) {
					if ("id".equals(toker.getName())) toker.getValueString();
					if ("name".equals(toker.getName())) name = toker.getValueString();
				}
				toker.leaveObject();
			}
			if (it.hasNext()) assertEquals(it.next(), name);
			if (toker.findItem("message")) toker.getValueString();
			toker.findItemNext();
			if ("likes".equals(toker.getName())) {
				toker.enterObject();
				if (toker.findItem("data") && toker.enterArray()) {
					while (toker.enterObject()) {
						while (toker.findItemNext()) {
							if ("id".equals(toker.getName())) toker.getValueString();
							if ("name".equals(toker.getName())) toker.getValueString();
						}
						toker.leaveObject();
					}
					toker.leaveArray();
				}
				toker.leaveObject();
				toker.findItemNext();
			}
			if ("comments".equals(toker.getName())) {
				toker.enterObject();
				if (toker.findItem("data") && toker.enterArray()) {
					while (toker.enterObject()) {
						while (toker.findItemNext()) {
							if ("id".equals(toker.getName())) toker.getValueString();
							if ("name".equals(toker.getName())) toker.getValueString();
							if ("created_datetime".equals(toker.getName())) toker.getValueDate();
							if ("message".equals(toker.getName())) toker.getValueString();
						}
						toker.leaveObject();
					}
					toker.leaveArray();
				}
				toker.leaveObject();
			}
			//out.println("<br/>"+ String.valueOf(post));
			toker.leaveObject();
		}
	}

	public void test3MapAryVal() {
		StringBuilder result = new StringBuilder("{ x : [1,2], y : 3 }");
		JsonTokenizer toker = new JsonTokenizer(result.toString());
		
		// With proper enter/leave.
		toker.enterObject();
		assertTrue(toker.findItemNext());
		//toker.findItem("x");
		assertEquals("x", toker.getName());
		toker.enterArray();
		assertEquals(1, toker.getValueInteger());
		assertEquals(2, toker.getValueInteger());
		toker.leaveArray();
		toker.findItem("y");
		assertEquals(3, toker.getValueInteger());
		assertFalse(toker.findItemNext());
		assertEquals(1, toker.getNestLevel());
		toker.leaveObject();
		assertEquals(0, toker.getNestLevel());
		
		JsonReader jr = new JsonReader(new StringReader(result.toString()));
		// With proper enter/leave.
		jr.enterObject();
		assertTrue(jr.findItemNext());
		//toker.findItem("x");
		assertEquals("x", jr.getName());
		jr.enterArray();
		assertEquals(1, jr.getValueInteger());
		assertEquals(2, jr.getValueInteger());
		jr.leaveArray();
		jr.findItem("y");
		assertEquals(3, jr.getValueInteger());
		assertFalse(jr.findItemNext());
		assertEquals(1, jr.getNestLevel());
		jr.leaveObject();
		assertEquals(0, jr.getNestLevel());
		
	}
	
	public void test4Buffer() {
		JsonBuffer bf = new JsonBuffer();
		String result;
		bf.openArray();
		for (int i=0; i<2; i++) {
			bf.openObject();
			bf.addItem("cd", "1234");
			bf.addItem("name", "Joe Smith");
			bf.openObject("likes");
			bf.addItem("count", 0);
			
			bf.openArray("data");
			bf.closeLevel();
			
			bf.closeLevel();
			bf.closeLevel();
		}
		bf.closeLevel();
		assertEquals(
			"[{\"cd\":\"1234\",\"name\":\"Joe Smith\",\"likes\":{\"count\":0,\"data\":[]}},{\"cd\":\"1234\",\"name\":\"Joe Smith\",\"likes\":{\"count\":0,\"data\":[]}}]",
			bf.toJson());
		//System.out.println(bf.toJson());
		
		JsonBuffer jw = new JsonBuffer();
		jw.openObject();
		jw.addItem("x", 1);
		jw.addItem("y", "t\\wo");
		jw.addArray("z", new Object[] {3,4,5});
		jw.closeLevel(); 
		result = jw.getBuffer().toString();
		assertEquals("{\"x\":1,\"y\":\"t\\\\wo\",\"z\":[3,4,5]}", result);
		
		Object[] ary = new Object[] {1,2,3};
		jw = new JsonBuffer();
		jw.openArray();
		jw.addArray(ary);
		jw.closeLevel();
		result = jw.getBuffer().toString();
		assertEquals("[[1,2,3]]", result);

		jw = new JsonBuffer();
		jw.openArray();
		jw.addArray(ary);
		jw.openArray();
		jw.addItem(4);
		jw.addItem(5);
		jw.addItem(6);
		jw.closeLevel();
		jw.closeLevel();
		result = jw.getBuffer().toString();
		assertEquals("[[1,2,3],[4,5,6]]", result);

	}
	
	public void test5Writer() {
		JsonWriter jw = new JsonWriter();
		Object[] ary = new Object[] {1,2,3};
		String result;
		
		jw.openObject();
		jw.addItem("x", 1);
		jw.addItem("y", "t\\wo");
		jw.addArray("z", ary);
		jw.closeLevel();
		result = jw.getWriter().toString();
		assertEquals("{\"x\":1,\"y\":\"t\\\\wo\",\"z\":[1,2,3]}", result);

		jw = new JsonWriter();
		jw.openArray();
		jw.addArray(ary);
		jw.closeLevel();
		result = jw.getWriter().toString();
		assertEquals("[[1,2,3]]", result);

		jw = new JsonWriter();
		jw.openArray();
		jw.addArray(ary);
		jw.openArray();
		jw.addItem(4);
		jw.addItem(5);
		jw.addItem(6);
		jw.closeLevel();
		jw.closeLevel();
		result = jw.getWriter().toString();
		assertEquals("[[1,2,3],[4,5,6]]", result);
	}

}
