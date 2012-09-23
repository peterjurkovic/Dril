package sk.peterjurkovic.dril;

import sk.peterjurkovic.dril.db.WordDBAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class DashboardActivity extends MainActivity {
	
	
	Button btnStart = null;
	
	WordDBAdapter wordAdapter = null;
	
	long countOfActiveWords = 0;
	
	int pos = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dashboard);
        wordAdapter = new WordDBAdapter(this);
        btnStart = (Button) findViewById(R.id.btn_start);
 
        Button btnBook = (Button) findViewById(R.id.btn_book);

        Button btn_stats = (Button) findViewById(R.id.btn_stats);
        
        Button btn_info = (Button) findViewById(R.id.btn_info);
 
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	
            	if(countOfActiveWords == 0){
            		Toast.makeText(getApplicationContext(), R.string.zero_cards_alert, Toast.LENGTH_SHORT).show();
            	
            	}else{
            		Intent i = new Intent(getApplicationContext(), DrilActivity.class);
            		startActivity(i);
            	}
            }
        });
        
        btnBook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            		Intent i = new Intent(getApplicationContext(), BookListActivity.class);
            		startActivity(i);
            }
        });
        
        btn_stats.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            		Intent i = new Intent(getApplicationContext(), SessionStatisticActivity.class);
            		startActivity(i);
            }
        });
        
        btn_info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
        		Intent i = new Intent(getApplicationContext(), InfoActivity.class);
        		startActivity(i);
	        }
	    });
        
        clearPreferencies();
        ImageButton goHome = (ImageButton) findViewById(R.id.home);
        goHome.setVisibility(View.INVISIBLE);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	updateCountOfActiveWords();  	
    }
      
    private void updateCountOfActiveWords(){
    	try{
    		countOfActiveWords = wordAdapter.getCountOfActiveWords();
    	}catch (Exception ex) {
    		ex.printStackTrace();
         } finally {
            wordAdapter.close();
        }
    }

    /* OPTION MENU ---------------------------- */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    
    private void clearPreferencies(){
    	 SharedPreferences sharedPreferences = getSharedPreferences(DrilActivity.STATISTIC_ID_KEY, MODE_PRIVATE);
         SharedPreferences.Editor editor = sharedPreferences.edit();
         editor.clear().commit();
    }
    
}
