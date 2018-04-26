package jp.tsubakicraft.easyreport.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateTimeUtil {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
	
	/**
	 * input 2018-12-21T00:00:00.000Z
	 */
	public static Date toDate(String input) {
		try {
			Date date = FORMAT.parse(input);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String dateString(Date date) {
		return DATE_FORMAT.format(date);
	}
	
	public static String dateTimeString(Date date) {
		return DATETIME_FORMAT.format(date);
	}
	
	public static String toDateString(String input) {
		Date d = toDate(input);
		if(d != null) {
			return dateString(d);
		}
		return null;
	}
	
	public static String toDateTimeString(String input) {
		Date d = toDate(input);
		if(d != null) {
			return dateTimeString(d);
		}
		return null;
	}
}
