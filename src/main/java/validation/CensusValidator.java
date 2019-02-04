package main.java.validation;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.errors.ErrorMessages;
import main.java.model.FileResults;
import main.java.util.FileUploadEnums;

public class CensusValidator {
	static final Logger log = LoggerFactory.getLogger(CensusValidator.class);

	private static FileResults fileResults;

	public final static int address1_MaxLength = 22;
	public final static int address2_MaxLength = 18;

	// Regex PO Box Restrictions
	final static String PO_BOX_REGEX[] = { "(?i)\\b(po box)\\b", "(?i)\\b(p.o.b.)\\b", "(?i)\\b(p.o. box)\\b",
			"(?i)\\b(po-box)\\b", "(?i)\\b(p.o.-box)\\b", "(?i)\\b(p.o box)\\b", "(?i)\\b(pobox)\\b",
			"(?i)\\b(p-o-box)\\b", "(?i)\\b(p-o box)\\b", "(?i)\\b(post office box)\\b", "(?i)\\b(p.o. -box)\\b",
			"(?i)\\w*(?<![a-zA-Z]+)bin", "(?i)box[0-9]", "(?i)box-[0-9]", "(?i)box [0-9]", "(?i)pob [0-9]",
			"(?i)\\b(pob)\\b", "(?i)\\b(box #)\\b", "(?i)\\b(p box)\\b", "(?i)\\b(p. o. box)\\b", "(?i)\\b(p.o.b)\\b",
			"(?i)\\b(po)\\b", "(?i)\\b(post box)\\b", "(?i)p\\.o\\.", "(?i)p\\.b\\." };
	
	// Regex only allow numbers, letters, spaces, and '-', '.', '#'
	final static String ALLOWED_CHARACTERS_REGEX = "^[a-zA-Z0-9-.# ]*$";

	CensusValidator(FileResults fileResults) {
		this.fileResults = fileResults;
	}

	public void fullName(JSONObject row) {
		String firstNameMi = getValueFromRowObj(row, "First Name MI");
		String lastName = getValueFromRowObj(row, "Last Name");
		if (firstNameMi != null && lastName != null) {
			String fullName = firstNameMi + lastName;
			if (fullName.length() > 30) {
				fileResults.addUploadError(false, getRowNum(row), 0, ErrorMessages.FULL_NAME_MAX_CHARACTER,
						FileUploadEnums.Error.CRITICAL.toString());
			}
		}
	}

	public void maxCharacterLimit(JSONObject row, String headerName, String value, int characterLimit) {
		if (value != null && value.length() > characterLimit) {
			fileResults.addUploadError(false, getRowNum(row), 0,
					ErrorMessages.getMaxCharacterErr(headerName, characterLimit),
					FileUploadEnums.Error.CRITICAL.toString());
		}
	}

	public void poBoxCheck(JSONObject row, String headerName, String address) {
		if (address != null) {
			for (String expression : PO_BOX_REGEX) {
				Matcher restriction = Pattern.compile(expression).matcher(address);
				if (restriction.find()) {				
					fileResults.addUploadError(false, getRowNum(row), 0,
							ErrorMessages.getPOBoxErr(headerName),
							FileUploadEnums.Error.CRITICAL.toString());
					break;
				}
			}
		}
	}
	
	public void noSpecialCharacters(JSONObject row, String headerName, String value) {		
		if (value != null) {
			if (!value.matches(ALLOWED_CHARACTERS_REGEX)) {
				fileResults.addUploadError(false, getRowNum(row), 0,
						ErrorMessages.getNoSpecialCharactersErr(headerName),
						FileUploadEnums.Error.CRITICAL.toString());
			}
		}
	}

	public static String getValueFromRowObj(JSONObject row, String fieldName) {
		return (String) ((HashMap) row).get(fieldName);
	}

	public static int getRowNum(JSONObject row) {
		return (int) ((HashMap) row).get("Row #");
	}
}
