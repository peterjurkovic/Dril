package sk.peterjurkovic.dril.v2.activities;

import java.util.ArrayList;
import java.util.List;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.dto.State;
import sk.peterjurkovic.dril.model.Language;
import sk.peterjurkovic.dril.model.SpinnerState;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class RegistrationActivity extends BaseActivity {

	private EditText loginField;
	private EditText emailField;
	private EditText passwordField;
	private EditText confirmPasswordField;
	private Spinner localeSpinner;
	private Spinner targetLocaleSpinner;
	private EditText firstName;
	private EditText lastName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);
		
		
		loginField = (EditText) findViewById(R.id.login_field);
		emailField = (EditText) findViewById(R.id.email_field);
		passwordField = (EditText) findViewById(R.id.password_field);
		confirmPasswordField = (EditText) findViewById(R.id.confirm_password_field);
		localeSpinner = (Spinner) findViewById(R.id.locale_field);
		targetLocaleSpinner = (Spinner) findViewById(R.id.target_locale_field);
		firstName = (EditText) findViewById(R.id.first_name_field);
		lastName = (EditText) findViewById(R.id.last_name_field);
		
		ArrayAdapter<State> adapter = languageAdapter();
		localeSpinner.setAdapter(adapter);
		targetLocaleSpinner.setAdapter(adapter);
		
	}
	
	protected ArrayAdapter<State> languageAdapter(){
		return getAddpter( Language.getAllStates() );
	}
	
	protected ArrayAdapter<State> getAddpter(List<SpinnerState> itemList){
		List<State> list = new ArrayList<State>(itemList.size());
		for(SpinnerState lang : itemList){
			list.add( new State(lang.getId(), context.getResources().getString(lang.getResource())));
		}
		ArrayAdapter<State> adapter =  new ArrayAdapter<State>(this,  R.layout.v2_spinner,  list);
		adapter.setDropDownViewResource(R.layout.v2_spinner_dropdown);	
		return adapter;
	}
}
