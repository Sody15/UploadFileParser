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
			for (Object jsonRowObj : fileResults.getJsonDataArray()) {
				jsonRowObj = (JSONObject) jsonRowObj;				
				for(Iterator iterator = ((HashMap) jsonRowObj).keySet().iterator(); iterator.hasNext();) {
				    String headerName = (String) iterator.next();
				    Object headerValue = ((HashMap) jsonRowObj).get(headerName);
				    
				}
			}
		}
		// Compliance Validation
		else if (FileUploadEnums.UploadType.COMPLIANCE.toString().equalsIgnoreCase(uploadType)) {
			
		}
	}
}
