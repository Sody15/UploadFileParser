package main.java.reader;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import main.java.model.FileResults;
import main.java.model.HeaderField;
import main.java.parser.CensusParser;
import main.java.parser.ContributionsParser;
import main.java.util.FileUploadEnums;
import main.java.errors.ErrorMessages;
import main.java.util.ObjectUtil;
import main.java.util.RegexMapper;

@Scope("session")
@Component
public class ExcelFile {
	static final Logger log = LoggerFactory.getLogger(ExcelFile.class);

	final static int CELL_TYPE_STRING = 1;
	final static int CELL_TYPE_DOUBLE = 0;

	final static DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	
	public static Map<Integer, String> excelColMap = createExcelColMap();

	private static Map<Integer, String> createExcelColMap() {
		Map<Integer, String> excelColMap = new HashMap<Integer, String>();
		excelColMap.put(1, "A");
		excelColMap.put(2, "B");
		excelColMap.put(3, "C");
		excelColMap.put(4, "D");
		excelColMap.put(5, "E");
		excelColMap.put(6, "F");
		excelColMap.put(7, "G");
		excelColMap.put(8, "H");
		excelColMap.put(9, "I");
		excelColMap.put(10, "J");
		excelColMap.put(11, "K");
		excelColMap.put(12, "L");
		excelColMap.put(13, "M");
		excelColMap.put(14, "N");
		excelColMap.put(15, "O");
		excelColMap.put(16, "P");
		excelColMap.put(17, "Q");
		excelColMap.put(18, "R");
		excelColMap.put(19, "S");
		excelColMap.put(20, "T");
		excelColMap.put(21, "U");
		excelColMap.put(22, "V");
		excelColMap.put(23, "W");
		excelColMap.put(24, "X");
		excelColMap.put(25, "Y");
		excelColMap.put(26, "Z");
		return excelColMap;
	}
	
	private static String convertToCurrency(double val) {
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		return formatter.format(val);
	}

	public void readExcel(MultipartFile file, FileResults fileResults, RegexMapper regexMapper) {
		log.info("Inside readExcel method");
		
		HashMap<Integer, HeaderField> headerMap = null;
		try {
			Workbook workbook = WorkbookFactory.create(file.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);
			
			if (sheet != null) {

				// Set Row Numbers
				setRowNums(sheet, fileResults);
	
				// Loop through each row to get data
				for (int rowNum = fileResults.getHeaderRowNum(); rowNum <= fileResults.getLastRowNum(); rowNum++) {
					// Get row
					Row row = sheet.getRow(rowNum - 1);
					// Header row
					if (rowNum == fileResults.getHeaderRowNum()) {
						headerMap = getHeaderRowValues(row);
						headerMap = regexMapper.headerRowRegexCheck(headerMap, fileResults.getUploadType(), fileResults);
					}
					// Data
					else if (rowNum > fileResults.getHeaderRowNum() && row != null && !isRowEmpty(row)) {
						JSONObject jsonObj = null;
						if (FileUploadEnums.UploadType.CONTRIBUTION.toString().equalsIgnoreCase(fileResults.getUploadType())) {
							jsonObj = getContributionDataRowValues(row, headerMap, fileResults);
						} else if (FileUploadEnums.UploadType.CENSUS.toString().equalsIgnoreCase(fileResults.getUploadType())) {
							jsonObj = getCensusDataRowValues(row, headerMap, fileResults);
						} else {
							jsonObj = getComplianceDataRowValues(row, headerMap, fileResults);
						}
						if (jsonObj != null) {
							fileResults.addToJsonDataArray(jsonObj);
						}
					}
				}
				
				// If contribution, make sure file total is greater than zero, if not then display error.
				if (FileUploadEnums.UploadType.CONTRIBUTION.toString().equalsIgnoreCase(fileResults.getUploadType()) 
						&& fileResults.getFileTotalAmt() == 0) {
					fileResults.addUploadError(true, 0, 0, 
							ErrorMessages.FILE_TOTAL_GREATER_THAN_ZERO,
							FileUploadEnums.Error.CRITICAL.toString());
				}
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// log.info(fileResults.getJsonDataArray().toJSONString());
	}

	// Method to set header row #, last row #, total headers rows, and total data rows
	private void setRowNums(Sheet sheet, FileResults fileResults) {
		int lastRowNum = sheet.getLastRowNum() + 1;
		boolean headerRowFound = false;

		int headerRowNum = 0, totalNumHeaderRows = 0, totalNumDataRows = 0;

		for (int x = 0; x < lastRowNum; x++) {
			// Get Row
			Row row = sheet.getRow(x);
			// Check if row is not empty
			if (row != null && !isRowEmpty(row)) {
				// Not header row
				if (headerRowFound) {
					totalNumDataRows++;
				} 
				// Header row
				else {
					headerRowNum = x + 1;
					headerRowFound = true;
					// Loop through cells in that row
					for (Cell cell : row) {
						// Check if cell is not empty
						if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
							totalNumHeaderRows++;
						}
					}
				}
			}
		}
		fileResults.setTotalNumRows(headerRowNum, lastRowNum, totalNumHeaderRows, totalNumDataRows);
	}

	// Check if row is empty or not
	public static boolean isRowEmpty(Row row) {
		for (int x = row.getFirstCellNum(); x < row.getLastCellNum(); x++) {
			Cell cell = row.getCell(x);
			if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
				return false;
			}
		}
		return true;
	}

