package sk.peterjurkovic.dril.v2.activities;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;


public class DashboardActivity extends BaseActivity{
	
	Button btnStart = null;
	WordDBAdapter wordAdapter = null;
	long countOfActiveWords = 0;
	private Context context;
	int pos = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.v2_dashboard);
		 wordAdapter = new WordDBAdapter(this);
	        btnStart = (Button) findViewById(R.id.btn_start);
	        
	        context = this;
	        
	        Button btnBook = (Button) findViewById(R.id.btn_book);

	        Button btn_stats = (Button) findViewById(R.id.btn_stats);
	        
	        Button btn_info = (Button) findViewById(R.id.btn_info);
	        
	        Button btn_update = (Button) findViewById(R.id.btn_update);
	        
	        btnBook.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            		Intent i = new Intent(context, BookListActivity.class);
	            		startActivity(i);
	            }
	        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.v2_main, menu);
		return true;
	}
}
