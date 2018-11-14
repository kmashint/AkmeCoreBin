package akme.core.test;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import akme.core.io.BufferedLineInputStream;

public class BufferedLineInputTest extends TestCase {

	public static void main(String[] args) {
		TestRunner.run(BufferedLineInputTest.class);
		timeTest1();
	}
	
	public static void timeTest1() {
		try {
			long t0 = System.currentTimeMillis();
			BufferedLineInputStream ins = new BufferedLineInputStream(new FileInputStream("C:/test.csv"),"ISO-8859-1");
			while (ins.readLineBytes() != null) ;

			long t1 = System.currentTimeMillis();
			BufferedReader rdr = new BufferedReader(new FileReader("C:/test.csv"));
			while (rdr.readLine() != null) ;

			long t2 = System.currentTimeMillis();
			System.out.println("t1.LineInputStream "+ (t1-t0) +" t2.LineReader "+ (t2 - t1));
		}
		catch (IOException ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}

	public BufferedLineInputTest(String name) {
		super(name);
	}
	
	private void checkEncoding(InputStream ins, String encoding) throws IOException {
		assertTrue(encoding + " should be supported.", BufferedLineInputStream.isEncodingSupported(encoding));
		BufferedLineInputStream bin = new BufferedLineInputStream(ins, encoding);
		assertEquals(encoding +" lines should work.", "x", bin.readLine());
		assertEquals(encoding +" lines should work.", "y", bin.readLine());
	}
 
	public void test1() {
		try {
			assertFalse("UTF-8 should NOT be supported.", BufferedLineInputStream.isEncodingSupported("UTF-8"));

			checkEncoding(new ByteArrayInputStream(new byte[] {'x','\n','y'}), ("ASCII"));
			checkEncoding(new ByteArrayInputStream(new byte[] {'x','\r','y'}), ("ASCII"));
			checkEncoding(new ByteArrayInputStream(new byte[] {'x','\r','\n','y'}), ("ASCII"));
			checkEncoding(new ByteArrayInputStream(new byte[] {'x','\n','y'}), ("ISO-8859-1"));
			checkEncoding(new ByteArrayInputStream(new byte[] {'x','\n','y'}), ("WINDOWS-1252"));
			
			BufferedLineInputStream bin;
			bin = new BufferedLineInputStream(new ByteArrayInputStream(new byte[] {'\n'}), "ASCII");
			assertEquals("Should read empty string for empty line.", "", bin.readLine());
			assertEquals("Should read null for end-of-file after a newline.", null, bin.readLine());
			
			try { 
				new BufferedLineInputStream(new ByteArrayInputStream(new byte[] {'\n'}), "UTF-8"); 
				fail("Should throw UnsupportedEncodingException."); 
			}
			catch (Exception ex) {}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	
}