	private static HashMap<Integer, HeaderField> getHeaderRowValues(Row row) {
		HashMap<Integer, HeaderField> headerMap = new HashMap<Integer, HeaderField>();
		if (row != null) {
			for (Cell cell : row) {
				if (cell.getCellType() == 1 && ObjectUtil.isNotNullOrEmpty(cell.getStringCellValue())) {
					HeaderField headerField = new HeaderField();
					headerField.setColumnNum(cell.getColumnIndex() + 1);
					headerField.setUserInputValue(cell.getStringCellValue());
					headerMap.put(headerField.getColumnNum(), headerField);
				}
			}
		}
		return headerMap;
	}
	
	private static JSONObject getContributionDataRowValues(Row row, HashMap<Integer, HeaderField> headerMap, FileResults fileResults) {
		// Get Row Number
		int rowNum = row.getRowNum() + 1;
		
		JSONObject rowObj = new JSONObject();
		rowObj.put("Row #", rowNum);

		String headerName = "";
		ContributionsParser contributionsParser = new ContributionsParser();
		for (Integer columnNum : headerMap.keySet()) {
			HeaderField headerField = headerMap.get(columnNum);
			headerName = headerField.getName();
			boolean hasMapping = !ErrorMessages.NO_MAPPING.equalsIgnoreCase(headerName);
			Cell cell = row.getCell(columnNum - 1);
			try {
				rowNum = row.getRowNum() + 1;
				String type = headerField.getType();
				
				// Cell is not Empty
				if (hasMapping) {
					if (cell != null) {
						String cellValue = "";
						// String
						if (cell.getCellType() == CELL_TYPE_STRING
								&& FileUploadEnums.HeaderType.STRING.toString().equalsIgnoreCase(type)) {
							cellValue = cell.getStringCellValue().trim().replaceAll("'", "");
						} 
						// Date
						else if (FileUploadEnums.HeaderType.DATE.toString().equalsIgnoreCase(type)) {
							if (cell.getDateCellValue() != null) {
								cellValue = df.format(cell.getDateCellValue());
							}
						} 
						// Currency
						else if (cell.getCellType() == CELL_TYPE_DOUBLE
								&& FileUploadEnums.HeaderType.CURRENCY.toString().equalsIgnoreCase(type)) {
							fileResults.addToFileTotalAmt(cell.getNumericCellValue());
							cellValue = convertToCurrency(cell.getNumericCellValue());
						}
						else if (cell.getCellType() == CELL_TYPE_STRING) {
							cellValue = cell.getStringCellValue().trim().replaceAll("'", "");
						} 
						else if (cell.getCellType() == CELL_TYPE_DOUBLE) {
							cellValue = Integer.toString((int) cell.getNumericCellValue());
						}
						
						// Parse Field
						contributionsParser = new ContributionsParser(headerField, cellValue, fileResults, row, columnNum, rowObj);
						contributionsParser.parseField();
					}
					else {
						rowObj.put(headerName, "");
						// If required field
						if (headerField.isRequired()) {
							fileResults.addUploadError(false, row.getRowNum() + 1, columnNum, 
									ErrorMessages.getRequiredFieldErr(headerName),
									FileUploadEnums.Error.CRITICAL.toString());
						}
					}
				}			
			}
			catch (Exception ex) {
				ex.printStackTrace();
				if (ex instanceof IllegalStateException || ex instanceof NumberFormatException 
						|| ex instanceof NullPointerException) {
					fileResults.addUploadError(false, row.getRowNum() + 1, columnNum,
							ErrorMessages.GENERIC,
							FileUploadEnums.Error.CRITICAL.toString());
				}
				if (hasMapping) {
					rowObj.put(headerName, "");
				}
			}
		}

		return rowObj;
	}

