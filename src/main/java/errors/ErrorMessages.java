package main.java.errors;

public class ErrorMessages {
	
	// Static Errors
	public static final String NO_MAPPING = "No Mapping Found";
	public static final String GENERIC = "Error parsing field";
	public static final String NO_MAPPING_COLUMN = "No Mapping for this Column";
	public static final String FILE_TOTAL_GREATER_THAN_ZERO = "File Total must be greater than $0.00";
	public static final String AT_LEAST_ONE_SOURCE = "You must have at least one source on your contribution file";

	// Errors with values
	public static final String REQUIRED_HEADER = "VALUE is a required Header Field";
	public static final String REQUIRED_FIELD = "Missing value for required field- VALUE";
	public static final String NOT_CORRECT_FORMAT = "VALUE not formatted correctly";
	public static final String VALUE_NOT_VALID = "VALUE value not valid";
	public static final String KEY_TERM_NO_MAPPING = "Key Term- KEY- Couldn't map your field- VALUE";
	public static final String SOURCE_NOT_AVAILABLE = "This source isn't available on this plan- VALUE";
	
	public static String getRequiredHeaderErr(String val) {
		return REQUIRED_HEADER.replace("VALUE", val);
	}
	public static String getRequiredFieldErr(String val) {
		return REQUIRED_FIELD.replace("VALUE", val);
	}
	public static String getNotCorrectFormatErr(String val) {
		return NOT_CORRECT_FORMAT.replace("VALUE", val);
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
	
}
