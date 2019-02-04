package main.java.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import main.java.model.KeyTerm;
import main.java.web.Application;
import main.java.errors.ErrorMessages;
import main.java.model.FileResults;
import main.java.model.HeaderField;
import main.java.model.HeaderMapping;

@Scope("session")
@Component
public class RegexMapper {

	static final Logger log = LoggerFactory.getLogger(RegexMapper.class);

	// File Mappings
	private static HashMap<String, List<HeaderMapping>> fileMappings = new HashMap<String, List<HeaderMapping>>();

	// Required Fields
	private static HashMap<String, List<String>> requiredFields = new HashMap<String, List<String>>();

	PrintWriter writer;

	// Read YAML files, create Regex Patterns, and put into session
	public RegexMapper() throws JsonParseException, JsonMappingException, IOException {
		log.info("Inside RegexMapper Initialize");

		// Get YAML Files
		HashMap<String, File> files = new HashMap<String, File>();
		files.put(FileUploadEnums.UploadType.CONTRIBUTION.toString(),
				new ClassPathResource("config//contributions.yaml").getFile());
		files.put(FileUploadEnums.UploadType.CENSUS.toString(), new ClassPathResource("config//census.yaml").getFile());
		files.put(FileUploadEnums.UploadType.COMPLIANCE.toString(),
				new ClassPathResource("config//compliance.yaml").getFile());

		// Read YAML Files and Create HeaderMapping Lists
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		List<HeaderMapping> headerMappingList;
		Iterator it = files.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			String fileType = (String) pair.getKey();
			File file = (File) pair.getValue();
			headerMappingList = mapper.readValue(file, new TypeReference<List<HeaderMapping>>() {
			});
			RegexMapper.fileMappings.put(fileType, headerMappingList);
		}

		writer = new PrintWriter("RegexPatterns.txt", "UTF-8");

		// Create Regex Patterns
		createRegexPatternsForMappings(RegexMapper.fileMappings);

