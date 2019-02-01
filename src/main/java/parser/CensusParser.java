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
			fileResults.addUploadError(false, row, columnNum, ErrorMessages.getValueNotValidErr("Gender"),
					FileUploadEnums.Error.WARNING.toString());
		}
	}
}
