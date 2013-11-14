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
import android.widget.Toast;

public class SessionStatisticActivity extends ListActivity {
	
	StatisticAdapter statisticAdapter = null;
	
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
		
	}
	
	
	/**
	 * Called when activity going to destroy
	 */
	@Override
	protected void onDestroy() {
		super.onStop();
		closeAdapterCursor();
	}
	
	
	/**
	 * If StatisticAdapters cursor is open, close it.
	 * 
	 */
	private void closeAdapterCursor(){
		try {
			if(statisticAdapter != null){
				if(!statisticAdapter.getCursor().isClosed())
					statisticAdapter.getCursor().close();
			}
		} catch (Exception e) {
			Log.d("", e.getMessage());
		}
	}
	
	public void initStatistics() {
		closeAdapterCursor();
		Cursor listCursor = null;
	    StatisticDbAdapter statisticDbAdapter = new StatisticDbAdapter(this);
	    try{
	    	listCursor = statisticDbAdapter.getSessionsStatistics();
	    	statisticAdapter = new StatisticAdapter(this, listCursor, false);
		    setListAdapter(statisticAdapter);
	    } catch (Exception e) {
			Log.d("SatisticActivity", "ERROR: " + e.getMessage());
		} finally {
			statisticDbAdapter.close();
		}
	    
	}
	
	
	/**
	 * Delete all statistic data from database and update list
	 * 
	 */
	public void resetStats() {
	    StatisticDbAdapter statisticDbAdapter = new StatisticDbAdapter(this);
	    try{
	    	statisticDbAdapter.deleteAll();
	    } catch (Exception e) {
			Log.d("SatisticActivity", "ERROR: " + e.getMessage());
		} finally {
			statisticDbAdapter.close();
		}
	    initStatistics();
	    Toast.makeText(this, R.string.deleted, Toast.LENGTH_LONG).show();
	}
	
	
	
	private void startCommonStats(){
		Intent i = new Intent(this, StatisticActivity.class);
		startActivity(i);
	}
	
	
	/* OPTION MENU ---------------------------------------- */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.stats_menu, menu);
	return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_about3:
	        startActivity(new Intent(this, AboutActivity.class));
	        return true;
	    case R.id.stats_reset:
	    	resetStats();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
    /* ENDOPTION MENU ---------------------------------------- */
	
}
