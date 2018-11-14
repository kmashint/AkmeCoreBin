package akme.core.test;

import java.util.HashMap;
import java.util.concurrent.Callable;

import junit.framework.TestCase;
import akme.core.util.Dependencies;
import akme.core.util.FinalDependencies;

/**
 * Test cases for FinalDependencies which provides set-once protection on Dependencies/Properties.
 */
public class FinalDependenciesTest extends TestCase {

	private Dependencies that;

	protected void setUp() throws Exception {
		that = new FinalDependencies();
	}

	@SuppressWarnings("serial")
	public void testWriteOnce() {
		
		that.put("x", 1);
		
		try { that.setProperty("x", "2"); fail("setProperty should throw"); }
		catch (IllegalArgumentException ex) { assertTrue(ex.getMessage().contains("already set")); }

		try { that.put("x", 2);	fail("put should throw"); }
		catch (IllegalArgumentException ex) { assertTrue(ex.getMessage().contains("already set")); }

		try { 
			that.putAll(new HashMap<Object,Object>(){{ put("x", 2); }}); 
			fail("putAll should throw");
		}
		catch (IllegalArgumentException ex) { assertTrue(ex.getMessage().contains("already set")); }

		try {
			that.putCallableWrapped("x", new Callable<Object>(){
				public Object call() { return null; }
			});	
			fail("putCallableWrapped should throw");
		}
		catch (IllegalArgumentException ex) { assertTrue(ex.getMessage().contains("already set")); }

		try { that.remove("x");	fail("remove should throw"); }
		catch (UnsupportedOperationException ex) { assertTrue(ex.getMessage().contains("not supported")); }

		try { that.clear(); fail("clear should throw"); }
		catch (UnsupportedOperationException ex) { assertTrue(ex.getMessage().contains("not supported")); }

	}

}
