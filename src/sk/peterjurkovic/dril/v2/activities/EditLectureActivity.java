package sk.peterjurkovic.dril.v2.activities;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.LectureDBAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.analytics.tracking.android.Log;

/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Oct 24, 2013
 * @version 2.0
 *
 * */
public class EditLectureActivity extends BaseActivity {
	
	private long lectureId;
	private long bookId;
	
	public static final String EXTRA_LECTURE_NAME = "lecture_name";
	
	public static final String EXTRA_LECTURE_ID = "lecture_id";
	public static final String EXTRA_BOOK_ID_FK = "fk_book_id";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        lectureId = getIntent().getLongExtra( EXTRA_LECTURE_ID , -1);
        bookId = getIntent().getLongExtra(EXTRA_BOOK_ID_FK , -1);
        setContentView(R.layout.v2_lecture_edit_layout);
        
        loadLectureData();
                
        Button submit = (Button)findViewById(R.id.submitLectureEdit);
        Button cancel = (Button)findViewById(R.id.cancelLectureEdit);
        
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	onSubmitEditLectureClicked();
            }
        });
        
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	onCancelEditLectureClicked();
            	
            }
        });
        setGoHomePageListener();
    }
	
	
	 public void onSubmitEditLectureClicked(){
        String lectureName = ((EditText)findViewById(R.id.editLectureName))
        													.getText().toString();
        if(lectureName.length() == 0){
        	return;
        }
        final LectureDBAdapter db = new LectureDBAdapter(context);
	    if(!db.isLectureNameUniqe(bookId, lectureName, lectureId)){
	    	Toast.makeText(context, R.string.err_lecture_uniqe, Toast.LENGTH_LONG).show();
	    	return;
	    }
        onSaveEditedBook(lectureId , lectureName);
	 }
	 
	 
	 public void onSaveEditedBook(long lectureId, String lectureName) {
		Intent result = new Intent();
		result.putExtra(EXTRA_LECTURE_ID, lectureId);
		result.putExtra(EXTRA_LECTURE_NAME, lectureName);
		setResult(RESULT_OK, result);
		finish();
		
	}
	 
	 
	public void onCancelEditLectureClicked(){
		finish();
	}
	
	
	public void loadLectureData(){
	
		LectureDBAdapter lectureDBAdapter = new LectureDBAdapter( this );
		Cursor cursor = null;
		try{
			cursor = lectureDBAdapter.getLecture(lectureId);
			putLectureData( cursor );
	    } catch (Exception e) {
	    	Log.e(e);
		} finally {
			cursor.close();
			lectureDBAdapter.close();
		}
	}

	
	public void putLectureData( Cursor cursor){
		if(cursor.getCount() == 0){ 
			Toast.makeText(this, R.string.error_no_data, Toast.LENGTH_LONG).show();
			return;
		}
		cursor.moveToFirst();
		int lectureNameIndex = cursor.getColumnIndex( LectureDBAdapter.LECTURE_NAME );
		((EditText)findViewById(R.id.editLectureName))
								.setText(cursor.getString(lectureNameIndex));		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	       goToParentActivity();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onBackPressed() {
    	 goToParentActivity();
    }
	
}
