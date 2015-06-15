package sk.peterjurkovic.dril.v2.activities;

import sk.peterjurkovic.dril.R;
import android.os.Bundle;
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
		
		/*
		loginField = findViewById(R.id.login_field);
		emailField
		passwordField;
		confirmPasswordField;
		localeSpinner;
		targetLocaleSpinner;
		firstName;
		lastName;
		*/
	}
}
