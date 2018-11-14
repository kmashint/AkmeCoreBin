package akme.mobile.test;

import junit.framework.TestCase;
import akme.mobile.util.MathUtil;

public class MathUtilTest extends TestCase {
	
	private static final double HALF_MAX_DOUBLE = Double.MAX_VALUE/2.0D;

	private int iterations = 1000000;

	public static void main(String[] args) {
		junit.textui.TestRunner.run(MathUtilTest.class);
	}
	
	public void test1() {
		double a, b;

		a = 0.0; b = 0.0;
		System.out.println("exp("+ a +"): "+ Math.exp(a) +" "+ MathUtil.exp(a));
		System.out.println("log("+ a +"): "+ Math.log(a) +" "+ MathUtil.log(a));
		System.out.println("log10("+ a +"): "+ Math.log10(a) +" "+ MathUtil.log10(a));
		System.out.println("pow("+ a +","+ b +"): "+ Math.pow(a,b) +" "+ MathUtil.pow(a,b));
		System.out.println("acos("+ a +"): "+ Math.acos(a) +" "+ MathUtil.acos(a));
		System.out.println("asin("+ a +"): "+ Math.asin(a) +" "+ MathUtil.asin(a));
		assertEquals("log10("+ a +")", Double.valueOf(Math.log10(a)), Double.valueOf(MathUtil.log10(a)));
		assertEquals("pow("+ a +","+ b +")", Double.valueOf(Math.pow(a,b)), Double.valueOf(MathUtil.pow(a,b)));
		assertEquals("atan2("+ a +","+ b +")", Double.valueOf(Math.atan2(a,b)), Double.valueOf(MathUtil.atan2(a,b)));

		a = 1.0; b = 2.0;
		System.out.println("exp("+ a +"): "+ Math.exp(a) +" "+ MathUtil.exp(a));
		System.out.println("log("+ a +"): "+ Math.log(a) +" "+ MathUtil.log(a));
		System.out.println("log10("+ a +"): "+ Math.log10(a) +" "+ MathUtil.log10(a));
		System.out.println("pow("+ a +","+ b +"): "+ Math.pow(a,b) +" "+ MathUtil.pow(a,b));
		System.out.println("acos("+ a +"): "+ Math.acos(a) +" "+ MathUtil.acos(a));
		System.out.println("asin("+ a +"): "+ Math.asin(a) +" "+ MathUtil.asin(a));
		System.out.println("atan2("+ a +","+ b +"): "+ Math.atan2(a,b) +" "+ MathUtil.atan2(a,b));
		assertEquals("log10("+ a +")", Double.valueOf(Math.log10(a)), Double.valueOf(MathUtil.log10(a)));
		assertEquals("pow("+ a +","+ b +")", Double.valueOf(Math.pow(a,b)), Double.valueOf(MathUtil.pow(a,b)));
		assertEquals("atan2("+ a +","+ b +")", Double.valueOf(Math.atan2(a,b)), Double.valueOf(MathUtil.atan2(a,b)));

		a = 2.0; b = 2.0;
		System.out.println("exp("+ a +"): "+ Math.exp(a) +" "+ MathUtil.exp(a));
		System.out.println("log("+ a +"): "+ Math.log(a) +" "+ MathUtil.log(a));
		System.out.println("log10("+ a +"): "+ Math.log10(a) +" "+ MathUtil.log10(a));
		System.out.println("pow("+ a +","+ b +"): "+ Math.pow(a,b) +" "+ MathUtil.pow(a,b));
		System.out.println("acos("+ a +"): "+ Math.acos(a) +" "+ MathUtil.acos(a));
		System.out.println("asin("+ a +"): "+ Math.asin(a) +" "+ MathUtil.asin(a));
		System.out.println("atan2("+ a +","+ b +"): "+ Math.atan2(a,b) +" "+ MathUtil.atan2(a,b));
		// log10(2.0D) is close enough with rounding.  The real method must handle base 2 separately. 
		assertEquals("log10("+ a +")", Math.log10(a), MathUtil.log10(a), 0.0000000000000001D);
		assertEquals("pow("+ a +","+ b +")", Double.valueOf(Math.pow(a,b)), Double.valueOf(MathUtil.pow(a,b)));
		assertEquals("atan2("+ a +","+ b +")", Double.valueOf(Math.atan2(a,b)), Double.valueOf(MathUtil.atan2(a,b)));
		
		a = 10.0; b = 3.0;
		System.out.println("exp("+ a +"): "+ Math.exp(a) +" "+ MathUtil.exp(a));
		System.out.println("log("+ a +"): "+ Math.log(a) +" "+ MathUtil.log(a));
		System.out.println("log10("+ a +"): "+ Math.log10(a) +" "+ MathUtil.log10(a));
		System.out.println("pow("+ a +","+ b +"): "+ Math.pow(a,b) +" "+ MathUtil.pow(a,b));
		System.out.println("atan2("+ a +","+ b +"): "+ Math.atan2(a,b) +" "+ MathUtil.atan2(a,b));
		assertEquals("log10("+ a +")", Double.valueOf(Math.log10(a)), Double.valueOf(MathUtil.log10(a)));
		assertEquals("pow("+ a +","+ b +")", Double.valueOf(Math.pow(a,b)), Double.valueOf(MathUtil.pow(a,b)));
		assertEquals("atan2("+ a +","+ b +")", Double.valueOf(Math.atan2(a,b)), Double.valueOf(MathUtil.atan2(a,b)));
		
		a = -2.0; b = 3.0;
		System.out.println("exp("+ a +"): "+ MathUtil.exp(a));
		System.out.println("log("+ a +"): "+ MathUtil.log(a));
		System.out.println("log10("+ a +"): "+ MathUtil.log10(a));
		System.out.println("pow("+ a +","+ b +"): "+ MathUtil.pow(a,b));
		System.out.println("atan2("+ a +","+ b +"): "+ MathUtil.atan2(a,b));
		assertEquals("log10("+ a +")", Double.valueOf(Math.log10(a)), Double.valueOf(MathUtil.log10(a)));
		assertEquals("pow("+ a +","+ b +")", Double.valueOf(Math.pow(a,b)), new Double(MathUtil.pow(a,b)));
		assertEquals("atan2("+ a +","+ b +")", Double.valueOf(Math.atan2(a,b)), new Double(MathUtil.atan2(a,b)));
	}
	
