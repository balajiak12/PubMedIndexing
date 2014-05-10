package edu.iub.pubmed.model;

public class Keyword {
	
	private String kwdGroupType;
	private String keywordText;

	public Keyword() {
	}

	public Keyword(String kwdGroupType, String keywordText) {
		this.kwdGroupType = kwdGroupType;
		this.keywordText = keywordText;
	}

	public String getKwdGroupType() {
		return this.kwdGroupType;
	}

	public void setKwdGroupType(String kwdGroupType) {
		this.kwdGroupType = kwdGroupType;
	}

	public String getKeywordText() {
		return this.keywordText;
	}

	public void setKeywordText(String keywordText) {
		this.keywordText = keywordText;
	}

}