	private static JSONObject getCensusDataRowValues(Row row, HashMap<Integer, HeaderField> headerMap, FileResults fileResults) {
		// Get Row Number
		int rowNum = row.getRowNum() + 1;
		
		JSONObject rowObj = new JSONObject();
		rowObj.put("Row #", rowNum);

		String headerName = "";
		CensusParser censusParser = new CensusParser();
		for (Integer columnNum : headerMap.keySet()) {
			HeaderField headerField = headerMap.get(columnNum);
			headerName = headerField.getName();
			boolean hasMapping = !ErrorMessages.NO_MAPPING.equalsIgnoreCase(headerName);
			String type = null;
			Cell cell = null;
			try {
				rowNum = row.getRowNum() + 1;
				cell = row.getCell(columnNum - 1);
				type = headerField.getType();
				
				// Cell is not Empty
				if (hasMapping) {
					if (cell != null) {
						String cellValue = "";
						// String
						if (cell.getCellType() == CELL_TYPE_STRING
								&& FileUploadEnums.HeaderType.STRING.toString().equalsIgnoreCase(type)) {
							cellValue = cell.getStringCellValue().trim().replaceAll("'", "");
						} 
						// Date
						else if (FileUploadEnums.HeaderType.DATE.toString().equalsIgnoreCase(type)) {
							if (cell.getDateCellValue() != null) {
								DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
								cellValue = df.format(cell.getDateCellValue());
							}
						} 
						// Currency
						else if (cell.getCellType() == CELL_TYPE_DOUBLE
								&& FileUploadEnums.HeaderType.CURRENCY.toString().equalsIgnoreCase(type)) {
							fileResults.addToFileTotalAmt(cell.getNumericCellValue());
							cellValue = convertToCurrency(cell.getNumericCellValue());
						}
						else if (cell.getCellType() == CELL_TYPE_STRING) {
							cellValue = cell.getStringCellValue().trim().replaceAll("'", "");
						} 
						else if (cell.getCellType() == CELL_TYPE_DOUBLE) {
							cellValue = Integer.toString((int) cell.getNumericCellValue());
						}
						
						// Parse Field
						censusParser = new CensusParser(headerField, cellValue, fileResults, row, columnNum, rowObj);
						censusParser.parseField();
					}
					else {
						rowObj.put(headerName, "");
						// If required field
						if (headerField.isRequired()) {
							fileResults.addUploadError(false, row.getRowNum() + 1, columnNum, 
									ErrorMessages.getRequiredFieldErr(headerName),
									FileUploadEnums.Error.CRITICAL.toString());
						}
					}
				}			
			}
			catch (Exception ex) {
				ex.printStackTrace();
				// Invalid date
				if (hasMapping && cell != null && FileUploadEnums.HeaderType.DATE.toString().equalsIgnoreCase(type)) {
					String severity = headerField.isRequired() ? FileUploadEnums.Error.CRITICAL.toString() : FileUploadEnums.Error.WARNING.toString();
					fileResults.addUploadError(false, row.getRowNum() + 1, columnNum,
							ErrorMessages.getDateInvalidErr(headerName),
							severity);
				} else {
					fileResults.addUploadError(false, row.getRowNum() + 1, columnNum,
						ErrorMessages.GENERIC,
						FileUploadEnums.Error.CRITICAL.toString());
				}
				if (hasMapping) {
					rowObj.put(headerName, "");
				}
			}
		}

		return rowObj;
	}
	
