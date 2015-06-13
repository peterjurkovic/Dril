package sk.peterjurkovic.dril.model;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import sk.peterjurkovic.dril.R;
import android.util.SparseArray;



public enum Language {
	
	ENGLISH(1, R.string.label_lang_en, Locale.UK),
	GERMAN(2, R.string.label_lang_de, Locale.GERMAN),
	FRENCH(3, R.string.label_lang_fr , Locale.FRENCH),
	SPANISH(4, R.string.label_lang_es, new Locale("es")),
	SLOVAK(5, R.string.label_lang_sk, new Locale("sk")),
	CZECH(6, R.string.label_lang_cs, new Locale("cs")),
	SWEDISH(7, R.string.label_lang_sv, new Locale("sv")),
	FINNISH(8, R.string.label_lang_fi, new Locale("fi")),
	ESPERANDO(9, R.string.label_lang_eo, new Locale("eo")),
	DANISH(10, R.string.label_lang_da, new Locale("da")),
	GREEK(11, R.string.label_lang_el, new Locale("el")),
	CROATIAN(12, R.string.label_lang_hr, new Locale("hr")),
	ITALIAN(13, R.string.label_lang_it, new Locale("it")),
	DUTCH(14, R.string.label_lang_nl, new Locale("nl")),
	POLISH(15, R.string.label_lang_pl, new Locale("pl")),
	SLOVENIAN(16, R.string.label_lang_sl, new Locale("sl")),
	PORTUGUESE(17, R.string.label_lang_pt, new Locale("pt"));
	
	private final int id;
	private final int resource;
	private final Locale locale;
	
	private final static SparseArray<Language> lookup = new SparseArray<Language>(17);
	
	static{
		for(Language l : values()){
			lookup.put(l.getId() - 1, l);
		}
	}
	
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
       return lookup.get(id - 1);
    }
	
	
}

