package sk.peterjurkovic.dril;

import sk.peterjurkovic.dril.db.JSONParser;
import android.app.Activity;
import android.os.Bundle;

public class UpdateActivity extends Activity {
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_activity);
		
		JSONParser json = new JSONParser();
		json.parseBooks();
		
	}
	
	
	
}
