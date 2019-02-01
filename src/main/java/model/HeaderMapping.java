package main.java.model;

import java.util.HashMap;
import java.util.List;

public class HeaderMapping {
	private String name;
	private boolean required;
	private String type;
	private List<String> accept;
	private String pattern;
	
	// Key Terms fields
	private List<KeyTerm> keyTerms;
	private HashMap<String, String> keyTermPatterns;
	
	// Source fields
	private List<String> sourceIds;
	private String sourceGroup;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public List<KeyTerm> getKeyTerms() {
		return keyTerms;
	}
	public void setKeyTerms(List<KeyTerm> keyTerms) {
		this.keyTerms = keyTerms;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public List<String> getAccept() {
		return accept;
	}
	public void setAccept(List<String> accept) {
		this.accept = accept;
	}
	public HashMap<String, String> getKeyTermPatterns() {
		return keyTermPatterns;
	}
	public void setKeyTermPatterns(HashMap<String, String> keyTermPatterns) {
		this.keyTermPatterns = keyTermPatterns;
	}
	public String getSourceGroup() {
		return sourceGroup;
	}
	public void setSourceGroup(String sourceGroup) {
		this.sourceGroup = sourceGroup;
	}
	public List<String> getSourceIds() {
		return sourceIds;
	}
	public void setSourceIds(List<String> sourceIds) {
		this.sourceIds = sourceIds;
	}
	@Override
	public String toString() {
		return "HeaderMapping [sourceIds=" + sourceIds + "]";
	}
}
