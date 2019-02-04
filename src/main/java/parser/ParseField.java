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
	
	protected final static String REGEX_ONLY_NUMBERS_ALLOWED = "[0-9]+";
	private static String REGEX_GET_ONLY_DIGITS = "\\D+";
	
	private static String SSN_PATTERN = "***-**-";
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
			fileResults.addUploadError(false, row.getRowNum() + 1, columnNum,
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
			fileResults.addUploadError(false, row.getRowNum() + 1, columnNum,
					ErrorMessages.getKeyTermNoMappingErr(headerField.getName(), val), severity);
			val = "";
		}
	}
	
	// Format SSN by getting last four digits
	protected static void formatSSN() {		
		boolean valid = true;
		val = val.replaceAll(REGEX_GET_ONLY_DIGITS,"");
		if (val.length() < 4) {
			valid = false;
		} else if (val.length() > 4) {
			val = SSN_PATTERN + val.substring(val.length() - 4);
		} else {
			val = SSN_PATTERN + val;
		}
		
		if (!valid) {
			val = "";
			fileResults.addUploadError(false, row.getRowNum() + 1, columnNum, 
					ErrorMessages.getInCorrectFormatErr("Social Security Number"),
					FileUploadEnums.Error.CRITICAL.toString());
		}
	}
}
