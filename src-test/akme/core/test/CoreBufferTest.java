package akme.core.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;
import akme.core.io.ByteBufferFast;
import akme.core.io.StringBufferFast;
import akme.core.io.StringInputStream;

/**
 * Test of Byte and String buffers.
 * 
 * @author kmashint
 */
public class CoreBufferTest extends TestCase {
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(CoreBufferTest.class);
	}
	
	private byte[] BYTE_ARY = new byte[] {'a','b','c','d'};
	
	//private byte[] NEWLINE_ARY = new byte[] {'\n'};
	
	public CoreBufferTest(String name) {
		super(name);
	}
	
	public void setUp() throws Exception {
		super.setUp();
	}

	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	private void testByteBuffer(int size) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(size); 
		ByteBufferFast buf;
		ByteArrayInputStream ins;
		ins = new ByteArrayInputStream(BYTE_ARY);
		buf = new ByteBufferFast(size);
		for (int n=buf.readFrom(ins); n != -1; n=buf.readFrom(ins)) {
			buf.writeToAndReset(bos);
		}
		assertEquals("abcd", bos.toString());
	}

	public void testByteBuffer() {
		//byte[] result;
		try {
			testByteBuffer(5);
			testByteBuffer(4);
			testByteBuffer(3);
			testByteBuffer(2);
			testByteBuffer(1);
			testByteBuffer(0);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	protected void doByteBufferFile(String name, String inFile, String outFile) {
		try {
			File inf = new File(inFile);
			FileInputStream ins = new FileInputStream(inf);
			File ouf = new File(outFile);
			FileOutputStream ous = new FileOutputStream(ouf);
			ByteBufferFast buf = new ByteBufferFast(1024 * 8);
			for (int n=buf.readFrom(ins); n != -1; n=buf.readFrom(ins)) {
				buf.writeToAndReset(ous);
			}
			ous.close();
			assertEquals(name +" file length should match", inf.length(), ouf.length());
			ouf.delete();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.toString());
		}
	}
	
	public void testByteBufferFile() {
		doByteBufferFile("shell32.dll","C:\\WINDOWS\\system32\\shell32.dll","C:\\temp\\shell32.dll.bin");
		doByteBufferFile("mshtml.dll","C:\\WINDOWS\\system32\\mshtml.dll","C:\\temp\\mshtml.dll.bin");
		doByteBufferFile("ole32.dll","C:\\WINDOWS\\system32\\ole32.dll","C:\\temp\\ole32.dll.bin");
		doByteBufferFile("oleaut32.dll","C:\\WINDOWS\\system32\\oleaut32.dll","C:\\temp\\oleaut32.dll.bin");
	}
	
	public void testStringInputStream() {
		try {
			StringInputStream ins;
			StringBufferFast result;
			ins = new StringInputStream("hello");
			result = new StringBufferFast();
			for (int n=ins.read(); n != -1; n=ins.read()) {
				result.append((char)n);
			}
			assertEquals("say hello", "hello", result.toString());
			
			ins = new StringInputStream("hello,hello,hello",5);
			result = new StringBufferFast();
			byte[] bb = new byte[20];
			for (int n=ins.read(bb); n != -1; n=ins.read(bb)) {
				for (int i=0; i<n; i++) {
					result.append((char)bb[i]);
				}
			}
			assertEquals("say hello,hello,hello", "hello,hello,hello", result.toString());

			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			Reader rdr = new InputStreamReader(cl.getResourceAsStream("META-INF/MANIFEST.MF"));
			char[] cc = new char[1024]; 
			result = new StringBufferFast();
			for (int n=rdr.read(cc,0,cc.length); n != -1; n=rdr.read(cc,0,cc.length)) {
				result.append(cc,0,n);
			}
			String inputStr = result.toString();
			ins = new StringInputStream(inputStr,5);
			result.reset();
			int c;
			while ((c=ins.read()) != -1) {
				result.append((char)c);
			}
			assertEquals("stream a file with read()", inputStr, result.toString());
			//System.out.println(result);
			
			ins = new StringInputStream(result.toString(),5);
			result.reset();
			bb = new byte[15];
			for (int n=ins.read(bb,0,bb.length); n != -1; n=ins.read(bb,0,bb.length)) {
				for (int i=0; i<n; i++) {
					result.append((char)bb[i]);
				}
			}
			assertEquals("stream a file with read(byte[],int,int)", inputStr, result.toString());
			//System.out.println(result);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.toString());
		}
	}

	public void testStringBuffer() {
		
	}

}
