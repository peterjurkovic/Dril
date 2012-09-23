package sk.peterjurkovic.dril;

import sk.peterjurkovic.dril.fragments.ViewWordFragment;
import sk.peterjurkovic.dril.listener.OnChangeWordStatusListener;
import sk.peterjurkovic.dril.listener.OnDeleteWordListener;
import sk.peterjurkovic.dril.listener.OnEditWordClickedListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class ViewWordActivity extends FragmentActivity 
											implements
												OnEditWordClickedListener,
												OnDeleteWordListener,
												OnChangeWordStatusListener{
												
	
	public static final String EXTRA_WORD_ID = "wordId";
	
	public static final String ACTION = "eventAction"; 
	
	public static final int EVENT_EDIT = 1;
	
	public static final int EVENT_DELETE = 2;
	
	public static final int EVENT_ACTIVE = 3;
	
	public static final int EVENT_DEACTIVE = 4;
	
	long wordId; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        wordId = getIntent().getLongExtra(EXTRA_WORD_ID, -1);
        setContentView(R.layout.word_view_activity);
        
        ImageButton goHome = (ImageButton) findViewById(R.id.home);
        goHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity( new Intent(ViewWordActivity.this, DashboardActivity.class) );
            }
        });

    }
	
	public long getWordId() {
		return wordId;
	}
	

	@Override
	public void onEditWordClicked(long wordId) {
		finishActivity(wordId, EVENT_EDIT);
	}

	
	@Override
	public void onDeleteClicked(long wordId) {
		finishActivity(wordId, EVENT_DELETE);
		
	}
	
	@Override
	public void activeWord(long wordId) {
		setWordStatus(wordId, ViewWordFragment.STATUS_ACTIVE);
	}

	
	@Override
	public void deactiveWord(long wordId) {
		setWordStatus(wordId, ViewWordFragment.STATUS_DEACTIVE);
		
	}
	
	
	private void setWordStatus(long wordId, int newStatusVal){
		((ViewWordFragment) getSupportFragmentManager()
				.findFragmentById(R.id.ViewWordFragment))
				.setWordStatus(wordId, newStatusVal);
	}
	
	
	public void finishActivity(long wordId, int actionEvent){
		Intent result = new Intent();
		result.putExtra(ACTION, actionEvent);
		result.putExtra(EXTRA_WORD_ID, wordId);
		setResult(RESULT_OK, result);
		finish();
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
