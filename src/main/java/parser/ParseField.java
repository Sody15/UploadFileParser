package main.java.parser;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Row;
import org.json.simple.JSONObject;

import main.java.errors.ErrorMessages;
import main.java.model.FileResults;
import main.java.model.HeaderField;
import main.java.util.FileUploadEnums;
import main.java.util.ObjectUtil;

public class ParseField {
	protected static HeaderField headerField;
	protected static String val; 
	protected static FileResults fileResults; 
	protected static Row row;
	protected static Integer columnNum;
	protected static JSONObject rowObj;
	
	protected final static String REGEX_ONLY_NUMBERS = "[0-9]+";
	protected final static int SSN_LENGTH = 9;
	protected final static int CONTRACT_NUMBER_LENGTH = 9;
	
	ParseField() {}
	
	ParseField(HeaderField headerField, String val, FileResults fileResults, Row row, Integer columnNum, JSONObject rowObj) {
		this.headerField = headerField;
		if (ObjectUtil.isNotNullOrEmpty(val)) {
			// Trim and remove special characters
			this.val = val.trim().replaceAll("\n", " ").replaceAll("'", "");
		} else {
			this.val = val;
		}
		this.fileResults = fileResults;
		this.row = row;
		this.columnNum = columnNum;
		this.rowObj = rowObj;
	}
	
	/*
	 * All shared upload a file methods are below.
	 */
	
	// If field is required, show error
	protected static void checkIfEmptyFieldIsRequired() {
		if (headerField.isRequired()) {
			fileResults.addUploadError(false, row, columnNum,
					ErrorMessages.getRequiredFieldErr(headerField.getName()),
					FileUploadEnums.Error.CRITICAL.toString());
			val = "";
		}
	}
	
	// Parse Key Term
	protected static void getKeyTerm() {
		boolean found = false;
		if (headerField.getKeyTermPatterns() != null) {
			Iterator it = headerField.getKeyTermPatterns().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				String pattern = (String) pair.getValue();
				Matcher restriction = Pattern.compile(pattern).matcher(val);
				if (restriction.find()) {
					String keyTerm = (String) pair.getKey();
					val = keyTerm;
					found = true;
				}
			}
		}
		if (!found) {
			String severity = headerField.isRequired() ? FileUploadEnums.Error.CRITICAL.toString()
					: FileUploadEnums.Error.WARNING.toString();
			fileResults.addUploadError(false, row, columnNum,
					ErrorMessages.getKeyTermNoMappingErr(headerField.getName(), val), severity);
			val = "";
		}
	}
	
	// Format SSN to add extra 0's, and make sure is numbers only and length equals 9
	protected static void formatSSN() {
		boolean valid = true;
		val = val.trim().replaceAll("-", "");
		if (val.matches(REGEX_ONLY_NUMBERS)) {
			if (val.length() != SSN_LENGTH && val.length() > 4 && val.length() < SSN_LENGTH) {
				while (val.length() < SSN_LENGTH) {
					val = "0" + val;
				}
			} else if (val.length() != SSN_LENGTH) {
				valid = false;
			}
		} else {
			valid = false;
		}
		
		if (!valid) {
			val = "";
			fileResults.addUploadError(false, row, columnNum, ErrorMessages.getNotCorrectFormatErr("SSN"),
					FileUploadEnums.Error.CRITICAL.toString());
		}
	}
	
	// Returns formatted SSN value(last four digits)
	protected static void getLastFourSSN() {
		if (val.length() >= 4) {
			val = val.trim().substring(val.length() - 4);
		}
	}
}
