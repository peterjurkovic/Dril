package sk.peterjurkovic.dril.v2.activities;

import java.util.ArrayList;
import java.util.List;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.SessionManager;
import sk.peterjurkovic.dril.dto.State;
import sk.peterjurkovic.dril.model.Language;
import sk.peterjurkovic.dril.model.SpinnerState;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class ChooseLanguageActivity extends AppCompatActivity{
	
	private SessionManager session;
	private Spinner localeSpinner;
	private Spinner targetLocaleSpinner;
	private Button nextButton;
	
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_language);
		session = new SessionManager(this);
		final Context context = this;
		
		localeSpinner = (Spinner) findViewById(R.id.locale_field);
		targetLocaleSpinner = (Spinner) findViewById(R.id.target_locale_field);
		nextButton = (Button) findViewById(R.id.saveLangs);
		
		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				State localeId = (State)localeSpinner.getSelectedItem();
				State targetLocaleId = (State)targetLocaleSpinner.getSelectedItem();
				session.setLanguages(localeId.getId(), targetLocaleId.getId());
				Intent i = new Intent(context, DashboardActivity.class);
				startActivity(i);
				finish();		
			}
		});
		
		prepareSpinners();
	}
	
	
	
	protected void prepareSpinners(){
		final ArrayAdapter<State> adapter = getAddpter();
		localeSpinner.setAdapter(adapter);
		targetLocaleSpinner.setAdapter(adapter);
		localeSpinner.setSelection( Language.getByLocale( getResources().getConfiguration().locale).getId() - 1 );
	}
	
	protected ArrayAdapter<State> getAddpter(){
		List<SpinnerState> langs = Language.getAllStates();
		List<State> list = new ArrayList<State>(langs.size());
		for(SpinnerState lang : langs){
			list.add( new State(lang.getId(), getResources().getString(lang.getResource())));
		}
		ArrayAdapter<State> adapter =  new ArrayAdapter<State>(this,  R.layout.v2_spinner,  list);
		adapter.setDropDownViewResource(R.layout.v2_spinner_dropdown);	
		return adapter;
	}
}
