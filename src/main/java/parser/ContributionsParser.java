package main.java.parser;

import org.apache.poi.ss.usermodel.Row;
import org.json.simple.JSONObject;

import main.java.errors.ErrorMessages;
import main.java.model.FileResults;
import main.java.model.HeaderField;
import main.java.util.FileUploadEnums;
import main.java.util.ObjectUtil;

public class ContributionsParser extends ParseField {
	
	public ContributionsParser() {}
	public ContributionsParser(HeaderField headerField, String val, FileResults fileResults, Row row, Integer columnNum,
			JSONObject rowObj) {
		super(headerField, val, fileResults, row, columnNum, rowObj);
	}

	public void parseField() {
		String headerName = headerField.getName();
		if (ObjectUtil.isNotNullOrEmpty(val)) {
			if ("Contract Number".equals(headerName)) {
				formatContractNum();
			} else if ("Social Security Number".equals(headerName)) {
				formatSSN();
			}
		} else {
			checkIfEmptyFieldIsRequired();
		}
		
		rowObj.put(headerName, this.val);
	}

	// Format Contract Number to add extra 0, and make sure is numbers only and length equals 9
	public static void formatContractNum() {
		boolean valid = true;
		val = val.trim().replaceAll("-", "");
		if (val.matches(REGEX_ONLY_NUMBERS)) {
			if (val.length() == 8) {
				val = "0" + val;
			} else if (val.length() != CONTRACT_NUMBER_LENGTH) {
				valid = false;
			}
		} else {
			valid = false;
		}
		
		if (!valid) {
			val = "";
			fileResults.addUploadError(false, row, columnNum, ErrorMessages.getNotCorrectFormatErr("Contract Number"),
					FileUploadEnums.Error.CRITICAL.toString());
		}
	}
	
}
