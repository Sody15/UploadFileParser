package main.java.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CensusValidator {
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

	public static boolean isThisDateValid(String dateToValidate) {
		if (dateToValidate == null) {
			return false;
		}
		DATE_FORMAT.setLenient(false);
		try {
			// If not valid, it will throw ParseException
			Date date = DATE_FORMAT.parse(dateToValidate);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
