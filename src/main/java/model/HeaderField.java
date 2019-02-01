package main.java.model;

public class HeaderField extends HeaderMapping {
	private String userInputValue;
	private int columnNum;
	private String sourceId;
	
	public int getColumnNum() {
		return columnNum;
	}
	public void setColumnNum(int columnNum) {
		this.columnNum = columnNum;
	}
	public String getUserInputValue() {
		return userInputValue;
	}
	public void setUserInputValue(String userInputValue) {
		this.userInputValue = userInputValue;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
}