	private static JSONObject getComplianceDataRowValues(Row row, HashMap<Integer, HeaderField> headerMap, FileResults fileResults) {
		// Get Row Number
		int rowNum = row.getRowNum() + 1;
		
		JSONObject rowObj = new JSONObject();
		rowObj.put("Row #", rowNum);

		String headerName = "";
		CensusParser censusParser = new CensusParser();
		for (Integer columnNum : headerMap.keySet()) {
			HeaderField headerField = headerMap.get(columnNum);
			headerName = headerField.getName();
			boolean hasMapping = !ErrorMessages.NO_MAPPING.equalsIgnoreCase(headerName);
			try {
				rowNum = row.getRowNum() + 1;
				Cell cell = row.getCell(columnNum - 1);
				String type = headerField.getType();
				
				// Cell is not Empty
				if (hasMapping) {
					if (cell != null) {
						String cellValue = "";
						// String
						if (cell.getCellType() == CELL_TYPE_STRING
								&& FileUploadEnums.HeaderType.STRING.toString().equalsIgnoreCase(type)) {
							cellValue = cell.getStringCellValue().trim().replaceAll("'", "");
						} 
						// Date
						else if (FileUploadEnums.HeaderType.DATE.toString().equalsIgnoreCase(type)) {
							if (cell.getDateCellValue() != null) {
								DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
								cellValue = df.format(cell.getDateCellValue());
							}
						} 
						// Currency
						else if (cell.getCellType() == CELL_TYPE_DOUBLE
								&& FileUploadEnums.HeaderType.CURRENCY.toString().equalsIgnoreCase(type)) {
							fileResults.addToFileTotalAmt(cell.getNumericCellValue());
							cellValue = convertToCurrency(cell.getNumericCellValue());
						}
						else if (cell.getCellType() == CELL_TYPE_STRING) {
							cellValue = cell.getStringCellValue().trim().replaceAll("'", "");
						} 
						else if (cell.getCellType() == CELL_TYPE_DOUBLE) {
							cellValue = Integer.toString((int) cell.getNumericCellValue());
						}
						
						// Parse Field
						censusParser = new CensusParser(headerField, cellValue, fileResults, row, columnNum, rowObj);
						censusParser.parseField();
					}
					else {
						rowObj.put(headerName, "");
						// If required field
						if (headerField.isRequired()) {
							fileResults.addUploadError(false, row.getRowNum() + 1, columnNum, 
									ErrorMessages.getRequiredFieldErr(headerName),
									FileUploadEnums.Error.CRITICAL.toString());
						}
					}
				}			
			}
			catch (Exception ex) {
				ex.printStackTrace();
				if (ex instanceof IllegalStateException || ex instanceof NumberFormatException 
						|| ex instanceof NullPointerException) {
					fileResults.addUploadError(false, row.getRowNum() + 1, columnNum,
							ErrorMessages.GENERIC,
							FileUploadEnums.Error.CRITICAL.toString());
				}
				if (hasMapping) {
					rowObj.put(headerName, "");
				}
			}
		}

		return rowObj;
	}

}
