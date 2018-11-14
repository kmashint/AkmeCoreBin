package akme.core.test;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import akme.core.util.ArrayUtil;

public class ArrayUtilTest extends TestCase {

	public static void main(String[] args) {
		TestRunner.run(ArrayUtilTest.class);
	}
	
	public ArrayUtilTest(String name) {
		super(name);
	}
	
	public void testHex() {
		//System.err.println(ArrayUtil.fromUnsignedHex("0f".toCharArray())[0]);
		assertEquals( "00", String.valueOf(ArrayUtil.toUnsignedHex(new byte[]{(byte)0x00})) );
		assertEquals( "0f", String.valueOf(ArrayUtil.toUnsignedHex(new byte[]{(byte)0x0f})) );
		assertEquals( "f0", String.valueOf(ArrayUtil.toUnsignedHex(new byte[]{(byte)0xf0})) );
		assertEquals( "ff", String.valueOf(ArrayUtil.toUnsignedHex(new byte[]{(byte)0xff})) );

		assertEquals( "00", String.valueOf(ArrayUtil.toUnsignedHex(ArrayUtil.toBytesFromUnsignedHex("00".toCharArray()))) );
		assertEquals( "0f", String.valueOf(ArrayUtil.toUnsignedHex(ArrayUtil.toBytesFromUnsignedHex("0f".toCharArray()))) );
		assertEquals( "f0", String.valueOf(ArrayUtil.toUnsignedHex(ArrayUtil.toBytesFromUnsignedHex("f0".toCharArray()))) );
		assertEquals( "ff", String.valueOf(ArrayUtil.toUnsignedHex(ArrayUtil.toBytesFromUnsignedHex("ff".toCharArray()))) );
		
		String str;
		byte[] buf;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			str = "This store has had {0} complaints over the past {1} days compared to its district average of {2}.  This results in a score of {3} in the complaints category.";
			buf = md.digest(str.getBytes());
			//System.err.println(String.valueOf(ArrayUtil.toUnsignedHex(buf)));
			assertEquals("52e5b77a6460ce502bbf07e510400988",String.valueOf(ArrayUtil.toUnsignedHex(buf)));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			fail(e.toString());
		}
		
	}
	
	public void testByteArray() {
		assertEquals( "01020304", String.valueOf(ArrayUtil.toUnsignedHex( ArrayUtil.toBytes(0x01020304) )) );
		assertEquals( "fffefdfc", String.valueOf(ArrayUtil.toUnsignedHex( ArrayUtil.toBytes(0xfffefdfc) )) );
		
		assertEquals( 0x01020304, ArrayUtil.toInt( ArrayUtil.toBytes(0x01020304), 0) );
		assertEquals( 0xfffefdfc, ArrayUtil.toInt( ArrayUtil.toBytes(0xfffefdfc), 0) );
		assertEquals( 0x0102030405060708L, ArrayUtil.toLong( ArrayUtil.toBytes(0x0102030405060708L), 0) );
		assertEquals( 0xfffefdfcfbfaf9f8L, ArrayUtil.toLong( ArrayUtil.toBytes(0xfffefdfcfbfaf9f8L), 0) );
	}
	
	public void testToArray() {
		try {
			String[] str = {"a","b","c"};
			assertTrue("Should go from array/list and back.", Arrays.equals(str,ArrayUtil.toStringArray(ArrayUtil.toArrayList(str))) );
			Number[] num = {new Integer(1), new Integer(2), new Integer(3)};
			assertTrue("Should go from array/list and back.", Arrays.equals(num,ArrayUtil.toNumberArray(ArrayUtil.toArrayList(num))) );
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	
	public void testAdd() {
		try {
			String[] str = {"a","b","c"};
			assertNull("Should be null.", ArrayUtil.addOne(null, null));
			assertNull("Should be null.", ArrayUtil.addAll(null, null));
			assertTrue("Should add one to the list.", Arrays.equals(str, ArrayUtil.addOne(new String[] {"a","b"}, "c")) );
			assertTrue("Should add two to the list.", Arrays.equals(str, ArrayUtil.addAll(new String[] {"a"}, new String[] {"b","c"})) );
			assertTrue("Should add one to the list.", Arrays.equals(new String[] {"a"}, ArrayUtil.addOne(null, "a")) );
			assertTrue("Should add three to the list.", Arrays.equals(str, ArrayUtil.addAll(null, new String[] {"a","b","c"})) );
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	
}
