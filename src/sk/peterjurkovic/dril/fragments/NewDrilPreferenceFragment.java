package sk.peterjurkovic.dril.fragments;

import java.util.List;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.SessionManager;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.model.DrilStrategy;
import sk.peterjurkovic.dril.model.Language;
import sk.peterjurkovic.dril.preferencies.IntListPreference;
import sk.peterjurkovic.dril.preferencies.PreferenceFragment;
import sk.peterjurkovic.dril.v2.constants.Constants;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class NewDrilPreferenceFragment extends PreferenceFragment{

	private SessionManager session;
	private Context context;
	
	 @Override
     public void onCreate(final Bundle savedInstanceState)
     {
         super.onCreate(savedInstanceState);
         context = getActivity();
         session = new SessionManager(context);
         
         PreferenceManager manager =  getPreferenceManager();
         manager.setSharedPreferencesMode(Context.MODE_PRIVATE);
         manager.setSharedPreferencesName(SessionManager.PREF_NAME);
         
         final PreferenceScreen root =  manager.createPreferenceScreen(context);
         if(session.isUserLoggedIn()){
        	  
        	 final PreferenceCategory dialogBasedPrefCat = new PreferenceCategory(context);
        	 
        	 root.addPreference(dialogBasedPrefCat);
        	 dialogBasedPrefCat.setPersistent(true);
        	 dialogBasedPrefCat.setTitle(R.string.pref_head_account);
        	 dialogBasedPrefCat.setKey(SessionManager.PREF_NAME);
        	 dialogBasedPrefCat.addPreference(createLimitPref());
        	 dialogBasedPrefCat.addPreference( createFirstNamePref() );
        	 dialogBasedPrefCat.addPreference( createLastNamePref() );
        	 addLangsPref(dialogBasedPrefCat);
         }
         
         final PreferenceCategory drilCategory = new PreferenceCategory(context);
         root.addPreference(drilCategory);
         drilCategory.setTitle("Dril");
         drilCategory.setKey(Constants.PREF_CATEG_DRIL);
         drilCategory.addPreference(createQuestionOrAnswerPref());
         drilCategory.addPreference( createInputAnserPref() );
         drilCategory.addPreference( createShowHelperPref() );
         setPreferenceScreen(root);
     }
	 
	 
	 private Preference createLimitPref(){
		 final String prefKey = SessionManager.KEY_WORD_LIMIT + "text";	
		 String value = "";
		 int wordLimit = session.getWordLimit();
		if(wordLimit == SessionManager.UNLIMITED){
			value = context.getString(R.string.unlimited);
		}else{
			value = String.valueOf(wordLimit);
		}
		 final long wordCount = new WordDBAdapter(context).getCountOfStoredWords();
		 Preference pref = new Preference(context);
		
		 pref.setEnabled(true);
		 pref.setSelectable(false);
		 pref.setTitle(context.getString(R.string.pref_word_limit, value));
		 pref.setSummary(context.getString(R.string.pref_word_limit_descr, wordCount));
		 pref.setKey(prefKey);
		 pref.setDefaultValue(SessionManager.DEFAULT_WORD_LIMIT + "");
		return pref;
	 }
	 
	 
	 private ListPreference createQuestionOrAnswerPref(){
		 List<DrilStrategy> list = DrilStrategy.getAll();
		 CharSequence[] entries = new CharSequence[list.size()];
		 CharSequence[] entryValues = new CharSequence[list.size()];
		 for(int i = 0; i < list.size(); i++){
			  entries[i] = context.getString(list.get(i).getResource());
			  entryValues[i] =  list.get(i).toString();
			 
		 }
		 ListPreference pref = new ListPreference(context);
		 pref.setDialogTitle(R.string.pref_label_test_descr_dialog);
		 pref.setKey(Constants.PREF_DRIL_STRATEGY);
		 pref.setDefaultValue(DrilStrategy.QUESTION.toString());
		 pref.setEntries(entries);
		 pref.setEntryValues(entryValues);
		 pref.setTitle(R.string.pref_label_test);
		 pref.setSummary(R.string.pref_label_test_descr);
		 return pref;
	 }
	 
	 
	 private CheckBoxPreference createShowHelperPref(){
		 CheckBoxPreference pref = new CheckBoxPreference(context);
		 pref.setKey(Constants.PREF_SHOW_HELPER);
		 pref.setDefaultValue(true);
		 pref.setTitle(R.string.pref_show_helper);
		 pref.setSummary(R.string.pref_show_helper_descr);
		 return pref;
	 }
	 
	 private CheckBoxPreference createInputAnserPref(){
		 CheckBoxPreference pref = new CheckBoxPreference(context);
		 pref.setKey(Constants.PREF_WRITE_ANSWER_KEY);
		 pref.setDefaultValue(true);
		 pref.setTitle(R.string.pref_input_answer);
		 pref.setSummary(R.string.pref_input_answer_descr);
		 return pref;
	 }
	 
	 private EditTextPreference createFirstNamePref(){
		 EditTextPreference pref = new EditTextPreference(context);
		 pref.setTitle(R.string.pref_first_name);
		 pref.setDialogTitle(R.string.pref_first_name);
		 pref.setDefaultValue("");
		 pref.getEditText().setText("");
		 pref.setKey(SessionManager.KEY_FIRST_NAME);
		 return pref;
	 }
	 
	 private EditTextPreference createLastNamePref(){
		 EditTextPreference pref = new EditTextPreference(context);
		 pref.setTitle(R.string.pref_last_name);
		 pref.setDialogTitle(R.string.pref_last_name);
		 pref.setDefaultValue("");
		 pref.getEditText().setText("");
		 pref.setKey(SessionManager.KEY_LAST_NAME);
		 return pref;
	 }
	 
	 
	 
	 private void addLangsPref(final PreferenceCategory categ){
		 final List<Language> langList = Language.getAll();
		 final String[] entries = new String[langList.size()];
		 final String[] entryValues = new String[langList.size()];
		 for(int i = 0; i < langList.size(); i++){
			  entries[i] = context.getString(langList.get(i).getResource());
			  entryValues[i] =  String.valueOf(langList.get(i).getId());
			 
		 }

		
		final IntListPreference questionLangPref = new IntListPreference(context);
		questionLangPref.setEntries(entries);
		questionLangPref.setEntryValues(entryValues);
		questionLangPref.setKey(SessionManager.KEY_LOCALE_ID);
		questionLangPref.setTitle(R.string.pref_locale);
		questionLangPref.setSummary(R.string.pref_locale_desc);
		questionLangPref.setPersistent(true);
		categ.addPreference(questionLangPref);
	
		final IntListPreference answerLangPref = new IntListPreference(context);
		answerLangPref.setEntries(entries);
		answerLangPref.setEntryValues(entryValues);
		answerLangPref.setKey(SessionManager.KEY_TARGET_LOCALE_ID);
		answerLangPref.setTitle(R.string.pref_target_locale);
		answerLangPref.setSummary(R.string.pref_target_locale_desc);
		categ.addPreference(answerLangPref);
		
	 }
}
