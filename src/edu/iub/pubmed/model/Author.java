package edu.iub.pubmed.model;
public class Author {

	private String givenName;
	private String surname;

	public Author() {
	}

	public Author(String givenName, String surname) {
		this.givenName = givenName;
		this.surname = surname;
	}

	public String getGivenName() {
		return this.givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getSurname() {
		return this.surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

}