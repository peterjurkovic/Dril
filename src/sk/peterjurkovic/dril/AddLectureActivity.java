package sk.peterjurkovic.dril;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class AddLectureActivity extends MainActivity {
	
	
	public static final String EXTRA_LECTURE_NAME = "lecture_name";
	public static final String EXTRA_BOOK_ID_FK = "fk_book_id";
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecture_add_activity);
        
        Button submit = (Button)findViewById(R.id.submitAddLecture);
        Button cancel = (Button)findViewById(R.id.cancelAddLecture);
        
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSubmitAddClicked();
            }
        });
        
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	onCancelAddClicked();
            }
        });
        
        ImageButton goHome = (ImageButton) findViewById(R.id.home);
        goHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity( new Intent(AddLectureActivity.this, DashboardActivity.class) );
            }
        });
    }


	public void onAddLecture(String name) {
		Intent result = new Intent();
		result.putExtra(EXTRA_LECTURE_NAME, name);
		setResult(RESULT_OK, result);
		finish();
		
	}
	  
	public void onCancelAddClicked(){
		finish();
	}
	
	public void onSubmitAddClicked(){
	    String lectureName = ((EditText)findViewById(R.id.addLectureName)).getText().toString();
	    if(lectureName.length() == 0) return;
	    onAddLecture(lectureName);
	}
}