	public void test2() {
		double a, b;

		for (int i=0; i<iterations; i++) {
			a = ((Math.random() * HALF_MAX_DOUBLE) - HALF_MAX_DOUBLE) * 2.0D;
			b = ((Math.random() * HALF_MAX_DOUBLE) - HALF_MAX_DOUBLE) * 2.0D;
			assertEquals("exp ", Double.doubleToLongBits(Math.exp(a)), Double.doubleToLongBits(MathUtil.exp(a)));
			assertEquals("log ", Double.doubleToLongBits(Math.log(a)), Double.doubleToLongBits(MathUtil.log(a)));
			assertEquals("log10 ", Double.doubleToLongBits(Math.log10(a)), Double.doubleToLongBits(MathUtil.log10(a)));
			assertEquals("pow ", Double.doubleToLongBits(Math.pow(a,b)),  Double.doubleToLongBits(MathUtil.pow(a,b)));
			assertEquals("acos ", Double.doubleToLongBits(Math.acos(a)), Double.doubleToLongBits(MathUtil.acos(a)));
			assertEquals("asin ", Double.doubleToLongBits(Math.asin(a)), Double.doubleToLongBits(MathUtil.asin(a)));
			assertEquals("atan2 ", Double.doubleToLongBits(Math.atan2(a,b)),  Double.doubleToLongBits(MathUtil.atan2(a,b)));
		}
	}
	
	public void test3() {
		double a, b;
		long t0;
		
		t0 = System.currentTimeMillis();
		System.out.println("time "+ t0);
		for (int i=0; i<iterations; i++) {
			a = ((Math.random() * HALF_MAX_DOUBLE) - HALF_MAX_DOUBLE) * 2.0D;
			b = ((Math.random() * HALF_MAX_DOUBLE) - HALF_MAX_DOUBLE) * 2.0D;
			Math.log(a);
			Math.pow(a, b);
		}
		System.out.println("SE time "+ (System.currentTimeMillis()-t0));
		
		t0 = System.currentTimeMillis();
		for (int i=0; i<iterations; i++) {
			a = ((Math.random() * HALF_MAX_DOUBLE) - HALF_MAX_DOUBLE) * 2.0D;
			b = ((Math.random() * HALF_MAX_DOUBLE) - HALF_MAX_DOUBLE) * 2.0D;
			MathUtil.log(a);
			MathUtil.pow(a, b);
		}		
		System.out.println("ME time "+ (System.currentTimeMillis()-t0));
	}

}
