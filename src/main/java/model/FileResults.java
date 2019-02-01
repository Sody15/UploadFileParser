package main.java.model;

import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Row;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import main.java.reader.ExcelFile;
import main.java.util.FileUploadEnums;
import main.java.errors.UploadError;

public class FileResults {
	private String fileName;
	private int headerRowNum;
	private int lastRowNum;
	private int totalNumHeaderRows;
	private int totalNumDataRows;
	private ArrayList<HeaderField> headerList;
	private JSONArray jsonDataArray = new JSONArray();
	private String uploadType;
	private double fileTotalAmt = 0;
	private ArrayList<UploadError> uploadErrorList = new ArrayList<UploadError>();
	private String fileSeverity;
	private String timeTaken;
	private boolean canSubmitFile = true;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public JSONArray getJsonDataArray() {
		return jsonDataArray;
	}

	public void setJsonDataArray(JSONArray jsonDataArray) {
		this.jsonDataArray = jsonDataArray;
	}

	public int getHeaderRowNum() {
		return headerRowNum;
	}

	public void setHeaderRowNum(int headerRowNum) {
		this.headerRowNum = headerRowNum;
	}

	public String getUploadType() {
		return uploadType;
	}

	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}

	public ArrayList<HeaderField> getHeaderList() {
		return headerList;
	}

	public void setHeaderList(ArrayList<HeaderField> headerList) {
		this.headerList = headerList;
	}

	public int getTotalNumDataRows() {
		return totalNumDataRows;
	}

	public void setTotalNumDataRows(int totalNumDataRows) {
		this.totalNumDataRows = totalNumDataRows;
	}

	public double getFileTotalAmt() {
		return fileTotalAmt;
	}

	public void setFileTotalAmt(double fileTotalAmt) {
		this.fileTotalAmt = fileTotalAmt;
	}

	public ArrayList<UploadError> getUploadErrorList() {
		return uploadErrorList;
	}

	public void setUploadErrorList(ArrayList<UploadError> uploadErrorList) {
		this.uploadErrorList = uploadErrorList;
	}

	public void addToJsonDataArray(JSONObject jsonObj) {
		this.getJsonDataArray().add(jsonObj);
	}

	public void addToFileTotalAmt(Double amt) {
		this.setFileTotalAmt(this.getFileTotalAmt() + amt);
	}

	public int getTotalNumHeaderRows() {
		return totalNumHeaderRows;
	}

	public void setTotalNumHeaderRows(int totalNumHeaderRows) {
		this.totalNumHeaderRows = totalNumHeaderRows;
	}

	public void setTotalNumRows(int headerRowNum, int lastRowNum, int totalNumHeaderRows, int totalNumDataRows) {
		this.headerRowNum = headerRowNum;
		this.lastRowNum = lastRowNum;
		this.totalNumHeaderRows = totalNumHeaderRows;
		this.totalNumDataRows = totalNumDataRows;
	}

	public int getLastRowNum() {
		return lastRowNum;
	}

	public void setLastRowNum(int lastRowNum) {
		this.lastRowNum = lastRowNum;
	}

	public String getFileSeverity() {
		return fileSeverity;
	}

	public void setFileSeverity(String fileSeverity) {
		this.fileSeverity = fileSeverity;
	}

	public String getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(String timeTaken) {
		this.timeTaken = timeTaken;
	}

	public boolean canSubmitFile() {
		return canSubmitFile;
	}

	public void setCanSubmitFile(boolean canSubmitFile) {
		this.canSubmitFile = canSubmitFile;
	}
	
	public void addUploadError(boolean globalError, Row row, int columnIndex, String errorMsg, String severity) {
		UploadError uploadError = new UploadError();
		if (globalError) {
			uploadError.setRowNum("*");
		} else if (row != null) {
			uploadError.setRowNum(Integer.toString(row.getRowNum() + 1));
		}
		if (columnIndex != 0) {
			uploadError.setColumnNum(Integer.toString(columnIndex));
			if (ExcelFile.excelColMap.get(columnIndex) != null) {
				uploadError.setExcelColumnNum(ExcelFile.excelColMap.get(columnIndex));
			}
		}
		uploadError.setErrorMsg(errorMsg);
		uploadError.setSeverity(severity);
		if (severity.equalsIgnoreCase(FileUploadEnums.Error.CRITICAL.toString())) {
			this.setCanSubmitFile(false);
		}
		ArrayList<UploadError> uploadErrorList = this.getUploadErrorList();
		uploadErrorList.add(uploadError);
		this.setUploadErrorList(uploadErrorList);
	}
}