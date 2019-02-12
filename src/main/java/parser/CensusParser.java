package main.java.parser;

import org.apache.poi.ss.usermodel.Row;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.model.FileResults;
import main.java.model.HeaderField;
import main.java.util.FileUploadEnums;
import main.java.errors.ErrorMessages;
import main.java.util.ObjectUtil;

public class CensusParser extends ParseField {
	static final Logger log = LoggerFactory.getLogger(CensusParser.class);

	public CensusParser() {}

	public CensusParser(HeaderField headerField, String val, FileResults fileResults, Row row, Integer columnNum,
			JSONObject rowObj) {
		super(headerField, val, fileResults, row, columnNum, rowObj);
	}

	public void parseField() {
		String headerName = headerField.getName();
		if (ObjectUtil.isNotNullOrEmpty(val)) {
			if ("Gender".equals(headerName) && ObjectUtil.isNotNullOrEmpty(val)) {
				parseGender();
			} else if ("Social Security Number".equals(headerName)) {
				formatSSN();
			} else if (ObjectUtil.isNotNullOrEmpty(val) && ("Status".equals(headerName)
					|| "Employee Types".equals(headerName) || "Employee Class".equals(headerName))) {
				getKeyTerm();
			} else if ("ZIP".equals(headerName)) {
				parseZip();
			}
		} else {
			checkIfEmptyFieldIsRequired();
		}
		rowObj.put(headerName, this.val);
	}

	private static void parseGender() {
		if ("M".equalsIgnoreCase(val) || "Male".equalsIgnoreCase(val)) {
			val = "Male";
		} else if ("F".equalsIgnoreCase(val) || "Female".equalsIgnoreCase(val)) {
			val = "Female";
		} else {
			val = "";
			fileResults.addUploadError(false, row.getRowNum() + 1, columnNum, ErrorMessages.getValueNotValidErr("Gender"),
					FileUploadEnums.Error.WARNING.toString());
		}
	}
	
	private static void parseZip() {
		// Get only Digits
		val = val.replaceAll(REGEX_GET_ONLY_DIGITS,"");
		if (val.length() != 5 && val.length() != 9) {
			if (val.length() < 5) {
				if (val.length() >= 3) {
					// Add zeros to beginning
					while (val.length() != 5) {
						val = "0" + val;
					}
				} else if (val.length() < 3) {
					fileResults.addUploadError(false, row.getRowNum() + 1, columnNum, ErrorMessages.ZIP_CONTAIN_5_CHARACTERS,
							FileUploadEnums.Error.CRITICAL.toString());
					val = "";
				}
			} else if (val.length() > 5 && val.length() < 9) {
				if (val.length() < 7) {
					fileResults.addUploadError(false, row.getRowNum() + 1, columnNum, ErrorMessages.ZIP_CONTAIN_9_CHARACTERS,
							FileUploadEnums.Error.CRITICAL.toString());
					val = "";
				} else {
					// Add zeros to beginning
					while (val.length() != 9) {
						val = "0" + val;
					}
				}
			} else if (val.length() > 9) {
				fileResults.addUploadError(false, row.getRowNum() + 1, columnNum, ErrorMessages.ZIP_NO_GREATER_THAN_9,
						FileUploadEnums.Error.CRITICAL.toString());
				val = "";
			}
		}
	}
}
