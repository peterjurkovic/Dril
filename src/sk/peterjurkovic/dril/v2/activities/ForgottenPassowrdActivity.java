package sk.peterjurkovic.dril.v2.activities;

import sk.peterjurkovic.dril.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ForgottenPassowrdActivity extends BaseActivity {

	private Button sendBtn;
	private Button backBtn;
	private EditText emailField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgotten_password_layout);

		sendBtn = (Button) findViewById(R.id.send);
		backBtn = (Button) findViewById(R.id.btnBackToLogin);
		emailField = (EditText) findViewById(R.id.email);
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			 
	            public void onClick(View view) {
	                Intent i = new Intent(context,LoginActivity.class);
	                startActivity(i);
	                finish();
	            }
	        });
	}
}
