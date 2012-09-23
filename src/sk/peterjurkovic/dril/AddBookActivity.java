package sk.peterjurkovic.dril;




import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class AddBookActivity extends FragmentActivity{
	
	public static final String EXTRA_BOOK_NAME = "book_name";
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_add_activity);
        
        Button submit = (Button)findViewById(R.id.submitAdd);
        Button cancel = (Button)findViewById(R.id.cancelAdd);
        
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
                startActivity( new Intent(AddBookActivity.this, DashboardActivity.class) );
            }
        });

    }


	public void onAddBook(String name) {
		Intent result = new Intent();
		result.putExtra(EXTRA_BOOK_NAME, name);
		setResult(RESULT_OK, result);
		finish();
		
	}
	  
	public void onCancelAddClicked(){
		finish();
	}
	
	public void onSubmitAddClicked(){
	    String bookName = ((EditText)findViewById(R.id.addBookName)).getText().toString();
	    if(bookName.length() == 0) return;
	    onAddBook(bookName);
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
	    	Log.d("MAINACTIVITY", "starting abotu...");
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
    /* ENDOPTION MENU ---------------------------------------- */
}
