package main.java.validation;

import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import main.java.model.FileResults;
import main.java.util.FileUploadEnums;

@Scope("session")
@Component
public class Validator {
	static final Logger log = LoggerFactory.getLogger(Validator.class);
	
	public void validateFile(FileResults fileResults) {
		log.info("Inside validateFile method");
		String uploadType = fileResults.getUploadType();
		log.info("Upload Type - " + uploadType);
		// Contributions Validation
		if (FileUploadEnums.UploadType.CONTRIBUTION.toString().equalsIgnoreCase(uploadType)) {
			for (Object jsonRowObj : fileResults.getJsonDataArray()) {
				jsonRowObj = (JSONObject) jsonRowObj;
				for(Iterator iterator = ((HashMap) jsonRowObj).keySet().iterator(); iterator.hasNext();) {
				    String headerName = (String) iterator.next();
				    Object headerVal = ((HashMap) jsonRowObj).get(headerName);
				    
//				    if ("Social Security Number".equals(headerName)) {
//				    	ContibutionsValidator.ssn(headerVal.toString());
//				    }
//				    else if ("Contract Number".equals(headerName)) {
//				    	ContibutionsValidator.contractNum(headerVal.toString());
//				    }
				    
				}
			}
		}
		// Census Validation
		else if (FileUploadEnums.UploadType.CENSUS.toString().equalsIgnoreCase(uploadType)) {
			CensusValidator censusValidator = new CensusValidator(fileResults);
			// Iterate over all rows
			for (Object jsonRowObj : fileResults.getJsonDataArray()) {
				jsonRowObj = (JSONObject) jsonRowObj;
				// Iterate over each rows cells
				for(Iterator iterator = ((HashMap) jsonRowObj).keySet().iterator(); iterator.hasNext();) {
				    String headerName = (String) iterator.next();
				    Object value = ((HashMap) jsonRowObj).get(headerName);
				    if ("Address 1".equals(headerName) || "Address 2".equals(headerName)) {
				    	int characterLimit = "Address 1".equals(headerName) ? censusValidator.address1_MaxLength : censusValidator.address2_MaxLength;
				    	censusValidator.maxCharacterLimit((JSONObject)jsonRowObj, headerName, (String)value, characterLimit);
				    	censusValidator.poBoxCheck((JSONObject)jsonRowObj, headerName, (String)value);
				    	censusValidator.noSpecialCharacters((JSONObject)jsonRowObj, headerName, (String)value);
				    } else if ("Status".equals(headerName)) {
				    	censusValidator.statusValidation((JSONObject)jsonRowObj, headerName, (String)value, 
				    			fileResults.getTakeoverFile(), fileResults.getFinalCensus());
				    }
				}
				
				// Fullname Validation
				censusValidator.fullName((JSONObject)jsonRowObj);
			}
		}
		// Compliance Validation
		else if (FileUploadEnums.UploadType.COMPLIANCE.toString().equalsIgnoreCase(uploadType)) {
			
		}
	}
}
