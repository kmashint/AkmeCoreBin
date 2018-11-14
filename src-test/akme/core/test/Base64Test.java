package akme.core.test;

import java.io.IOException;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import akme.core.io.Base64UrlUtil;
import akme.core.io.Base64Util;
import akme.core.io.ByteBufferFastOutputStream;

public class Base64Test extends TestCase {

	public static void main(String[] args) {
		TestRunner.run(Base64Test.class);
	}
	
	public Base64Test(String name) {
		super(name);
	}
	
	public String encodeDecode64(String value) throws IOException { 
//System.out.println(value +" : "+ Base64Util.encodeBase64( value.getBytes() ));
		ByteBufferFastOutputStream result = new ByteBufferFastOutputStream(4);
		result.write( Base64Util.decodeBase64( Base64Util.encodeBase64( value.getBytes() ) ) );
		return result.getInternalBuffer().toString();
	}
	
	public String encodeDecode64Url(String value) throws IOException {
//System.out.println(value +" : "+ Base64Util.encodeBase64( value.getBytes() ));
		ByteBufferFastOutputStream result = new ByteBufferFastOutputStream(4);
		result.write( Base64UrlUtil.decodeBase64( Base64UrlUtil.encodeBase64( value.getBytes() ) ) );
		return result.getInternalBuffer().toString();
	}
	
	public void testBase64() {
		String str = null;
		try {
			str = "012345";
			assertEquals(str, encodeDecode64(str));
			str = "0123456";
			assertEquals(str, encodeDecode64(str));
			str = "012345+/";
			assertEquals(str, encodeDecode64(str));
			str = "012345-_";
			assertEquals(str, encodeDecode64(str));
			str = "012345+/-_";
			assertEquals(str, encodeDecode64(str));
		}
		catch (Exception ex) {
			System.err.println(str +" : "+ Base64UrlUtil.encodeBase64( str.getBytes() ));
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}

	/**
	 * 	// base 64 encode the binary signature
	    String signature = Base64Util.encodeBase64(sigBytes);
	    
	    // convert the signature to 'web safe' base 64
	    signature = signature.replace('+', '-');
	    signature = signature.replace('/', '_');
	 */
	public void testBase64Url() {
		String str = null;
		try {
			str = "012345";
			assertEquals(str, encodeDecode64Url(str));
			str = "0123456";
			assertEquals(str, encodeDecode64Url(str));
			str = "012345+/";
			assertEquals(str, encodeDecode64Url(str));
			str = "012345-_";
			assertEquals(str, encodeDecode64Url(str));
			str = "012345+/-_";
			assertEquals(str, encodeDecode64Url(str));
		}
		catch (Exception ex) {
			System.err.println(str +" : "+ Base64UrlUtil.encodeBase64( str.getBytes() ));
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	
	public void testRandom64Url() {
		String str = null;
		try {
			for (int i=0; i<1000; i++) {
				int len = (int) Math.floor(Math.random()*1024.0*8);
				char[] buf = new char[len];
				for (int j=0; j<len; j++) buf[j] = (char) Math.floor(Math.random()*128.0);
				str = String.valueOf(buf);
				assertEquals(str, encodeDecode64Url(str));
			}
		}
		catch (Exception ex) {
			System.err.println(str +" : "+ Base64UrlUtil.encodeBase64( str.getBytes() ));
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}

}