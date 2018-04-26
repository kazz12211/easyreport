package jp.tsubakicraft.easyreport.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class DateTimeUtil {
	
	private static DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
	private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
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
	
	private static Date dateByAddingHours(Date date, int hours) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, hours);
		return cal.getTime();
	}
	
	public static String dateString(Date date, TimeZone timeZone) {
		return DATE_FORMAT.format(dateByAddingHours(date, timeZone.getRawOffset()));
	}
	
	public static String dateTimeString(Date date, TimeZone timeZone) {
		return DATETIME_FORMAT.format(dateByAddingHours(date, timeZone.getRawOffset()));
	}
	
	public static String toDateString(String input, TimeZone timeZone) {
		Date d = toDate(input);
		if(d != null) {
			return dateString(d, timeZone);
		}
		return null;
	}
	
	public static String toDateTimeString(String input, TimeZone timeZone) {
		Date d = toDate(input);
		if(d != null) {
			return dateTimeString(d, timeZone);
		}
		return null;
	}
}
