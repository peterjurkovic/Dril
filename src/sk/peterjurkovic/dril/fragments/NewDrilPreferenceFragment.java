package sk.peterjurkovic.dril.fragments;

import java.util.List;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.SessionManager;
import sk.peterjurkovic.dril.model.DrilStrategy;
import sk.peterjurkovic.dril.model.Language;
import sk.peterjurkovic.dril.preferencies.PreferenceFragment;
import sk.peterjurkovic.dril.v2.constants.Constants;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceCategory;
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
         
                  
         final PreferenceScreen root =  getPreferenceManager().createPreferenceScreen(context);
         if(session.isLoggedIn()){
        	  
        	 final PreferenceCategory dialogBasedPrefCat = new PreferenceCategory(context);
        	 root.addPreference(dialogBasedPrefCat);
        	 dialogBasedPrefCat.setTitle("Account");
        	 dialogBasedPrefCat.setKey(SessionManager.PREF_NAME);
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
		 pref.setTitle("Your first name");
		 pref.setDialogTitle("Your first name");
		 pref.setDefaultValue("");
		 pref.getEditText().setText("");
		 pref.setKey(SessionManager.KEY_FIRST_NAME);
		 return pref;
	 }
	 
	 private EditTextPreference createLastNamePref(){
		 EditTextPreference pref = new EditTextPreference(context);
		 pref.setTitle("Your last name");
		 pref.setDialogTitle("Your last name");
		 pref.setDefaultValue("");
		 pref.getEditText().setText("");
		 pref.setKey(SessionManager.KEY_LAST_NAME);
		 return pref;
	 }
	 
	 
	 
	 private ListPreference addLangsPref(PreferenceCategory categ){
		 List<Language> langList = Language.getAll();
		 CharSequence[] entries = new CharSequence[langList.size()];
		 CharSequence[] entryValues = new CharSequence[langList.size()];
		 for(int i = 0; i < langList.size(); i++){
			  entries[i] = context.getString(langList.get(i).getResource());
			  entryValues[i] =  String.valueOf(langList.get(i).getId());
			 
		 }
		  
		 
		ListPreference questionLangPref = new ListPreference(context);
		questionLangPref.setEntries(entries);
		questionLangPref.setEntryValues(entryValues);
		questionLangPref.setDefaultValue(Language.ENGLISH.getId()+"");
		questionLangPref.setKey(SessionManager.KEY_LOCALE_ID);
		questionLangPref.setTitle("Your language");
		questionLangPref.setSummary("Your native language");
		categ.addPreference(questionLangPref);
		
		ListPreference anserLangPref = new ListPreference(context);
		anserLangPref.setEntries(entries);
		anserLangPref.setEntryValues(entryValues);
		anserLangPref.setDefaultValue(Language.ENGLISH.getId()+"");
		anserLangPref.setKey(SessionManager.KEY_TARGET_LOCALE_ID);
		anserLangPref.setTitle("Your target language");
		anserLangPref.setSummary("Which want you to learn");
		categ.addPreference(anserLangPref);
		
		return null;
	 }
}