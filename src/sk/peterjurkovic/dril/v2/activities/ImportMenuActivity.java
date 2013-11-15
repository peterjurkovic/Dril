package sk.peterjurkovic.dril.v2.activities;


import sk.peterjurkovic.dril.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * 
 * @author Peter Jurkovič
 * @date Nov 14, 2013
 * @version 2.0
 */
public class ImportMenuActivity extends BaseActivity {
	
	public static final String EXTRA_ID = "extra_id";
	public static final String EXTRA_CREATE_LECTURE = "extra_only_words";
	public static final String EXTRA_IS_CSV = "extra_is_csv";
	
	private long id = 0;
	private boolean createLecture = false;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.v2_import_menu);
		
		 Intent i = getIntent();
		 id = i.getLongExtra(EXTRA_ID, 0);
		 createLecture = i.getBooleanExtra(EXTRA_CREATE_LECTURE, false);
		 context = this;
		 if(id != 0){
			 LinearLayout importViaCsvBtn = (LinearLayout)findViewById(R.id.importViaCsv);
	         importViaCsvBtn.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	        	 logAction("csv_import");
                 Intent i = new Intent(ImportMenuActivity.this, ImportFileActivity.class);
                 i.putExtra(EXTRA_IS_CSV, true);
                 startImportActivity(i);
	         }
	         });
	             
	         LinearLayout importViaIdBtn = (LinearLayout)findViewById(R.id.importViaId);     
	         importViaIdBtn.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	        	 logAction("import_id");
                 Intent i = new Intent(ImportMenuActivity.this, ImportWebActivity.class);
                 startImportActivity(i);
	         }
	         });
	         	         
	         LinearLayout importViaXlsBtn = (LinearLayout)findViewById(R.id.importViaXls);
	         importViaXlsBtn.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	        	 logAction("xls_import");
                 Intent i = new Intent(ImportMenuActivity.this, ImportFileActivity.class);
                 i.putExtra(EXTRA_IS_CSV, false);
                 startImportActivity(i);
	         }
	         });
		 }
	}
	
	
	private void startImportActivity(Intent intent){
		intent.putExtra(EXTRA_ID, id);
		intent.putExtra(EXTRA_CREATE_LECTURE, createLecture);
        startActivity(intent);
	}
	
	private void logAction(final String name){
		 EasyTracker tracker = EasyTracker.getInstance(context);
  		tracker.send(MapBuilder.createEvent(
  					"ui_action", 
  					"button_press", 
  					name, 
  					null)
  				.build());
	}
}