		writer.close();
	}

	private void createRegexPatternsForMappings(HashMap<String, List<HeaderMapping>> fileMappings) {
		Iterator it = fileMappings.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			String fileType = (String) pair.getKey();
			List<HeaderMapping> headerList = (List<HeaderMapping>) pair.getValue();
			for (HeaderMapping header : headerList) {
				// Headers
				String pattern = createRegexPattern(header.getAccept());
				header.setPattern(pattern);

				// Key Terms
				if (header.getKeyTerms() != null) {
					HashMap<String, String> keyTermsPatternMap = new HashMap<String, String>();
					List<KeyTerm> keyTerms = header.getKeyTerms();
					for (KeyTerm keyTerm : keyTerms) {
						pattern = createRegexPattern(keyTerm.getValues());
						keyTermsPatternMap.put(keyTerm.getName(), pattern);
					}
					header.setKeyTermPatterns(keyTermsPatternMap);
				}

				// Set Required Fields
				if (header.isRequired()) {
					List<String> requiredList;
					if (RegexMapper.requiredFields.get(fileType) != null) {
						requiredList = RegexMapper.requiredFields.get(fileType);
					} else {
						requiredList = new ArrayList<String>();
					}
					requiredList.add(header.getName());
					RegexMapper.requiredFields.put(fileType, requiredList);

				}
			}
		}
	}

	private String createRegexPattern(List<String> acceptedWords) {
		String pattern = "(?i)";
		for (String words : acceptedWords) {
			boolean exactMatch = false;
			// If contains asterisk, then Regex match should be only on that word/words
			if (words.contains("*")) {
				exactMatch = true;
			}
			// If not first iteration, add Or clause to pattern
			if (pattern != "(?i)") {
				pattern += "|";
			}
			// Match word wherever it's found
			if (!exactMatch) {
				String[] splitWords = words.split("\\s+");
				for (String word : splitWords) {
					// One Word
					if (splitWords.length == 1) {
						pattern += "\\b" + word + "\\b";
					}
					// Multiple words
					else {
						pattern += "(?=.*\\b" + word + "\\b)";
					}
				}
			}
			// Match exact word
			else {
				pattern += "^" + words.replace("*", "") + "$";
			}
		}
		// Write Regex Patterns to .txt file
		writer.println(pattern);

		return pattern;
	}

	// Check header mappings using Regex
	public HashMap<Integer, HeaderField> headerRowRegexCheck(HashMap<Integer, HeaderField> headerMap, String uploadType,
			FileResults fileResults) {

		List<HeaderMapping> uploadTypeList = RegexMapper.fileMappings.get(uploadType);
		List<String> headerRequiredList = RegexMapper.requiredFields.get(uploadType);

		List<String> headerList = new ArrayList<String>();

		int sourceCount = 0;

		// Iterate through header columns
		for (Integer columnNum : headerMap.keySet()) {
			HeaderField headerField = headerMap.get(columnNum);
			String headerStr = headerField.getUserInputValue().trim();

			boolean headerMatchFound = false;
			boolean sourceAvailable = false;
			boolean sourceNotFound = false;
			// Iterate through header Regex mappings looking for a match with header column
			// name
			for (HeaderMapping headerMapping : uploadTypeList) {
				Matcher restriction = Pattern.compile(headerMapping.getPattern()).matcher(headerStr);
				// Match found
				if (restriction.find()) {

					// If Source
					if (headerMapping.getSourceGroup() != null) {
						// Look to see if plan has source available
						for (String sourceId : headerMapping.getSourceIds()) {
							if (Application.planSourcesMap.get(sourceId) != null) {
								headerMatchFound = true;
								sourceAvailable = true;
								headerField.setSourceGroup(headerMapping.getSourceGroup());
								headerField.setSourceIds(headerMapping.getSourceIds());
								headerField.setSourceId(sourceId);
								headerField.setName(headerMapping.getName());
								headerField.setType(headerMapping.getType());
								headerField.setRequired(headerMapping.isRequired());
								headerList.add(headerMapping.getName());
								sourceCount++;
								break;
							}
						}
						// If source isn't available for plan, add error
						if (!sourceAvailable) {
							fileResults.addUploadError(true, 0, columnNum,
									ErrorMessages.getSourceNotAvailErr(headerMapping.getName()),
									FileUploadEnums.Error.CRITICAL.toString());
						}
					}
					// Not Source
					else {
						headerMatchFound = true;
						headerField.setName(headerMapping.getName());
						headerField.setType(headerMapping.getType());
						headerField.setRequired(headerMapping.isRequired());
						// If Key Term
						if (headerMapping.getKeyTermPatterns() != null) {
							headerField.setKeyTermPatterns(headerMapping.getKeyTermPatterns());
						}
						headerList.add(headerMapping.getName());
					}

				}
				headerMap.put(columnNum, headerField);
			}
			// If no match was found add an error
			if (!headerMatchFound && (sourceNotFound != true)) {
				headerField.setName(ErrorMessages.NO_MAPPING);
				fileResults.addUploadError(true, 0, columnNum, ErrorMessages.NO_MAPPING_COLUMN,
						FileUploadEnums.Error.WARNING.toString());
			}
		}

		// If file type is contribution, make sure at least one source is on file, else
		// add error
		if (FileUploadEnums.UploadType.CONTRIBUTION.toString().equals(uploadType) && sourceCount == 0) {
			fileResults.addUploadError(true, 0, 0, ErrorMessages.AT_LEAST_ONE_SOURCE,
					FileUploadEnums.Error.CRITICAL.toString());
		}

		fileResults.setHeaderList(new ArrayList<HeaderField>(headerMap.values()));

		// Check for required header fields
		for (String requiredHeaderName : headerRequiredList) {
			// If required field isn't part of header- add upload error
			if (!headerList.contains(requiredHeaderName)) {
				fileResults.addUploadError(true, 0, 0, ErrorMessages.getRequiredHeaderErr(requiredHeaderName),
						FileUploadEnums.Error.CRITICAL.toString());
			}
		}

		return headerMap;
	}

}