package main.java.validation;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.model.FileResults;
import main.java.util.ObjectUtil;

public class ContibutionsValidator {
	static final Logger log = LoggerFactory.getLogger(ContibutionsValidator.class);
	
	private static FileResults fileResults;
	
	ContibutionsValidator (FileResults fileResults){
		this.fileResults = fileResults;
	}
	
	public static void ssn(String val) {
		
		// Check if exists in PARIS
		// The SSN does not match the SSN we have on record. Please validate your file and contact customer service if our records are incorrect.
		// Severity- Warning
		
	}
	
	
}
