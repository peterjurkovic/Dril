package sk.peterjurkovic.dril.model;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import sk.peterjurkovic.dril.R;

public enum Language {
	
	ENGLISH(1, R.string.label_lang_en, Locale.US),
	GERMAN(2, R.string.label_lang_de, Locale.GERMAN),
	FRENCH(3, R.string.label_lang_fr , Locale.FRENCH),
	SPANISH(4, R.string.label_lang_es, new Locale("es")),
	SLOVAK(5, R.string.label_lang_sk, new Locale("sk")),
	CZECH(6, R.string.label_lang_cs, new Locale("cs")),
	SWEDISH(7, R.string.label_lang_sv, new Locale("sv"));
	
	private int id;
	private int resource;
	private Locale locale;
	
	
	private Language(int id, int resource, Locale locale){
		this.id = id;
		this.resource = resource;
		this.locale = locale;
	}


	public int getId() {
		return id;
	}


	public int getResource() {
		return resource;
	}


	public Locale getLocale() {
		return locale;
	}
	
	
	public static List<Language> getAll(){
		return Arrays.asList(values());
	}

	public static Language getById(int id) {
        for (Language i : getAll()) {
            if (i.getId() == id) {
                return i;
            }
        }
        return null;
    }
	
	
}

