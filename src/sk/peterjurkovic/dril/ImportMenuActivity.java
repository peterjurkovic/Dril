package sk.peterjurkovic.dril;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImportMenuActivity extends MainActivity {
	
	  
  long lectureId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.import_menu_layout);
	
	    Intent i = getIntent();
	    
	    lectureId = i.getLongExtra(EditLectureActivity.EXTRA_LECTURE_ID, 0);
	    
	    ((TextView)findViewById(R.id.importLectureName))
	    		.setText(WordActivity.getLectureName(this, lectureId));
	    
	    ImageButton goHome = (ImageButton) findViewById(R.id.home);
	      goHome.setOnClickListener(new View.OnClickListener() {
	          public void onClick(View v) {
	              startActivity( new Intent(ImportMenuActivity.this, DashboardActivity.class) );
	          }
	  });
	    
	    // IMPORT VIA CSV
	    LinearLayout importViaCsvBtn = (LinearLayout)findViewById(R.id.importViaCsv);
	    
	    importViaCsvBtn.setOnClickListener(new View.OnClickListener() {
	          public void onClick(View v) {
		        	Intent i = new Intent(ImportMenuActivity.this,  ImportCsvActivity.class);
		      		i.putExtra(EditLectureActivity.EXTRA_LECTURE_ID, lectureId);
		      		startActivity(i);
	          }
	    });
	    
	    // IMPORT VIA drilapp.com
	    
	    LinearLayout importViaIdBtn = (LinearLayout)findViewById(R.id.importViaId);
	    
	    importViaIdBtn.setOnClickListener(new View.OnClickListener() {
	          public void onClick(View v) {
		        	Intent i = new Intent(ImportMenuActivity.this,  ImportIdActivity.class);
		      		i.putExtra(EditLectureActivity.EXTRA_LECTURE_ID, lectureId);
		      		startActivity(i);
	          }
	    });
	      
	    }  
	  	
	  		
	
}
