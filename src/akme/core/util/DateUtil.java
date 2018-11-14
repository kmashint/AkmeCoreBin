package akme.core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Utility class for Calendar- and Date-related functionality.
 *
 * @author Copyright(c) 2009 AKME Solutions
 * @author $Author: keith.mashinter $
 * @version $Date: $ 
 * $NoKeywords: $
 */
public abstract class DateUtil {
	
	public static final long MILLIS_IN_MINUTE = (60000);
	public static final long MILLIS_IN_HOUR = (MILLIS_IN_MINUTE * 60);
	public static final long MILLIS_IN_DAY = (MILLIS_IN_HOUR * 24);
	public static final long MILLIS_IN_WEEK = (MILLIS_IN_DAY * 7);

	public static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");

	/** 
	 * Return the <b>same</b> mutable Calendar instance trimmed of any milliseconds.
	 */
	public static Calendar trimMillis(final Calendar cal) {
		if (cal == null) return null;
		cal.set(Calendar.MILLISECOND,0);
		return cal;
	}
	
	/**
	 * Return a <b>new</b> semi-immutable (avoid setTime) Date instance trimmed of any milliseconds.
	 */
	public static Date trimMillis(final Date date) {
		if (date == null) return null;
		final long millis = date.getTime();
		return new Date(millis - (millis % 1000L));
	}
	
	/**
	 * Return the <b>same</b> mutable Calendar instance trimmed of time to the start of the day (midnight).
	 */
	public static Calendar trimTime(final Calendar cal) {
		if (cal == null) return null;
		// This shouldn't just subtract from the MILLIS_IN_DAY due to time zone offsets and the millis is GMT/UTC.
		cal.set(Calendar.MILLISECOND,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.HOUR_OF_DAY,0);
		return cal;
	}
	
	/**
	 * Return a <b>new</b> semi-immutable (avoid setTime) Date instance trimmed of time to the start of the day (midnight).
	 * This is less efficient than trimTime(Calendar) since it creates a temporary calendar to strip the time.
	 */
	public static Date trimTime(final Date date) {
		if (date == null) return null;
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return trimTime(cal).getTime();
	}
	
	/**
	 * Return a <b>new</b> semi-immutable (avoid setTime) Date instance trimmed of time to the start of the day (midnight).
	 * This is more efficient than trimTime(Date) but only works with UTC time.
	 */
	public static Date trimTimeUTC(final Date date) {
		if (date == null) return null;
		// We can subtract millis here since Date millis are in UTC/GMT.
		final long millis = date.getTime();
		return new Date(millis - (millis % MILLIS_IN_DAY));
	}
	
	/** 
	 * Return yyyyMM as an integer for fast comparisons.
	 */
	public static int toYearMonthInt(final Calendar cal) {
		return cal.get(Calendar.YEAR) * 100 + (cal.get(Calendar.MONTH)+1);
	}

