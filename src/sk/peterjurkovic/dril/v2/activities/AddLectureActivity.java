package sk.peterjurkovic.dril.v2.activities;


import sk.peterjurkovic.dril.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Oct 24, 2013
 * @version 2.0
 */
public class AddLectureActivity extends BaseActivity {
	
	public static final String EXTRA_LECTURE_NAME = "lecture_name";
	public static final String EXTRA_BOOK_ID_FK = "fk_book_id";
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_lecture_add_layout);
        
        Button submit = (Button)findViewById(R.id.submitAddLecture);
        Button cancel = (Button)findViewById(R.id.cancelAddLecture);
        
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
                onSubmitAddClicked();
            }
        });
        
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	onCancelAddClicked();
            }
        });
        setGoHomePageListener();
    }
    
    
    @Override
    public void onBackPressed() {
    	 goToParentActivity();
    }

    
	public void onAddLecture(String name) {
		Intent result = new Intent();
		result.putExtra(EXTRA_LECTURE_NAME, name);
		setResult(RESULT_OK, result);
		finish();
	}
	  
	public void onCancelAddClicked(){
		 goToParentActivity();
	}
	
	public void onSubmitAddClicked(){
	    String lectureName = ((EditText)findViewById(R.id.addLectureName)).getText().toString();
	    if(lectureName.length() == 0) return;
	    onAddLecture(lectureName);
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
	
}
