package sk.peterjurkovic.dril.v2.activities;

import sk.peterjurkovic.dril.AddWordActivity;
import sk.peterjurkovic.dril.EditWordActivity;
import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.ViewWordActivity;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.fragments.AddWordFragment;
import sk.peterjurkovic.dril.fragments.EditWordFragment;
import sk.peterjurkovic.dril.fragments.ViewWordFragment;
import sk.peterjurkovic.dril.fragments.WordListFragment;
import sk.peterjurkovic.dril.listener.OnAddWordListener;
import sk.peterjurkovic.dril.listener.OnChangeWordStatusListener;
import sk.peterjurkovic.dril.listener.OnDeleteWordListener;
import sk.peterjurkovic.dril.listener.OnEditWordClickedListener;
import sk.peterjurkovic.dril.listener.OnEditWordListener;
import sk.peterjurkovic.dril.listener.OnShowWordListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WordActivity extends BaseActivity  implements OnAddWordListener,
									OnEditWordClickedListener,
									OnEditWordListener,
									OnShowWordListener,
									OnDeleteWordListener,
									OnChangeWordStatusListener{

	
	private static final int REQUEST_ADD_WORD = 0;
	private static final int REQUEST_EDIT_WORD = 1;
	private static final int REQUEST_VIEW = 2;
	public static final String LECTURE_ID_EXTRA = "fk_lecture_id";
	private boolean dualPane;
	public static final String TAG = "WordActivity";
	private long lectureId = -1;
	private String lectureName;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.v2_word_list_layout);
        
        lectureId = getIntent().getLongExtra(LECTURE_ID_EXTRA, -1);
        
        if(lectureId == -1) throw new Error("Lecture ID is not set.");
        
        dualPane = findViewById(R.id.right_column) != null;
        
        lectureName = getLectureName(this, lectureId);
        
	    ((TextView)findViewById(R.id.wordListLabel)).setText( lectureName );
        
        ((Button)findViewById(R.id.addNewWord)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddNewWordClicked();
			}
		});
	}
	
	/** ------------------------------------------------------------
	 * Router of activities results.
	 * 
	 * implemented operation:
	 * 
	 *  - REQUEST_ADD_WORD (0), save new word into lection 
	 *  - REQUEST_EDIT_WORD (1), save edited word into lection
	 * 
	 * @throws Error otherwise
	 */
	@Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
	 	if(resultCode != RESULT_OK) return;
	 	switch(requestCode){
	 		case REQUEST_ADD_WORD :
		 		saveNewWord(	
		 				data.getStringExtra(AddWordActivity.EXTRA_QUESTION),
		 				data.getStringExtra(AddWordActivity.EXTRA_ANSWER)
		 				);
	 		break;
		 	case REQUEST_EDIT_WORD :
		 		saveEditedWord(
		 				data.getLongExtra(EditWordActivity.EXTRA_WORD_ID, -1),
		 				data.getStringExtra(EditWordActivity.EXTRA_QUESTION),
		 				data.getStringExtra(EditWordActivity.EXTRA_ANSWER)
	 				);
	 		break;
		 	case REQUEST_VIEW :
		 			int action = data.getIntExtra(ViewWordActivity.ACTION, -1);
		 			long wordId = data.getLongExtra(EditWordActivity.EXTRA_WORD_ID, -1);
		 			switch(action){
		 				case ViewWordActivity.EVENT_EDIT :
		 					onEditWordClicked(wordId);
		 				break;
		 				case ViewWordActivity.EVENT_DELETE :
		 					onDeleteClicked(wordId);
		 				break; 		
		 			}
	 		break;
	 		default : 
	 			throw new Error("Unknown activity requestCode: " + requestCode);
	 	
	 	}
        super.onActivityResult(requestCode, resultCode, data);
    }
	
	
	private void onAddNewWordClicked() {
		if(dualPane){
			Bundle data = new Bundle();
			data.putString(AddWordActivity.EXTRA_LECTURE_NAME, lectureName);
            Fragment f = new AddWordFragment();
            f.setArguments(data);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.right_column, f);
            ft.addToBackStack(null);
            ft.commit();
	    }else{
	        Intent i = new Intent(this, AddWordActivity.class);
	        i.putExtra(AddWordActivity.EXTRA_LECTURE_NAME, lectureName);
	        startActivityForResult(i, REQUEST_ADD_WORD);
        }
	}
	
	/** ------------------------------------------------------------
	 * Select current lecture name from database 
	 * 
	 * @param long ID of given lecture
	 * @return name of current lecture
	 */
	public static String getLectureName(Context context, long lectureId ){
		WordDBAdapter wordDBAdapter = new WordDBAdapter( context );
		String lectureName = null;
		try{
			lectureName = wordDBAdapter.getLectureNameById(lectureId);
	    } catch (Exception e) {
			Log.d("getLectureName", "ERROR: " + e.getMessage());
		} finally {
			wordDBAdapter.close();
		}
		return lectureName;
	}
	
	
	public ViewWordFragment getViewWordFragment(){
		return (ViewWordFragment) getSupportFragmentManager().findFragmentByTag("rcTag");
	}
		
	public static void showToastMessage(Context ctx, int resourceId){
		Toast.makeText(ctx, resourceId, Toast.LENGTH_LONG).show();
	}
	
	public WordListFragment getWordListFragment(){
		return ((WordListFragment) getSupportFragmentManager().findFragmentById(
	            R.id.WordListFragment));
	}
	
	public void changeWordStatus(long wordId, int newStatusVal){
		getViewWordFragment().setWordStatus(wordId, newStatusVal);
		if(dualPane) 
			getWordListFragment().updateList();
	}
	
	@Override
	public void activeWord(long wordId) {
		changeWordStatus(wordId, ViewWordFragment.STATUS_ACTIVE);
	}

	@Override
	public void deactiveWord(long wordId) {
		changeWordStatus(wordId,ViewWordFragment.STATUS_ACTIVE );
		if(dualPane){ 
			getWordListFragment().updateList();
		}
	}

	@Override
	public void onDeleteClicked(long wordId) {
		WordListFragment wlf = getWordListFragment();
		wlf.deleteWord(wordId);
		wlf.updateList();
		if(dualPane){
			ViewWordFragment f = getViewWordFragment();
			if(f == null) throw new Error("ViewWordFragment not found.");
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.remove(f);
			ft.commit();
		}
	}

	@Override
	public void showWord(long wordId) {
		if(dualPane){
			Bundle data = new Bundle();
			data.putLong(ViewWordActivity.EXTRA_WORD_ID, wordId);
            Fragment f = new ViewWordFragment();
            f.setArguments(data);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.right_column, f, "rcTag");
            ft.addToBackStack(null);
            ft.commit();
		}else{
	        Intent i = new Intent(this, ViewWordActivity.class);
	        i.putExtra(ViewWordActivity.EXTRA_WORD_ID, wordId);
	        startActivityForResult(i, REQUEST_VIEW);
	    }
	}

	@Override
	public void saveEditedWord(long wordId, String question, String answer) {
		WordDBAdapter wordDBAdapter = new WordDBAdapter(this);
		boolean updated = false;
		try {
			updated = wordDBAdapter.updateWord(wordId, question, answer);
		} catch (Exception e) {
			Log.d(TAG, "ERROR: " + e.getMessage());
		} finally {
			wordDBAdapter.close();
		}
		if(updated){
			getWordListFragment().updateList();
			showToastMessage(this,  R.string.saved_ok);
		    if(dualPane){
		    	showWord( wordId );
		    }
		}else{
			showToastMessage(this,  R.string.saved_no);
		}
	}

	
	@Override
	public void onEditWordClicked(long wordId) {
		if(dualPane){
			Bundle data = new Bundle();
			data.putLong(EditWordActivity.EXTRA_WORD_ID, wordId);
            Fragment f = new EditWordFragment();
            f.setArguments(data);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.right_column, f);
            ft.addToBackStack(null);
            ft.commit();
		}else{
	        Intent i = new Intent(this, EditWordActivity.class);
	        i.putExtra(EditWordActivity.EXTRA_WORD_ID, wordId);
	        startActivityForResult(i, REQUEST_EDIT_WORD);
	    }
	}

	
	@Override
	public void saveNewWord(String question, String answer) {
		WordDBAdapter wordDBAdapter = new WordDBAdapter(this);
		long id = -1;
		try {
			id = wordDBAdapter.insertWord(lectureId, question, answer);
		} catch (Exception e) {
			Log.d(TAG, "ERROR: " + e.getMessage());
		} finally {
			wordDBAdapter.close();
		}
		if(id > -1){
			getWordListFragment().updateList();
			showToastMessage(this,  R.string.word_added);
		    if(dualPane){
		    	showWord( id );
		    }
		}else{
			showToastMessage(this,  R.string.word_not_added);
		}
	}
	
	public long getLectureId(){
		return lectureId;
	}
	
	

}