	/** 
	 * Return yyyyMMdd as an integer for fast comparisons.
	 */
	public static int toDateInt(final Calendar cal) {
		return cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH)+1) * 100 + cal.get(Calendar.DAY_OF_MONTH);
	}
	
	/** 
	 * Return yyyyMMdd as an integer for fast comparisons (month is 1-12).
	 */
	public static int toDateInt(final int year, final int month, final int day) {
		return year * 10000 + month * 100 + day;
	}
	
	/**
	 * Return a Calendar based on the given yyyyMMdd integer date.
	 */
	public static Calendar toCalendarFromDateInt(final int date) {
		return new GregorianCalendar(date / 10000, (date / 100) % 100 - 1, date % 100);
	}
	
	/** 
	 * Return yyyyMMddHHmmss (month is 1-12, 24-hour, no millis) as a long for fast comparisons.
	 */
	public static long toDateTimeLong(final int year, final int month, final int day, final int hour, final int minute, final int second) {
		return (long) year * 10000000000L + month * 100000000L + day * 1000000L
			+ hour * 10000L + minute * 100L + second;
	}

	/** 
	 * Return yyyyMMddHHmmss (24-hour, no millis) as a long for fast comparisons.
	 */
	public static long toDateTimeLong(final Calendar cal) {
		return (long) cal.get(Calendar.YEAR) * 10000000000L + (cal.get(Calendar.MONTH)+1L) * 100000000L + cal.get(Calendar.DAY_OF_MONTH) * 1000000L
			+ cal.get(Calendar.HOUR_OF_DAY) * 10000L + cal.get(Calendar.MINUTE) * 100L + cal.get(Calendar.SECOND);
	}

	/**
	 * Return a Calendar based on the given yyyyMMddHHmmss (24-hour, no millis) long date.
	 */
	public static Calendar toCalendarFromDateTimeLong(final long datetime) {
		final int d = (int) (datetime / 1000000L);
		final int t = (int) (datetime % 1000000L);
		return new GregorianCalendar(d / 10000, (d / 100) % 100 - 1, d % 100,
				t / 10000, (t / 100) % 100, t % 100);
	}
	
	/**
	 * Convert the Date to the default Calendar.
	 */
	public static Calendar toCalendar(final Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	/**
	 * Parse an ISO Date String, e.g. "Year-Mo-DaTHr:Mi:Se" or "Year-Mo-Da Hr:Mi:Se" or "YearMoDaHrMiSe", 
	 * to the given Calendar, or return the given default if not parseable or may through a runtime exception.
	 * The given will also be set either to the dateStr or the def if dateStr is invalid and returned.
	 * If the dateStr is invalid and def is null then null is returned.
	 * Supports ".Mil" milli- or fractional seconds and TimeZone info (Z or +/-HH(:)MM).
	 * 
	 * @return Either the modified given Calendar or null if the def Calendar was null.
	 */
	public static Calendar parseIsoCalendar(final Calendar given, final String dateStr, final Calendar def) {
		Calendar result;
		final int len = dateStr != null ? dateStr.length() : 0;
		if (len == 8 || len == 10 || len == 14 || len == 15 || len == 19 || (len >= 20 && len <= 29)) {
			final long dt;
			if (len == 10) {
				dt = Long.parseLong(
					dateStr.substring(0,4)+
					dateStr.substring(5,7)+
					dateStr.substring(8,10));
			} else if (len == 19 || (len >= 20 && len <= 29)) {
				dt = Long.parseLong(
					dateStr.substring(0,4)+
					dateStr.substring(5,7)+
					dateStr.substring(8,10)+
					dateStr.substring(11,13)+
					dateStr.substring(14,16)+
					dateStr.substring(17,19));
			} else if (len == 15) {
				dt = (long)1000000L*Long.parseLong(dateStr.substring(0,8)) + Long.parseLong(dateStr.substring(9,15));
			} else {
				dt = Long.parseLong(dateStr);
			}
			if (dt < 99999999L) {
				// YearMoDa
				result = given;
				if (!setValidDate(result, (int)dt)) result = null;
			} else {
				// YearMoDaHoMiSe
				result = given;
				int end = len;
				if (len >= 20 && len <= 29) {
					// Find end of milliseconds or fractional seconds.
					end = 19;
					for (char c; end < len && ((c = dateStr.charAt(end)) == '.' || (c >= '0' && c <= '9')); end++) ;
					// Find TimeZone TimeZone Z or +/-HH(:)MM.
					if ('Z' == dateStr.charAt(len-1)) result.setTimeZone(GMT_ZONE);
					else if (end < len) result.setTimeZone(TimeZone.getTimeZone("GMT"+dateStr.substring(end,len)));
				}
				if (!setValidDateTime(result, dt)) result = null;
				else if (end > 20) result.set(Calendar.MILLISECOND, NumberUtil.toIntPrimitive(dateStr.substring(20,end), 0));
			}
		} else {
			result = null;
		}
		if (result == null) {
			if (def == null) result = def;
			else {
				result = given;
				result.clear();
				result.setTimeInMillis(def.getTimeInMillis());
				result.setTimeZone(def.getTimeZone());
			}
		}
		return result;
	}
	
	/**
	 * Parse an ISO Date String, e.g. "Year-Mo-DaTHr:Mi:Se" or "Year-Mo-Da Hr:Mi:Se" or "YearMoDaHrMiSe", 
	 * to a GregorianCalendar, or return the given default if not parseable or may through a runtime exception.
	 * Supports ".Mil" milli- or fractional seconds and TimeZone info (Z or +/-HH(:)MM).
	 */
	public static Calendar parseIsoCalendar(final String dateStr, final Calendar def) {
		return parseIsoCalendar(Calendar.getInstance(), dateStr, def);
	}
	
	/**
	 * Parse an ISO Date String, e.g. "Year-Mo-DaTHr:Mi:Se" or "Year-Mo-Da Hr:Mi:Se" or "YearMoDaHrMiSe", 
	 * to a GregorianCalendar, or return null if not parseable or may through a runtime exception.
	 */
	public static Calendar parseIsoCalendar(final String dateStr) {
		return parseIsoCalendar(Calendar.getInstance(), dateStr, null);
	}
	
    /**
     * Format a Calendar date to the ISO format, simplified as per W3C.
     * <br><code>1994-11-05T08:15:30-05:00</code> corresponds to November 5, 1994, 8:15:30 am, US Eastern Standard Time.
     * <br><code>19941105T131530Z</code> corresponds to the same instant. <p>
     * See http://www.w3.org/TR/NOTE-datetime and ISO 8601 for background information.
     * 
     * @param cal Calendar as the source of the date.
     * @param showDelimiters Show delimiters (- in date, : in time) or not.
	 * @param trueTfalseSpace Show, without the quotes, date'T'time if true, date' 'time if false.
     * @param showMillis Show milliseconds or not.
	 * @param showTimeZone Show TimeZone info (Z or +/-HH(:)MM) or not.
     */
	public static String formatIsoDateTime(final Calendar cal, final boolean showDelimiters, final boolean trueTfalseSpace, final boolean showMillis, final boolean showTimeZone) {
		/*
		int offset = (int)( cal.getTimeZone().getRawOffset() / 60000 );  
		int offset = (int)( cal.getTimeZone().getOffset(cal.getTimeInMillis()) / 60000);
		int offset = (int)(( cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET) ) / 60000);
		*/
		int offset = (int)( cal.getTimeZone().getOffset(
				1, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 
				cal.get(Calendar.DAY_OF_WEEK), (int)((long)cal.getTime().getTime() % MILLIS_IN_DAY)
				) / 60000);
		final char offsgn = (offset == 0) ? 'Z' : (offset > 0) ? '+' : '-';
		if (offset < 0) offset = -offset;
		final int offmin = offset % 60;
		final int offhrs = offset / 60;

		final char[] result = new char[29];
		int pos = 0;
		pos += StringUtil.padZeroLeftFastMaxLength(result, pos, cal.get(Calendar.YEAR), 4);
		if (showDelimiters) result[pos++] = '-';
		pos += StringUtil.padZeroLeftFastMax2(result, pos, cal.get(Calendar.MONTH)+1);
		if (showDelimiters) result[pos++] = '-';
		pos += StringUtil.padZeroLeftFastMax2(result, pos, cal.get(Calendar.DAY_OF_MONTH));
		result[pos++] = (trueTfalseSpace) ? 'T' : ' ';
		pos += StringUtil.padZeroLeftFastMax2(result, pos, cal.get(Calendar.HOUR_OF_DAY));
		if (showDelimiters) result[pos++] = ':';
		pos += StringUtil.padZeroLeftFastMax2(result, pos, cal.get(Calendar.MINUTE));
		if (showDelimiters) result[pos++] = ':';
		pos += StringUtil.padZeroLeftFastMax2(result, pos, cal.get(Calendar.SECOND));
		if (showMillis) {
			if (showDelimiters) result[pos++] = '.';
			pos += StringUtil.padZeroLeftFastMaxLength(result, pos, cal.get(Calendar.MILLISECOND), 3);
		}
		if (showTimeZone) {
			result[pos++] = offsgn;
			if (offset != 0) {
				pos += StringUtil.padZeroLeftFastMax2(result, pos, offhrs);
				if (showDelimiters) result[pos++] = ':';
				pos += StringUtil.padZeroLeftFastMax2(result, pos, offmin);
			}
		}
		return String.valueOf(result,0,pos);
	}

	/**
	 * Format as an ISO date-time (yyyy-MM-dd'T'HH:mm:ss or yyyyMMdd'T'HHmmss).
	 * 
	 * @see toIsoDateTime(java.util.Calendar,boolean,boolean,boolean,boolean)
	 */
    public static String formatIsoDateTime(final Calendar cal, final boolean showDelimiters) {
        return formatIsoDateTime(cal, showDelimiters, true, false, false);
    }

	/**
	 * Format as an ISO date-time with delimiters (yyyy-MM-dd'T'HH:mm:ss).
	 * 
	 * @see toIsoDateTime(java.util.Calendar,boolean,boolean,boolean,boolean)
	 */
	public static String formatIsoDateTime(final Calendar cal) {
		return formatIsoDateTime(cal, true, true, false, false);
	}
	
	/**
	 * Format as an ISO date-time with milliseconds (yyyy-MM-dd'T'HH:mm:ss.SSS or yyyyMMdd'T'HHmmssSSS).
	 * 
	 * @see toIsoDateTime(java.util.Calendar,boolean,boolean,boolean,boolean)
	 */
	public static String formatIsoDateMillis(final Calendar cal, final boolean showDelimiters) {
		return formatIsoDateTime(cal, showDelimiters, true, true, false);
	}

	/**
	 * Format as an ISO date-time with milliseconds (yyyy-MM-dd'T'HH:mm:ss.SSS).
	 * 
	 * @see toIsoDateTime(java.util.Calendar,boolean,boolean,boolean,boolean)
	 */
	public static String formatIsoDateMillis(final Calendar cal) {
		return formatIsoDateTime(cal, true, true, true, false);
	}

	/** 
	 * Format a Date to the standard format (yyyy-MM-dd or yyyyMMdd) or an empty string if null. 
	 */
	public static String formatDate(final Calendar date, final boolean showDelimiters) {
		if (date != null) { 
			return formatDateInt(toDateInt(date), showDelimiters);
		}
		else {
			return StringUtil.EMPTY_STRING;
		}
	}
	
	/**
	 * Format a date to the ISO (yyyy-MM-dd) format or an empty string if null.
	 */
	public static String formatDate(final Calendar date) {
		return formatDate(date, true);
	}

	/**
	 * Format from a long yyyyMMdd to an date, no milliseconds (yyyy-MM-dd or yyyyMMdd).
	 */ 
	public static String formatDateInt(final int date, final boolean showDelimiters) {
		final char[] result = new char[10];
		int pos = 0;
		pos += StringUtil.padZeroLeftFastMaxLength(result, pos, date / 10000, 4);
		if (showDelimiters) result[pos++] = '-';
		pos += StringUtil.padZeroLeftFastMax2(result, pos, date / 100);
		if (showDelimiters) result[pos++] = '-';
		pos += StringUtil.padZeroLeftFastMax2(result, pos, date);
		return String.valueOf(result, 0, pos);
	}
	
	/**
	 * Format from a long yyyyMMdd to an date, no milliseconds (yyyy-MM-dd or yyyyMMdd).
	 */ 
	public static String formatDateInt(final int date) {
		return formatDateInt(date, true);
	}

	/** 
	 * Format a Date to standard format (yyyy-MM-dd' 'HH:mm:ss or yyyyMMdd' 'HHmmss) or an empty string if null. 
	 */
	public static String formatDateTime(final Calendar date, final boolean showDelimiters) {
		if (date != null) {
			return formatDateTimeLong(toDateTimeLong(date), showDelimiters, false);
		}
		else {
			return StringUtil.EMPTY_STRING;
		}
	}

	/**
	 * Format a date to the standard (yyyy-MM-dd' 'HH:mm:ss) format or an empty string if null.
	 */
	public static String formatDateTime(final Calendar date) {
		return formatDateTime(date, true);
	}

	/**
	 * Format as an ISO date-time (yyyy-MM-dd' 'HH:mm:ss or yyyyMMdd' 'HHmmss).
	 */
    public static String formatDateMillis(final Calendar cal, final boolean showDelimiters) {
        return formatIsoDateTime(cal, showDelimiters, false, true, false);
    }

	/**
	 * Format as an ISO date-time with delimiters (yyyy-MM-dd' 'HH:mm:ss).
	 */
	public static String formatDateMillis(final Calendar cal) {
		return formatIsoDateTime(cal, true, false, true, false);
	}
	
	/**
	 * Format from a long yyyyMMddHHmmss to an ISO date-time, no milliseconds (yyyy-MM-dd'T'HH:mm:ss or yyyyMMdd'T'HHmmss or with ' ' instead of 'T').
	 */ 
	public static String formatDateTimeLong(final long datetime, final boolean showDelimiters, final boolean trueTfalseSpace) {
		final int d = (int) (datetime / 1000000L);
		final int t = (int) (datetime % 1000000L);
		final char[] result = new char[19];
		int pos = 0;
		pos += StringUtil.padZeroLeftFastMaxLength(result, pos, d / 10000, 4);
		if (showDelimiters) result[pos++] = '-';
		pos += StringUtil.padZeroLeftFastMax2(result, pos, d / 100);
		if (showDelimiters) result[pos++] = '-';
		pos += StringUtil.padZeroLeftFastMax2(result, pos, d);
		result[pos++] = (trueTfalseSpace) ? 'T' : ' ';
		pos += StringUtil.padZeroLeftFastMax2(result, pos, t / 10000);
		if (showDelimiters) result[pos++] = ':';
		pos += StringUtil.padZeroLeftFastMax2(result, pos, t / 100);
		if (showDelimiters) result[pos++] = ':';
		pos += StringUtil.padZeroLeftFastMax2(result, pos, t);
		return String.valueOf(result,0,pos);
	}

	/**
	 * Format from a long yyyyMMddHHmmss to an ISO date-time with no milliseconds (yyyy-MM-dd'T'HH:mm:ss).
	 */
	public static String formatIsoDateTimeLong(final long datetime) {
		return formatDateTimeLong(datetime, true, true);
	}

	/**
	 * Format from a long yyyyMMddHHmmss to an ISO date-time with no milliseconds (yyyy-MM-dd'T'HH:mm:ss or yyyyMMdd'T'HHmmss).
	 */
	public static String formatIsoDateTimeLong(final long datetime, final boolean showDelimiters) {
		return formatDateTimeLong(datetime, showDelimiters, true);
	}
		
	/**
	 * Format from a long yyyyMMddHHmmss to a space-separated date-time with no milliseconds (yyyy-MM-dd' 'HH:mm:ss).
	 */
	public static String formatDateTimeLong(final long datetime) {
		return formatDateTimeLong(datetime, true, false);
	}

	/**
	 * Format from a long yyyyMMddHHmmss to a space-separated date-time with no milliseconds (yyyy-MM-dd' 'HH:mm:ss or yyyyMMdd' 'HHmmss).
	 */
	public static String formatDateTimeLong(final long datetime, final boolean showDelimiters) {
		return formatDateTimeLong(datetime, showDelimiters, false);
	}

	/**
	 * Validate the datetime, setting the given Calendar.
	 * This is helpful since the Calendar manipulates values, e.g.
	 * 1999-12-32 magically becomes 2000-01-01 although it is invalid as given.
	 */
	public static boolean setValidIsoDateTime(final Calendar cal, final String dateStr) {
		return parseIsoCalendar(cal, dateStr, null) != null;
	}
	
	/**
	 * Validate the datetime, setting the given Calendar.
	 * This is helpful since the Calendar manipulates values, e.g.
	 * 1999-12-32 magically becomes 2000-01-01 although it is invalid as given.
	 * 
	 * @return true if the date-time was valid as given, i.e. the Calendar did not need to manipulate the given values.
	 */
	public static boolean setValidDateTime(final Calendar cal, final long datetime) {
		final int d = (int) (datetime / 1000000L);
		final int t = (int) (datetime % 1000000L);
		return setValidDateTime(cal, 
				d / 10000, (d / 100) % 100, d % 100,
				t / 10000, (t / 100) % 100, t % 100);
	}

	/**
	 * Validate the datetime, setting the given Calendar.
	 * This is helpful since the Calendar manipulates values, e.g.
	 * 1999-12-32 magically becomes 2000-01-01 although it is invalid as given.
	 * The mo, month, is expected in the normal range 1-12 and adjusted internally to the Calendar 0-11.
	 * 
	 * @return true if the date-time was valid as given, i.e. the Calendar did not need to manipulate the given values.
	 */
	public static boolean setValidDateTime(final Calendar cal, final int ye, final int mo, final int da, final int ho, final int mi, final int se) {
		cal.clear();
		cal.set(ye, mo-1, da, ho, mi, se);
		return (ye == cal.get(Calendar.YEAR) && mo == (cal.get(Calendar.MONTH)+1) && da == cal.get(Calendar.DAY_OF_MONTH)
			&& ho == cal.get(Calendar.HOUR_OF_DAY) && mi == cal.get(Calendar.MINUTE) && se == cal.get(Calendar.SECOND));
	}

	/**
	 * Validate the datetime, setting the given Calendar.
	 * This is helpful since the Calendar manipulates values, e.g.
	 * 1999-12-32 magically becomes 2000-01-01 although it is invalid as given.
	 * 
	 * @return true if the date was valid as given, i.e. the Calendar did not need to manipulate the given values.
	 */
	public static boolean setValidDate(final Calendar cal, final int date) {
		return setValidDate(cal, date / 10000, (date / 100) % 100, date % 100);
	}

	/**
	 * Validate the datetime, setting the given Calendar.
	 * This is helpful since the Calendar manipulates values, e.g.
	 * 1999-12-32 magically becomes 2000-01-01 although it is invalid as given.
	 * The mo, month, is expected in the normal range 1-12 and adjusted internally to the Calendar 0-11.
	 * 
	 * @return true if the date was valid as given, i.e. the Calendar did not need to manipulate the given values.
	 */
	public static boolean setValidDate(final Calendar cal, final int ye, final int mo, final int da) {
		cal.clear();
		cal.set(ye, mo-1, da);
		return (ye == cal.get(Calendar.YEAR) && mo == (cal.get(Calendar.MONTH)+1) && da == cal.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * Return the day of year of the given date where day 1 is the first monday.
	 * Return -6 through 0 for days before the first monday of the year.
	 */
	public static int getDayOfYearByMonday(final Calendar date) {
		return date.get(Calendar.DAY_OF_YEAR) - ( (date.get(Calendar.DAY_OF_YEAR)-date.get(Calendar.DAY_OF_WEEK)+8)%7 );
	}
	
	/**
	 * Return the week of year for the given date where week 1 starts on the first monday of the year.
	 * Return 0 for days before the first monday of the year.
	 */
	public static int getWeekOfYearByMonday(final Calendar date) {
		return (getDayOfYearByMonday(date) + 6) / 7;
	}

	/**
	 * Return a 7-digit year*1000+dayOfYear based on dayOfYear=1 on the first monday of the year.
	 * Use (int)result/1000 to get the year, (int)result%1000 to get the dayOfYear.
	 */
	public static int getYearDayByMonday(final Calendar date) {
		final int result = getDayOfYearByMonday(date);
		if (result < 1) return (date.get(Calendar.YEAR)-1) * 1000 + (getDayOfYearByMonday(new GregorianCalendar(date.get(Calendar.YEAR)-1, 12-1, 31)) + 2 + result);
		else return date.get(Calendar.YEAR)*1000 + result;
	}

	/**
	 * Return a 6-digit year*100+weekOfYear based on dayOfYear=1 on the first monday of the year.
	 * Use (int)result/100 to get the year, (int)result%100 to get the weekOfYear.
	 */
	public static int getYearWeekByMonday(final Calendar date) {
		final int result = getWeekOfYearByMonday(date);
		if (result < 1) return (date.get(Calendar.YEAR)-1) * 100 + (getWeekOfYearByMonday(new GregorianCalendar(date.get(Calendar.YEAR)-1, 12-1, 31)));
		else return date.get(Calendar.YEAR)*100 + result;
	}
	
	/**
	 * Return the first date of the given (year, week) where week 1 starts the first monday of the year. 
	 */
	public static Calendar getWeekBeginDateByMonday(final int year, final int week) {
		final Calendar result = new GregorianCalendar(year, 1-1, 1);
		result.add(Calendar.DAY_OF_YEAR, 1-getDayOfYearByMonday(result) + (week-1)*7);
		return result;
	}

	/**
	 * Return the last date of the given (year, week) where week 1 starts the first monday of the year. 
	 */
	public static Calendar getWeekEndDateByMonday(final int year, final int week) {
		final Calendar result = getWeekBeginDateByMonday(year, week);
		result.add(Calendar.DAY_OF_YEAR, 6);
		return result;
	}

	/**
	 * Return the first date (Mon) of the given (year, week) where week 1 starts the Monday with 4-Jan in it,
	 * the ISO-8601 definition of week 01, equivalently the week with the first Thursday of the year.
	 */
	public static Calendar getWeekBeginDateByIsoMonday(final int year, final int week) {
		final Calendar result = new GregorianCalendar(year, 1-1, 4);
		result.add(Calendar.DAY_OF_YEAR, -(result.get(Calendar.DAY_OF_WEEK)+5)%7 + (week-1)*7);
		return result;
	}
	
	/**
	 * Return the last date (Sun) of the given (year, week) where week 1 starts the Monday with 4-Jan in it,
	 * the ISO-8601 definition of week 01, equivalently the week with the first Thursday of the year.
	 */
	public static Calendar getWeekEndDateByIsoMonday(final int year, final int week) {
		final Calendar result = getWeekBeginDateByIsoMonday(year, week);
		result.add(Calendar.DAY_OF_YEAR, 6);
		return result;
	}

	/**
	 * Return a 7-digit year*1000+dayOfYear where dayOfYear=1 on the Monday of the week with 4-Jan in it,
	 * equivalently the dayOfYear=1 on the Monday of the week with the first Thursday in it (ISO-8601).
	 * Use (int)result/1000 to get the year, (int)result%1000 to get the dayOfYear,
	 * and (int)(result%1000-1)/7+1 to get the weekOfYear.
	 */
	public static int getYearDayByIsoMonday(final Calendar date) {
		final int thuOffset = 3 - (date.get(Calendar.DAY_OF_WEEK)+5)%7;
		final Calendar thuDate = (Calendar) date.clone();
		thuDate.add(Calendar.DAY_OF_YEAR, thuOffset); // bridge year by first Thursday
		final int isoYear = thuDate.get(Calendar.YEAR);
		final int isoWeek0 = (thuDate.get(Calendar.DAY_OF_YEAR)-1)/7;
		return isoYear*1000 + isoWeek0*7 + 4-thuOffset;
	}

	/**
	 * Return a 7-digit year*1000+weekOfYear where dayOfYear=1 on the Monday of the week with 4-Jan in it,
	 * equivalently the dayOfYear=1 on the Monday of the week with the first Thursday in it (ISO-8601).
	 * Use (int)result/1000 to get the year, (int)result%1000 to get the weekOfYear.
	 */
	public static int getYearWeekByIsoMonday(final Calendar date) {
		final int result = getYearDayByIsoMonday(date);
		return result - result%1000 + (result%1000-1)/7+1;
	}
	
	/**
	 * Return the rounded (.5 up) number of days between date1 and date2, negative if date2 < date1.
	 * This will be affected by daylight saving time forward/back of an hour and leap seconds (e.g. 2012-07-01).
	 * Giving pure dates, trimmed of time to midnight, will remove the effects of DST and leap seconds.
	 */
	public static int diffDays(final Calendar date1, final Calendar date2) {
		return diffDays(date1.getTimeInMillis(), date2.getTimeInMillis());
	}

	/**
	 * Return the rounded (.5 up) number of days between date1 and date2, negative if date2 < date1.
	 * This will be affected by daylight saving time forward/back of an hour and leap seconds (e.g. 2012-07-01).
	 * Giving pure dates, trimmed of time to midnight, will remove the effects of DST and leap seconds.
	 */
	public static int diffDays(final Date date1, final Date date2) {
		return diffDays(date1.getTime(), date2.getTime());
	}

	/**
	 * Return the rounded (.5 up) number of days between date1 and date2, negative if date2 < date1.
	 * This will be affected by daylight saving time forward/back of an hour and leap seconds (e.g. 2012-07-01).
	 * Giving pure dates, trimmed of time to midnight, will remove the effects of DST and leap seconds.
	 */
	public static int diffDays(final long date1millis, final long date2millis) {
		return (int) ((long)(date2millis - date1millis + 12*MILLIS_IN_HOUR) / MILLIS_IN_DAY);
	}

}
