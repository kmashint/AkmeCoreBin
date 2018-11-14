package akme.core.test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import akme.core.io.ByteBufferOutputStream;
import akme.core.io.StreamUtil;
import akme.core.util.ExceptionUtil;
import junit.framework.TestCase;
import junit.textui.TestRunner;

public class ByteBufferTest extends TestCase {

	static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
	static final Charset UTF_8 = Charset.forName("UTF-8");

	public static void main(String[] args) {
		TestRunner.run(ByteBufferTest.class);
	}
	
	public void test1() {
		final int size = 512;
		final byte[] bb = new byte[size];
		Arrays.fill(bb, (byte)'!'); // ! is ASCII 33
		
		for (int i=0; i<2; i++) {
			ByteBufferOutputStream bous = new ByteBufferOutputStream(size, i!=0);
			try {
				assertEquals("initial capacity", size, bous.buffer().capacity());
				assertEquals("initial limit", size, bous.buffer().limit());
				assertEquals("initial position", 0, bous.buffer().position());
				
				bous.write(bb);
				assertEquals("write1 limit", size, bous.buffer().limit());
				assertEquals("write1 position", size, bous.buffer().position());
				
				bous.write(bb, 0, 1+i);
				assertEquals("write2 capacity", size<<1, bous.buffer().capacity());
				assertEquals("write2 limit", size<<1, bous.buffer().limit());
				assertEquals("write2 position", size+1+i, bous.buffer().position());
				
				bous.buffer().limit(3);
				assertEquals("!!!", bous.toString(ISO_8859_1));
				assertEquals("!!!", bous.toString(UTF_8));

			}
			catch (IOException ex) {
				fail(ExceptionUtil.getShortStackTraceAndCause(ex));
			}
			finally {
				StreamUtil.closeQuiet(bous);
			}
		}
		
	}
	
	
}
