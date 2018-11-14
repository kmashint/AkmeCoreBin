package akme.core.test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import junit.framework.TestCase;
import akme.core.util.DateUtil;

public class DateUtilTest extends TestCase {
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(DateUtilTest.class);
	}
	
	public DateUtilTest(String name) {
		super(name);
	}
	
	public void setUp() throws Exception {
		super.setUp();
	}

	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testDiff() {
		Calendar date1 = Calendar.getInstance();
		date1.clear();
		Calendar date2 = Calendar.getInstance();
		date2.clear();
		
		date1.set(2012,11-1,4, 0,1,2);
		date2.set(2012,11-1,5, 0,1,2);
		assertEquals("2012-11 DST fall back", 1, DateUtil.diffDays(date1, date2));
		date1.set(2012,11-1,4, 0,1,2);
		date2.set(2012,11-1,5, 11,1,1);
		assertEquals("2012-11 DST fall back", 1, DateUtil.diffDays(date1, date2));
		date1.set(2012,11-1,4, 0,1,2);
		date2.set(2012,11-1,5, 11,1,2);
		assertEquals("2012-11 DST fall back", 2, DateUtil.diffDays(date1, date2));
		
		date1.set(2012,6-1,30, 0,1,2);
		date2.set(2012,7-1,1, 0,1,2);
		assertEquals("2012-07-1 leap second back", 1, DateUtil.diffDays(date1, date2));
		date1.set(2012,6-1,30, 0,1,2);
		date2.set(2012,7-1,1, 12,1,1);
		assertEquals("2012-07-1 leap second back", 1, DateUtil.diffDays(date1, date2));
		date1.set(2012,6-1,30, 0,1,2);
		date2.set(2012,7-1,1, 12,1,2);
		assertEquals("2012-07-1 leap second back", 2, DateUtil.diffDays(date1, date2));
	}
	
	public void testFormatIsoDate() {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		
		cal.set(2001,2-1,3, 4,5,6);
		assertEquals("2001-02-03 04:05:06", DateUtil.formatDateTime(cal));
		
		cal.set(Calendar.MILLISECOND, 123);
		assertEquals("2001-02-03 04:05:06.123", DateUtil.formatDateMillis(cal));

		cal.set(Calendar.MILLISECOND, 23);
		assertEquals("2001-02-03 04:05:06.023", DateUtil.formatDateMillis(cal));

	}

	public void testParseIsoDate() {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		Calendar gmt;
		
		String calStr = "2001-01-03 04:05:06";
		DateUtil.parseIsoCalendar(calStr);
		assertEquals(cal, DateUtil.parseIsoCalendar(DateUtil.formatDateTime(cal)));

		cal.set(Calendar.MILLISECOND, 123);
		assertEquals(cal, DateUtil.parseIsoCalendar(DateUtil.formatDateMillis(cal)));

		cal.set(Calendar.MILLISECOND, 23);
		assertEquals(cal, DateUtil.parseIsoCalendar(DateUtil.formatDateMillis(cal)));

		gmt = DateUtil.parseIsoCalendar(DateUtil.formatDateMillis(cal)+"Z");
		gmt.setTimeZone(TimeZone.getDefault());
		assertEquals(cal.getTime(), gmt.getTime());
		
		cal.setTimeZone(TimeZone.getTimeZone("GMT-05:00"));
		DateUtil.parseIsoCalendar(cal, calStr, null);
		assertEquals(cal.getTime(), DateUtil.parseIsoCalendar(calStr+"-05:00").getTime());
		
		calStr = "2011-09-17T05:26:51";
		cal.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		DateUtil.parseIsoCalendar(cal, calStr, null);
		assertEquals(cal.getTime().getTime(), DateUtil.parseIsoCalendar(calStr+"+0000").getTime().getTime());
		assertEquals("2011-09-17T05:26:51", DateUtil.formatIsoDateTime(cal));
		assertEquals("2011-09-17 05:26:51", DateUtil.formatDateTime(cal));
	}
	
	public void testFirstIsoMondayOfYear() {
		Calendar date;
		assertEquals("Sun=1", 1, new GregorianCalendar(2012, 1-1, 1).get(Calendar.DAY_OF_WEEK));

		/* // more details for debugging
		date = new GregorianCalendar(2010, 1-1, 1);  logDate(date);
		date = new GregorianCalendar(2010, 1-1, 2);  logDate(date);
		date = new GregorianCalendar(2010, 1-1, 3);  logDate(date);
		date = new GregorianCalendar(2010, 1-1, 4);  logDate(date);
		date = new GregorianCalendar(2010, 1-1, 5);  logDate(date);
		date = new GregorianCalendar(2010, 1-1, 6);  logDate(date);
		date = new GregorianCalendar(2010, 1-1, 7);  logDate(date);
		date = new GregorianCalendar(2010, 1-1, 8);  logDate(date);
		date = new GregorianCalendar(2010, 1-1, 9);  logDate(date);
		date = new GregorianCalendar(2010, 1-1, 10);  logDate(date);

		System.out.println("");
		
		date = new GregorianCalendar(2010, 12-1, 30);  logDate(date);
		date = new GregorianCalendar(2010, 12-1, 31);  logDate(date);
		date = new GregorianCalendar(2011, 1-1, 1);  logDate(date);
		date = new GregorianCalendar(2011, 1-1, 2);  logDate(date);
		date = new GregorianCalendar(2011, 1-1, 3);  logDate(date);
		date = new GregorianCalendar(2011, 1-1, 4);  logDate(date);
		date = new GregorianCalendar(2011, 1-1, 5);  logDate(date);

		System.out.println("");
		
		date = new GregorianCalendar(2011, 12-1, 30);  logDate(date);
		date = new GregorianCalendar(2011, 12-1, 31);  logDate(date);
		date = new GregorianCalendar(2012, 1-1, 1);  logDate(date);
		date = new GregorianCalendar(2012, 1-1, 2);  logDate(date);
		date = new GregorianCalendar(2012, 1-1, 3);  logDate(date);
		date = new GregorianCalendar(2012, 1-1, 4);  logDate(date);
		date = new GregorianCalendar(2012, 1-1, 5);  logDate(date);
		date = new GregorianCalendar(2012, 1-1, 6);  logDate(date);

		System.out.println("");
		
		date = new GregorianCalendar(2012, 12-1, 30);  logDate(date);
		date = new GregorianCalendar(2012, 12-1, 31);  logDate(date);
		date = new GregorianCalendar(2013, 1-1, 1);  logDate(date);
		date = new GregorianCalendar(2013, 1-1, 2);  logDate(date);
		date = new GregorianCalendar(2013, 1-1, 3);  logDate(date);
		date = new GregorianCalendar(2013, 1-1, 4);  logDate(date);
		date = new GregorianCalendar(2013, 1-1, 5);  logDate(date);
		date = new GregorianCalendar(2013, 1-1, 6);  logDate(date);

		System.out.println("");
		
		date = new GregorianCalendar(2013, 12-1, 30);  logDate(date);
		date = new GregorianCalendar(2013, 12-1, 31);  logDate(date);
		date = new GregorianCalendar(2014, 1-1, 1);  logDate(date);
		date = new GregorianCalendar(2014, 1-1, 2);  logDate(date);
		date = new GregorianCalendar(2014, 1-1, 3);  logDate(date);
		date = new GregorianCalendar(2014, 1-1, 4);  logDate(date);
		date = new GregorianCalendar(2014, 1-1, 5);  logDate(date);
		date = new GregorianCalendar(2014, 1-1, 6);  logDate(date);
		date = new GregorianCalendar(2014, 1-1, 7);  logDate(date);
		*/

		assertEquals(20050103, DateUtil.toDateInt(DateUtil.getWeekBeginDateByIsoMonday(2005, 1)));
		assertEquals(20060102, DateUtil.toDateInt(DateUtil.getWeekBeginDateByIsoMonday(2006, 1)));
		assertEquals(20070101, DateUtil.toDateInt(DateUtil.getWeekBeginDateByIsoMonday(2007, 1)));
		assertEquals(20071231, DateUtil.toDateInt(DateUtil.getWeekBeginDateByIsoMonday(2008, 1)));
		assertEquals(20081229, DateUtil.toDateInt(DateUtil.getWeekBeginDateByIsoMonday(2009, 1)));
		assertEquals(20100104, DateUtil.toDateInt(DateUtil.getWeekBeginDateByIsoMonday(2010, 1)));
		assertEquals(20110103, DateUtil.toDateInt(DateUtil.getWeekBeginDateByIsoMonday(2011, 1)));
		assertEquals(20120102, DateUtil.toDateInt(DateUtil.getWeekBeginDateByIsoMonday(2012, 1)));
		assertEquals(20121231, DateUtil.toDateInt(DateUtil.getWeekBeginDateByIsoMonday(2013, 1)));
		assertEquals(20131230, DateUtil.toDateInt(DateUtil.getWeekBeginDateByIsoMonday(2014, 1)));
		assertEquals(20141229, DateUtil.toDateInt(DateUtil.getWeekBeginDateByIsoMonday(2015, 1)));
		assertEquals(20160104, DateUtil.toDateInt(DateUtil.getWeekBeginDateByIsoMonday(2016, 1)));
		assertEquals(20170102, DateUtil.toDateInt(DateUtil.getWeekBeginDateByIsoMonday(2017, 1)));
		assertEquals(20180101, DateUtil.toDateInt(DateUtil.getWeekBeginDateByIsoMonday(2018, 1)));

		assertEquals(2005001, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2005, 1-1, 3)));
		assertEquals(2005364, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2006, 1-1, 1)));
		assertEquals(2006001, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2006, 1-1, 2)));
		assertEquals(2006364, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2006, 12-1, 31)));
		assertEquals(2007001, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2007, 1-1, 1)));
		assertEquals(2007364, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2007, 12-1, 30)));
		assertEquals(2008001, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2007, 12-1, 31)));
		assertEquals(2008364, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2008, 12-1, 28)));
		assertEquals(2009001, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2008, 12-1, 29)));
		assertEquals(2009371, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2010, 1-1, 3))); // 53rd week
		assertEquals(2010001, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2010, 1-1, 4)));
		assertEquals(2010364, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2011, 1-1, 2)));
		assertEquals(2011001, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2011, 1-1, 3)));
		assertEquals(2011364, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2012, 1-1, 1)));
		assertEquals(2012001, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2012, 1-1, 2)));
		assertEquals(2012364, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2012, 12-1, 30)));
		assertEquals(2013001, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2012, 12-1, 31)));
		assertEquals(2013364, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2013, 12-1, 29)));
		assertEquals(2014001, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2013, 12-1, 30)));
		assertEquals(2014364, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2014, 12-1, 28)));
		assertEquals(2015001, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2014, 12-1, 29)));
		assertEquals(2015371, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2016, 1-1, 3))); // 53rd week
		assertEquals(2016001, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2016, 1-1, 4)));
		assertEquals(2016364, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2017, 1-1, 1)));
		assertEquals(2017001, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2017, 1-1, 2)));
		assertEquals(2018001, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2018, 1-1, 1)));
		assertEquals(2018364, DateUtil.getYearDayByIsoMonday(new GregorianCalendar(2018, 12-1, 30)));

	}
	
	public void testFirstMondayOfYear() {
		Calendar date;
		
		assertEquals(20100104, DateUtil.toDateInt(DateUtil.getWeekBeginDateByMonday(2010, 1)));
		assertEquals(20110103, DateUtil.toDateInt(DateUtil.getWeekBeginDateByMonday(2011, 1)));
		assertEquals(20120102, DateUtil.toDateInt(DateUtil.getWeekBeginDateByMonday(2012, 1)));
		assertEquals(20130107, DateUtil.toDateInt(DateUtil.getWeekBeginDateByMonday(2013, 1)));

		assertEquals(20100110, DateUtil.toDateInt(DateUtil.getWeekEndDateByMonday(2010, 1)));
		assertEquals(20110109, DateUtil.toDateInt(DateUtil.getWeekEndDateByMonday(2011, 1)));
		assertEquals(20120108, DateUtil.toDateInt(DateUtil.getWeekEndDateByMonday(2012, 1)));
		assertEquals(20130113, DateUtil.toDateInt(DateUtil.getWeekEndDateByMonday(2013, 1)));

		date = new GregorianCalendar(2010, 1-1, 4);
		assertEquals(1, DateUtil.getDayOfYearByMonday(date));
		assertEquals(1, DateUtil.getWeekOfYearByMonday(date));
		assertEquals(2010001, DateUtil.getYearDayByMonday(date));
		assertEquals(201001, DateUtil.getYearWeekByMonday(date));
		
		date = new GregorianCalendar(2010, 12-1, 31);
		assertEquals(362, DateUtil.getDayOfYearByMonday(date));
		assertEquals(52, DateUtil.getWeekOfYearByMonday(date));
		assertEquals(2010362, DateUtil.getYearDayByMonday(date));
		assertEquals(201052, DateUtil.getYearWeekByMonday(date));

		date = new GregorianCalendar(2011, 1-1, 1);
		assertEquals(-1, DateUtil.getDayOfYearByMonday(date));
		assertEquals(0, DateUtil.getWeekOfYearByMonday(date));
		assertEquals(363, DateUtil.getDayOfYearByMonday(new GregorianCalendar(2011-1, 12-1, 31)) + 2 + DateUtil.getDayOfYearByMonday(date));
		assertEquals(2010363, DateUtil.getYearDayByMonday(date));
		assertEquals(201052, DateUtil.getYearWeekByMonday(date));
		
		date = new GregorianCalendar(2011, 1-1, 2);
		assertEquals(0, DateUtil.getDayOfYearByMonday(date));
		assertEquals(0, DateUtil.getWeekOfYearByMonday(date));
		assertEquals(364, DateUtil.getDayOfYearByMonday(new GregorianCalendar(2011-1, 12-1, 31)) + 2 + DateUtil.getDayOfYearByMonday(date));
		assertEquals(2010364, DateUtil.getYearDayByMonday(date));
		assertEquals(201052, DateUtil.getYearWeekByMonday(date));

		date = new GregorianCalendar(2011, 1-1, 3);
		assertEquals(1, DateUtil.getDayOfYearByMonday(date));
		assertEquals(1, DateUtil.getWeekOfYearByMonday(date));
		assertEquals(2011001, DateUtil.getYearDayByMonday(date));
		assertEquals(201101, DateUtil.getYearWeekByMonday(date));
		
	}

}
