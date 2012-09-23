package sk.peterjurkovic.dril;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class MainActivity extends  Activity{
	
	
	protected static final int MENU_ACTION_ABOUT = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.main_menu, menu);
	return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    Intent i;
	    switch (item.getItemId()) {
	    case R.id.menu_about:
	        i = new Intent(this, AboutActivity.class);
	        startActivityForResult(i, MENU_ACTION_ABOUT);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == MENU_ACTION_ABOUT && resultCode == RESULT_OK){}
        super.onActivityResult(requestCode, resultCode, data);
    }

	
}

