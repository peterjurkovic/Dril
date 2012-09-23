package sk.peterjurkovic.dril;

import sk.peterjurkovic.dril.adapter.StatisticAdapter;
import sk.peterjurkovic.dril.db.StatisticDbAdapter;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class SessionStatisticActivity extends ListActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.session_statistics_layout);
		initStatistics();
		
		Button commonStats = (Button) findViewById(R.id.statsCommon);
		
		commonStats.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startCommonStats();
			}
		});
		
		ImageButton goHome = (ImageButton) findViewById(R.id.home);
        goHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity( new Intent(SessionStatisticActivity.this, DashboardActivity.class) );
            }
        });
	}
	
	
	public void initStatistics() {

		Cursor listCursor = null;
	    StatisticDbAdapter statisticDbAdapter = new StatisticDbAdapter(this);
	    try{
	    	listCursor = statisticDbAdapter.getSessionsStatistics();
	    	StatisticAdapter statisticAdapter = new StatisticAdapter(this, listCursor, false);
		    setListAdapter(statisticAdapter);
	    } catch (Exception e) {
			Log.d("SatisticActivity", "ERROR: " + e.getMessage());
		} finally {
			statisticDbAdapter.close();
		}
	    
	}
	
	
	private void startCommonStats(){
		Intent i = new Intent(this, StatisticActivity.class);
		startActivity(i);
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
