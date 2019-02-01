package main.java.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import main.java.model.FileResults;
import main.java.reader.ExcelFile;
import main.java.util.RegexMapper;
import main.java.validation.Validator;

@Controller
@Scope("session")
public class FileController {
	
	static final Logger log = LoggerFactory.getLogger(FileController.class);
	
	@Autowired
	private RegexMapper regexMapper;
	@Autowired
	private ExcelFile excelFile;
	@Autowired
	private Validator validator;

	@RequestMapping(value={"", "/", "back"}, method = RequestMethod.GET)
    public String showHomePage(ModelMap model) throws JsonParseException, JsonMappingException, IOException{
    	log.info("Render Home Page");
        return "index";
    }
	
	@RequestMapping(value="/upload-file", method = RequestMethod.POST)
    public String uploadFile(ModelMap model, @RequestParam("file") MultipartFile file, 
    		@RequestParam String uploadType) {
		log.info("Inside uploadFile method");
		
		// Start timer
		StopWatch timer = new StopWatch();
		timer.start();
		log.info("Timer Started");
		
		FileResults fileResults = new FileResults();
		fileResults.setFileName(file.getOriginalFilename());
		fileResults.setUploadType(uploadType);
		
		// Read File
		excelFile.readExcel(file, fileResults, regexMapper);
		
		// File Validation
		validator.validateFile(fileResults);
		
		model.put("fileResults", fileResults);
		
		// Stop timer
		timer.stop();
		fileResults.setTimeTaken(timer.getTotalTimeSeconds() + " seconds");
		log.info("Timer- " + timer.getTotalTimeSeconds() + " seconds");
		
        return "fileResults";
    }
}
