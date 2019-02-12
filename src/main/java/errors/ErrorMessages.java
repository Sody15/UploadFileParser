package main.java.errors;

public class ErrorMessages {
	
	// Static Errors
	public static final String NO_MAPPING = "No Mapping Found.";
	public static final String GENERIC = "Error parsing field.";
	public static final String NO_MAPPING_COLUMN = "No Mapping for this Column.";
	public static final String FILE_TOTAL_GREATER_THAN_ZERO = "File Total must be greater than $0.00.";
	public static final String AT_LEAST_ONE_SOURCE = "You must have at least one source on your contribution file.";
	public static final String ZIP_CONTAIN_5_CHARACTERS= "ZIP must contain 5 characters.";
	public static final String ZIP_CONTAIN_9_CHARACTERS= "ZIP must contain 9 characters.";
	public static final String ZIP_NO_GREATER_THAN_9= "ZIP must contain no greater than 9 characters.";

	// Errors with values
	private static final String REQUIRED_HEADER = "VALUE is a required Header Field.";
	private static final String REQUIRED_FIELD = "Missing value for required field- VALUE.";
	private static final String INCORRECT_FORMAT = "VALUE not formatted correctly.";
	private static final String VALUE_NOT_VALID = "VALUE value not valid.";
	private static final String KEY_TERM_NO_MAPPING = "Key Term- KEY- Couldn't map your field- VALUE.";
	private static final String SOURCE_NOT_AVAILABLE = "This source isn't available on this plan- VALUE.";
	
	// Census Validation Errors
	public static final String FULL_NAME_MAX_CHARACTER = "Combination of First and Last Name is limited to 30 characters.";
	private static final String INVALID_FIELD = "VALUE is Invalid.";
	public static final String STATUS_QDRO_BENEFICIARY_DECEASED = "Status of QDRO/Beneficiary/Deceased requires further action. "
			+ "Contact EPAC Customer Service to address (see left sidebar for contact info).";
	private static final String DATE_IS_INVALID = "Invalid Date- VALUE. Couldn't parse your field. Dates need to be in mm/dd/yyyy format.";
	private static final String MAX_CHARACTER_LIMIT = "The KEY field is limited to VALUE characters."; 
	private static final String PO_BOX = "VALUE is invalid. PO Box's are not allowed. It must be a street address.";
	private static final String NO_SPECIAL_CHARACTERS = "VALUE is invalid. Special Characters are not allowed.";
	public static final String TERMINATION_DATE_ONLY_APPLICABLE = "Termination Date is only applicable for the status Terminated & Retired.";
	
	public static String getRequiredHeaderErr(String val) {
		return REQUIRED_HEADER.replace("VALUE", val);
	}
	public static String getRequiredFieldErr(String val) {
		return REQUIRED_FIELD.replace("VALUE", val);
	}
	public static String getInCorrectFormatErr(String val) {
		return INCORRECT_FORMAT.replace("VALUE", val);
	}
	public static String getValueNotValidErr(String val) {
		return VALUE_NOT_VALID.replace("VALUE", val); 
	}
	public static String getKeyTermNoMappingErr(String key, String val) {
		return KEY_TERM_NO_MAPPING.replace("KEY", key).replace("VALUE", val);
	}
	public static String getSourceNotAvailErr(String val) {
		return SOURCE_NOT_AVAILABLE.replace("VALUE", val);
	}
	public static String getDateInvalidErr(String val) {
		return DATE_IS_INVALID.replace("VALUE", val);
	}
	public static String getMaxCharacterErr(String key, int val) {
		return MAX_CHARACTER_LIMIT.replace("KEY", key).replace("VALUE", Integer.toString(val));
	}
	public static String getPOBoxErr(String val) {
		return PO_BOX.replace("VALUE", val);
	}
	public static String getNoSpecialCharactersErr(String val) {
		return NO_SPECIAL_CHARACTERS.replace("VALUE", val);
	}
	public static String getInvalidFieldErr(String val) {
		return INVALID_FIELD.replace("VALUE", val);
	}
}
