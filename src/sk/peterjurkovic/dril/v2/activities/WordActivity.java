package sk.peterjurkovic.dril.v2.activities;


import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.fragments.AddWordFragment;
import sk.peterjurkovic.dril.fragments.EditWordFragment;
import sk.peterjurkovic.dril.fragments.WordListFragment;
import sk.peterjurkovic.dril.listener.OnAddWordListener;
import sk.peterjurkovic.dril.listener.OnEditWordClickedListener;
import sk.peterjurkovic.dril.listener.OnEditWordListener;
import sk.peterjurkovic.dril.listener.OnWordClickListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WordActivity extends BaseActivity implements OnAddWordListener,
		OnEditWordClickedListener, OnEditWordListener,  OnWordClickListener {

	public static final int REQUEST_ADD_WORD = 0;
	public static final int REQUEST_EDIT_WORD = 1;

	public static final String LECTURE_ID_EXTRA = "fk_lecture_id";
	public static final String LECTURE_NAME_EXTRA = "fk_lecture_name";
	
	private boolean dualPane = false;
	public static final String TAG = "WordActivity";
	private long lectureId = -1;
	private String lectureName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.v2_word_list_layout);

		if(savedInstanceState != null){
			lectureId = savedInstanceState.getLong(LECTURE_ID_EXTRA);
			lectureName = savedInstanceState.getString(LECTURE_NAME_EXTRA);
		}else{
			lectureId = getIntent().getLongExtra(LECTURE_ID_EXTRA, -1);
			if (lectureId == -1) {
				logException("Lecture ID is not set.", false);
				throw new Error("Lecture ID is not set.");
			}
			lectureName = getLectureName(this, lectureId);
		}
		((TextView) findViewById(R.id.wordListLabel)).setText(lectureName);

		((Button) findViewById(R.id.addNewWord))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onAddNewWordClicked();
					}
				});

	}

	/**
	 * ------------------------------------------------------------ Router of
	 * activities results.
	 * 
	 * implemented operation:
	 * 
	 * - REQUEST_ADD_WORD (0), save new word into lection - REQUEST_EDIT_WORD
	 * (1), save edited word into lection
	 * 
	 * @throws Error
	 *             otherwise
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case REQUEST_ADD_WORD:
			saveNewWord(data.getStringExtra(AddWordActivity.EXTRA_QUESTION),
					data.getStringExtra(AddWordActivity.EXTRA_ANSWER));
			break;
		case REQUEST_EDIT_WORD:
			saveEditedWord(
					data.getLongExtra(EditWordActivity.EXTRA_WORD_ID, -1),
					data.getStringExtra(EditWordActivity.EXTRA_QUESTION),
					data.getStringExtra(EditWordActivity.EXTRA_ANSWER));
			break;
		default:
			throw new Error("Unknown activity requestCode: " + requestCode);

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * ------------------------------------------------------------ Create new
	 * word and update items of fragment: WordListFragment.
	 * 
	 * @param String
	 *            question
	 * @param String
	 *            answer
	 */
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

		if (id > -1) {
			getWordListFragment().updateList();
			showToastMessage(this, R.string.word_added);
			if (dualPane) {
				//edi (id);
			}
		} else {
			showToastMessage(this, R.string.word_not_added);
		}

	}

	/**
	 * ------------------------------------------------------------ Select
	 * current lecture name from database
	 * 
	 * @param long ID of given lecture
	 * @return name of current lecture
	 */
	public static String getLectureName(Context context, long lectureId) {
		WordDBAdapter wordDBAdapter = new WordDBAdapter(context);
		String lectureName = null;
		try {
			lectureName = wordDBAdapter.getLectureNameById(lectureId);
		} catch (Exception e) {
			Log.d("getLectureName", "ERROR: " + e.getMessage());
		} finally {
			wordDBAdapter.close();
		}
		return lectureName;
	}

	private void onAddNewWordClicked() {
		if (dualPane) {
			Bundle data = new Bundle();
			data.putString(AddWordActivity.EXTRA_LECTURE_NAME, lectureName);
			Fragment f = new AddWordFragment();
			f.setArguments(data);
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			//ft.replace(R.id.right_column, f);
			ft.addToBackStack(null);
			ft.commit();
		} else {
			Intent i = new Intent(this, AddWordActivity.class);
			i.putExtra(AddWordActivity.EXTRA_LECTURE_NAME, lectureName);
			startActivityForResult(i, REQUEST_ADD_WORD);
		}
	}

	@Override
	public void onEditWordClicked(long wordId) {
		if (dualPane) {
			Bundle data = new Bundle();
			data.putLong(EditWordActivity.EXTRA_WORD_ID, wordId);
			Fragment f = new EditWordFragment();
			f.setArguments(data);
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			// ft.replace(R.id.right_column, f);
			ft.addToBackStack(null);
			ft.commit();
		} else {
			Intent i = new Intent(this, EditWordActivity.class);
			i.putExtra(EditWordActivity.EXTRA_WORD_ID, wordId);
			startActivityForResult(i, REQUEST_EDIT_WORD);
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

		if (updated) {
			getWordListFragment().updateList();
			showToastMessage(this, R.string.saved_ok);
			if (dualPane) {
				//showWord(wordId);
			}
		} else {
			showToastMessage(this, R.string.saved_no);
		}

	}

	public long getLectureId() {
		return lectureId;
	}


	public WordListFragment getWordListFragment() {
		return ((WordListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.WordListFragment));
	}

	

	public static void showToastMessage(Context ctx, int resourceId) {
		Toast.makeText(ctx, resourceId, Toast.LENGTH_LONG).show();
	}

	/**
	 * Active all words in lecture and update list
	 * 
	 */
	public void activeAllWordInLecture() {
		WordDBAdapter wordDbAdapter = new WordDBAdapter(this);
		boolean deactivated = false;
		try {
			deactivated = wordDbAdapter.changeWordActivity(lectureId,
					WordDBAdapter.STATUS_ACTIVE);
		} catch (Exception e) {
			Log.d(TAG, "ERROR: " + e.getMessage());
		} finally {
			wordDbAdapter.close();
		}
		if (deactivated) {
			getWordListFragment().updateList();
			Toast.makeText(this, R.string.activated, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Deactive all words in lecture and update list
	 */
	public void deactiveAllWordInLecture() {
		WordDBAdapter wordDbAdapter = new WordDBAdapter(this);
		boolean deactivated = false;
		try {
			deactivated = wordDbAdapter.changeWordActivity(lectureId,
					WordDBAdapter.STATUS_DEACTIVE);
		} catch (Exception e) {
			Log.d(TAG, "ERROR: " + e.getMessage());
		} finally {
			wordDbAdapter.close();
		}
		if (deactivated) {
			getWordListFragment().updateList();
			Toast.makeText(this, R.string.words_deactived, Toast.LENGTH_SHORT)
					.show();
		}
	}

	/*	ACTION BAR MENU ---------------------	 */
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuAddWord:
				onAddNewWordClicked();
			return true;
			case R.id.menuActivateLecture:
				activeAllWordInLecture();
			return true;
			case R.id.menuDeactivateLecture:
				deactiveAllWordInLecture();
			return true;
			case R.id.menuImport:
				importIntoLecture();
			return true;
			}
		return super.onOptionsItemSelected(item);
	}
	
	private void importIntoLecture(){
		Intent i = new Intent(this,  ImportMenuActivity.class);
		i.putExtra(ImportMenuActivity.EXTRA_ID, lectureId);
		i.putExtra(ImportMenuActivity.EXTRA_CREATE_LECTURE, false);
		startActivity(i);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.v2_word_list_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	

	/* ENDOPTION MENU ---------------------------------------- */

	@Override
	public void onListItemClick(View v, long id) {
		 WordListFragment wordListFratment = (WordListFragment) getSupportFragmentManager()
                 .findFragmentById(R.id.WordListFragment);
		 Log.d(TAG, "on list item clicked...");
		 if (wordListFratment != null) {
		         if (wordListFratment.hasSelectedItems()
		                         && wordListFratment.getActionMode() == null) {
		                 ActionMode actionMode = startSupportActionMode(wordListFratment
		                                 .getActionModeCallback());
		                 actionMode.setTitle(wordListFratment.getWordAdapter()
		                                 .getCountOfSelected() + "");
		                 wordListFratment.setActionMode(actionMode);
		         } else if (!wordListFratment.hasSelectedItems()
		                         && wordListFratment.getActionMode() != null) {
		                 wordListFratment.getActionMode().finish();
		         } else if (wordListFratment.getActionMode() != null) {
		                 wordListFratment.getActionMode().setTitle(
		                                 wordListFratment.getWordAdapter().getCountOfSelected()
		                                                 + "");
		         }
		 }
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(LECTURE_ID_EXTRA, lectureId);
		outState.putString(LECTURE_NAME_EXTRA, lectureName);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		lectureId =  savedInstanceState.getLong(LECTURE_ID_EXTRA);
		lectureName = savedInstanceState.getString(LECTURE_NAME_EXTRA);
	}
}
