package sk.peterjurkovic.dril;

import sk.peterjurkovic.dril.db.BookDBAdapter;
import sk.peterjurkovic.dril.db.StatisticDbAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class StatisticActivity extends  MainActivity{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics_layout);
			Button commonStats = (Button) findViewById(R.id.statsList);
		
		commonStats.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startListStatisticActivity();
			}
		});
		
		
		initStatistics();
	}
	
	
	
	public void putLectureData(Cursor cursor){
		if(cursor.getCount() == 0){ 
			Toast.makeText(this, R.string.error_no_data, Toast.LENGTH_LONG).show();
			return;
		}
		cursor.moveToFirst();
		
		int bookCountIndex = cursor.getColumnIndex( BookDBAdapter.BOOK_COUNT );
		int lectureCountIndex = cursor.getColumnIndex( BookDBAdapter.LECTURES_COUNT );
		int wordCountIndex = cursor.getColumnIndex( BookDBAdapter.WORD_COUNT );
		int activatedWordcountIndex = cursor.getColumnIndex( BookDBAdapter.ACTIVE_WORD_COUNT );
		int avgRateIndex = cursor.getColumnIndex( BookDBAdapter.AVG_RATE );
		int finishedIndex = cursor.getColumnIndex( BookDBAdapter.FINISHED );
		
		((TextView)findViewById(R.id.tableCountOfBooks))
								.setText(cursor.getString(bookCountIndex));	
		((TextView)findViewById(R.id.tableCountOfLectures))
								.setText(cursor.getString(lectureCountIndex));	
		((TextView)findViewById(R.id.tableCountOfWords))
								.setText(cursor.getString(wordCountIndex));	
		((TextView)findViewById(R.id.tableCountOfActiveWords))
								.setText(cursor.getString(activatedWordcountIndex));
		double avg = cursor.getDouble(avgRateIndex);
		avg = avg * 100;
		avg = Math.round(avg);
		((TextView)findViewById(R.id.tableRate))
								.setText((avg / 100) + "" );
		((TextView)findViewById(R.id.tableFinished))
								.setText(cursor.getString(finishedIndex));
		cursor.close();
	}
	
	
	
	public void initStatistics() {

		Cursor statsCursor = null;
	    StatisticDbAdapter statisticDbAdapter = new StatisticDbAdapter(this);
	    try{
	    	statsCursor = statisticDbAdapter.getStatistics();
		    putLectureData(statsCursor);
	    } catch (Exception e) {
			Log.d("SatisticActivity", "ERROR: " + e.getMessage());
		} finally {
			statisticDbAdapter.close();
		}
	    
	}
	

	private void startListStatisticActivity(){
		Intent i = new Intent(this, SessionStatisticActivity.class);
		startActivity(i);
	}
	

	
}

