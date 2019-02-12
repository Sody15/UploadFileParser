package main.java.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.errors.ErrorMessages;
import main.java.model.FileResults;
import main.java.util.FileUploadEnums;
import main.java.util.ObjectUtil;

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
	
	// State Code List
	List<String> stateCodeList = populateStateCodeList();

	CensusValidator(FileResults fileResults) {
		this.fileResults = fileResults;
	}
	
	private List<String> populateStateCodeList() {
		List<String> stateCodeList = new ArrayList<String>();
		String stateCodeStr = "AL,AK,AS,AZ,AR,CA,CO,CT,DE,DC,FM,FL,GA,GU,HI,ID,IL,IN,IA,KS,KY,LA,ME,MH,MD,MA,"
				+ "MI,MN,MS,MO,MT,NE,NV,NH,NJ,NM,NY,NC,ND,MP,OH,OK,OR,PW,PA,PR,RI,SC,SD,TN,TX,UT,VT,VI,VA,"
				+ "WA,WV,WI,WY";
		stateCodeList = Arrays.asList(stateCodeStr.split(","));
		return stateCodeList;		
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
		if (ObjectUtil.isNotNullOrEmpty(value) && value.length() > characterLimit) {
			fileResults.addUploadError(false, getRowNum(row), 0,
					ErrorMessages.getMaxCharacterErr(headerName, characterLimit),
					FileUploadEnums.Error.CRITICAL.toString());
		}
	}

	public void poBoxCheck(JSONObject row, String headerName, String address) {
		if (ObjectUtil.isNotNullOrEmpty(address)) {
			for (String expression : PO_BOX_REGEX) {
				Matcher restriction = Pattern.compile(expression).matcher(address);
				if (restriction.find()) {
					fileResults.addUploadError(false, getRowNum(row), 0, ErrorMessages.getPOBoxErr(headerName),
							FileUploadEnums.Error.CRITICAL.toString());
					break;
				}
			}
		}
	}

	public void noSpecialCharacters(JSONObject row, String headerName, String value) {
		if (ObjectUtil.isNotNullOrEmpty(value)) {
			if (!value.matches(ALLOWED_CHARACTERS_REGEX)) {
				fileResults.addUploadError(false, getRowNum(row), 0,
						ErrorMessages.getNoSpecialCharactersErr(headerName), FileUploadEnums.Error.CRITICAL.toString());
			}
		}
	}

	public void statusValidation(JSONObject row, String headerName, String status, String takeoverFile, String finalCensus) {
		if (ObjectUtil.isNotNullOrEmpty(status)) {
			if ("Deceased".equals(status) || "QDRO".equals(status) || "Beneficiary".equals(status)) {
				fileResults.addUploadError(false, getRowNum(row), 0, ErrorMessages.STATUS_QDRO_BENEFICIARY_DECEASED,
						FileUploadEnums.Error.WARNING.toString());
			} else if (("Disabled".equals(status) || "Inactive".equals(status))
					&& ObjectUtil.isNullOrEmpty(getValueFromRowObj(row, "Status Change Date"))) {
				// Try and get Status Change Date
				fileResults.addUploadError(false, getRowNum(row), 0,
						ErrorMessages.getInvalidFieldErr("Status Change Date"),
						FileUploadEnums.Error.CRITICAL.toString());
			} else if ("Suspended".equals(status)
					&& ObjectUtil.isNullOrEmpty(getValueFromRowObj(row, "Hardship Suspension Date"))) {
				fileResults.addUploadError(false, getRowNum(row), 0,
						ErrorMessages.getInvalidFieldErr("Hardship Suspension Date"),
						FileUploadEnums.Error.CRITICAL.toString());
			} else if (("Terminated".equals(status) || "Retired".equals(status))
					&& ObjectUtil.isNullOrEmpty(getValueFromRowObj(row, "Termination Date"))) {
				fileResults.addUploadError(false, getRowNum(row), 0,
						ErrorMessages.getInvalidFieldErr("Termination Date"),
						FileUploadEnums.Error.CRITICAL.toString());
			} else if (!"Terminated".equals(status) && !"Retired".equals(status)
					&& ObjectUtil.isNotNullOrEmpty(getValueFromRowObj(row, "Termination Date"))) {
				fileResults.addUploadError(false, getRowNum(row), 0,
						ErrorMessages.TERMINATION_DATE_ONLY_APPLICABLE,
						FileUploadEnums.Error.CRITICAL.toString());
			} else if (!"Not eligible".equals(status) && !"Eligible".equals(status)
					&& ObjectUtil.isNullOrEmpty(getValueFromRowObj(row, "Plan Entry Date")) && 
					("Y".equals(takeoverFile) || ("N".equals(takeoverFile) && "Y".equals(finalCensus))) ) {
				fileResults.addUploadError(false, getRowNum(row), 0,
						ErrorMessages.getInvalidFieldErr("Plan Entry Date"), FileUploadEnums.Error.CRITICAL.toString());
			}
		}
	}
	
	public void stateCodeValidation(JSONObject row, String stateCode) {
		if (ObjectUtil.isNotNullOrEmpty(stateCode) && !stateCodeList.contains(stateCode)) {
			fileResults.addUploadError(false, getRowNum(row), 0,
					ErrorMessages.getInvalidFieldErr("State"),
					FileUploadEnums.Error.CRITICAL.toString());
		}
	}

	public static String getValueFromRowObj(JSONObject row, String fieldName) {
		return ((HashMap) row).get(fieldName) != null ? (String) ((HashMap) row).get(fieldName) : null;
	}

	public static int getRowNum(JSONObject row) {
		return (int) ((HashMap) row).get("Row #");
	}
}
