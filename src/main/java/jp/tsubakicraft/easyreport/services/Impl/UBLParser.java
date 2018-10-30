package jp.tsubakicraft.easyreport.services.Impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Element;

public class UBLParser {

	private static DateFormat _DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	@SuppressWarnings("unchecked")
	protected <T> T objectValue(Element element, Class<?> clazz) {
		if(element == null)
			return null;

		String text = element.getTextContent();
		T value = null;
		if(text != null) {
			if(clazz.isAssignableFrom(String.class)) {
				value = (T) text;
			} else if(clazz.isAssignableFrom(Date.class)) {
				try {
					value = (T) _DATE_FORMAT.parse(text);
				} catch (ParseException e) {
				}
			} else if(clazz.isAssignableFrom(Integer.class)) {
				value =  (T) Integer.valueOf(text);
			} else if(clazz.isAssignableFrom(Long.class)) {
				value =  (T) Long.valueOf(text);
			} else if(clazz.isAssignableFrom(Float.class)) {
				value =  (T) Float.valueOf(text);
			} else if(clazz.isAssignableFrom(Double.class)) {
				value =  (T) Double.valueOf(text);
			} else if(clazz.isAssignableFrom(Boolean.class)) {
				value =  (T) Boolean.valueOf(text);
			} 
		}
		
		return value;
		
	}

}
