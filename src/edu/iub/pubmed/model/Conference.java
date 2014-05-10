package edu.iub.pubmed.model;
import java.util.Date;

public class Conference {

	private Date date;
	private String name;
	private String num;
	private String loc;
	private String sponsor;
	private String theme;
	private String acronym;
	private String fullName;

	public Conference() {
	}

	public Conference(Date date, String name, String num, String loc,
			String sponsor, String theme, String acronym, String fullName) {
		this.date = date;
		this.name = name;
		this.num = num;
		this.loc = loc;
		this.sponsor = sponsor;
		this.theme = theme;
		this.acronym = acronym;
		this.fullName = fullName;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNum() {
		return this.num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getLoc() {
		return this.loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public String getSponsor() {
		return this.sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	public String getTheme() {
		return this.theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getAcronym() {
		return this.acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

}
