package sk.peterjurkovic.dril;

import sk.peterjurkovic.dril.db.LectureDBAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class EditLectureActivity extends MainActivity {
	
	private long lectureId;
	
	public static final String EXTRA_LECTURE_NAME = "lecture_name";
	
	public static final String EXTRA_LECTURE_ID = "lecture_id";
	
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        lectureId = getIntent().getLongExtra( EXTRA_LECTURE_ID , -1);
        setContentView(R.layout.lecture_edit_activity);
        
        loadLectureData();
        
        
        Button submit = (Button)findViewById(R.id.submitLectureEdit);
        Button cancel = (Button)findViewById(R.id.cancelLectureEdit);
        
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	onSubmitEditLectureClicked();
            }
        });
        
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	onCancelEditLectureClicked();
            	
            }
        });
        
        ImageButton goHome = (ImageButton) findViewById(R.id.home);
        goHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity( new Intent(EditLectureActivity.this, DashboardActivity.class) );
            }
        });
    }
	
	
	 public void onSubmitEditLectureClicked(){
        String lectureName = ((EditText)findViewById(R.id.editLectureName))
        													.getText().toString();
        if(lectureName.length() == 0) return;
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
			Log.d("EditBookFragment", "ERROR: " + e.getMessage());
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
	
	
	
	
}
