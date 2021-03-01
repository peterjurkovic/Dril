package sk.peterjurkovic.dril.dto;

import sk.peterjurkovic.dril.model.Language;

/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Nov 6, 2013
 *
 */
public class WordToPronauceDto {
	
	private String value;
	
	private Language language;
	
	private boolean showFailureToast = true;

	
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

	public boolean isShowFailureToast() {
		return showFailureToast;
	}

	public void setShowFailureToast(boolean showFailureToast) {
		this.showFailureToast = showFailureToast;
	}

	
	
	
	
	
	
}
