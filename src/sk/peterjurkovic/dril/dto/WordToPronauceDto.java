package sk.peterjurkovic.dril.dto;

import sk.peterjurkovic.dril.model.Language;

public class WordToPronauceDto {
	
	private String value;
	
	private Language language;

	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	
	
	
	
	
	
}
