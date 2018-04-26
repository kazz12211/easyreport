package jp.tsubakicraft.easyreport.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class DateTimeUtil {

	private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
	
	/**
	 * input 2018-12-21T00:00:00.000Z
	 * @param locale 
	 */
	public static Date toDate(String input) {
		try {
			Date date = FORMAT.parse(input);
			return date;
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	public static String dateString(Date date, Locale locale) {
		Calendar cal = Calendar.getInstance(locale);
		TimeZone tz = cal.getTimeZone();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		format.setTimeZone(tz);
		return format.format(date);
	}
	
	public static String dateTimeString(Date date, Locale locale) {
		Calendar cal = Calendar.getInstance(locale);
		TimeZone tz = cal.getTimeZone();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		format.setTimeZone(tz);
		return format.format(date);
	}
	
	public static String toDateString(String input, Locale locale) {
		Date d = toDate(input);
		if(d != null) {
			return dateString(d, locale);
		}
		return null;
	}
	
	public static String toDateTimeString(String input, Locale locale) {
		Date d = toDate(input);
		if(d != null) {
			return dateTimeString(d, locale);
		}
		return null;
	}
}
