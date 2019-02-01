package main.java.errors;

public class UploadError {
	private String fieldName;
	private String type;
	private String errorMsg;
	private String columnNum;
	private String excelColumnNum;
	private String rowNum;
	private String severity;
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getColumnNum() {
		return columnNum;
	}
	public void setColumnNum(String columnNum) {
		this.columnNum = columnNum;
	}
	public String getRowNum() {
		return rowNum;
	}
	public void setRowNum(String rowNum) {
		this.rowNum = rowNum;
	}
	public String getExcelColumnNum() {
		return excelColumnNum;
	}
	public void setExcelColumnNum(String excelColumnNum) {
		this.excelColumnNum = excelColumnNum;
	}
}
