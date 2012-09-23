package sk.peterjurkovic.dril;

import sk.peterjurkovic.dril.listener.OnAddWordListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class AddWordActivity 
					extends FragmentActivity 
					implements OnAddWordListener {
		
	public static final String EXTRA_QUESTION = "question";
	
	public static final String EXTRA_ANSWER = "answer";
	
	public static final String EXTRA_LECTURE_NAME = "lecturen_name";
	
	private String wordLabel;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_add_activity);
        
        Button cancel = (Button)findViewById(R.id.cancelAddQuestion);
        
        cancel.setVisibility(View.VISIBLE);
        
        
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
        
        wordLabel =  getString(R.string.word_add_label) + " " 
        		+ getIntent().getStringExtra(EXTRA_LECTURE_NAME);
        
        ImageButton goHome = (ImageButton) findViewById(R.id.home);
        goHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity( new Intent(AddWordActivity.this, DashboardActivity.class) );
            }
        });
    }



	@Override
	public void saveNewWord(String question, String answer) {
		Intent result = new Intent();
		result.putExtra(EXTRA_QUESTION, question);
		result.putExtra(EXTRA_ANSWER, answer);
		setResult(RESULT_OK, result);
		finish();
		
	}
	
	public String getWordLabel(){
		return wordLabel;
	}
	
	
	/* OPTION MENU ---------------------------------------- */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.main_menu, menu);
	return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_about:
	        startActivity(new Intent(this, AboutActivity.class));
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
    /* ENDOPTION MENU ---------------------------------------- */

}
