package sk.peterjurkovic.dril;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class InfoActivity extends MainActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_activity);
		
		ImageButton goHome = (ImageButton) findViewById(R.id.home);
        goHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity( new Intent(InfoActivity.this, DashboardActivity.class) );
            }
        });
  
	}
	
}
