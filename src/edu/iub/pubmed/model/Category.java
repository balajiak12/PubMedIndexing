package edu.iub.pubmed.model;

/**
 * @author Balaji
 */

public class Category {

	private String subject;
	private String seriesTitle;
	private String seriesText;

	public Category() {
	}

	public Category(String subject, String seriesTitle, String seriesText) {
		this.subject = subject;
		this.seriesTitle = seriesTitle;
		this.seriesText = seriesText;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSeriesTitle() {
		return this.seriesTitle;
	}

	public void setSeriesTitle(String seriesTitle) {
		this.seriesTitle = seriesTitle;
	}

	public String getSeriesText() {
		return this.seriesText;
	}

	public void setSeriesText(String seriesText) {
		this.seriesText = seriesText;
	}

}
