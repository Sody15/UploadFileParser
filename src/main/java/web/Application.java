package main.java.web;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("main.java")

public class Application {
	
	static final Logger log = LoggerFactory.getLogger(Application.class);
	
	public static HashMap<String, String> planSourcesMap;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("Application Started");
        
        // Load plan level indicators
        planSourcesMap = new HashMap<String, String>();
        planSourcesMap.put("{", "Roth Contributions");
        planSourcesMap.put("O", "Employee Salary Deferrals Pre Tax");
        planSourcesMap.put("V", "Employer");
    }
}