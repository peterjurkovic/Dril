package sk.peterjurkovic.dril.v2.activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.fragments.AddWordFragment;
import sk.peterjurkovic.dril.fragments.EditWordFragment;
import sk.peterjurkovic.dril.fragments.WordListFragment;
import sk.peterjurkovic.dril.listener.OnAddWordListener;
import sk.peterjurkovic.dril.listener.OnDeleteSelectedWordsListener;
import sk.peterjurkovic.dril.listener.OnEditWordClickedListener;
import sk.peterjurkovic.dril.listener.OnEditWordListener;
import sk.peterjurkovic.dril.listener.OnWordClickListener;
import sk.peterjurkovic.dril.sync.OnSuccessSyncListener;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;

public class WordActivity extends BaseActivity implements OnAddWordListener,
        OnEditWordClickedListener, OnEditWordListener, OnWordClickListener,
		OnDeleteSelectedWordsListener, OnSuccessSyncListener {

	public static final int REQUEST_ADD_WORD = 0;
	public static final int REQUEST_EDIT_WORD = 1;

	public static final String BOOK_ID_EXTRA = "book_id";
	public static final String LECTURE_ID_EXTRA = "fk_lecture_id";
	public static final String LECTURE_NAME_EXTRA = "fk_lecture_name";
	
	private boolean dualPane = false;
	public static final String TAG = "WordActivity";
	private long lectureId = -1;
	private long bookId = -1;
	private String lectureName;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.v2_word_list_layout);

		if(savedInstanceState != null){
			lectureId = savedInstanceState.getLong(LECTURE_ID_EXTRA);
			lectureName = savedInstanceState.getString(LECTURE_NAME_EXTRA);
			bookId = savedInstanceState.getLong(BOOK_ID_EXTRA);
		}else{
			lectureId = getIntent().getLongExtra(LECTURE_ID_EXTRA, -1);
			bookId = getIntent().getLongExtra(BOOK_ID_EXTRA, 0);
			if (lectureId == -1) {
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


	@Override
	public void saveNewWord(String question, String answer) {
		WordDBAdapter wordDBAdapter = new WordDBAdapter(this);

		long id = -1;
		try {
			id = wordDBAdapter.insertWord(lectureId, question, answer);
		} catch (Exception e) {
			GoogleAnalyticsUtils.log(e);
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
			showToastMessage(this, R.string.error_ocurred);
		}

	}
	

	public static String getLectureName(Context context, long lectureId) {
		WordDBAdapter wordDBAdapter = new WordDBAdapter(context);
		String lectureName = null;
		try {
			lectureName = wordDBAdapter.getLectureNameById(lectureId);
		} catch (Exception e) {
			GoogleAnalyticsUtils.log(e);
		} finally {
			wordDBAdapter.close();
		}
		return lectureName;
	}

	private void onAddNewWordClicked() {
		if (dualPane) {
			Bundle data = new Bundle();
			data.putString(AddWordActivity.EXTRA_LECTURE_NAME, lectureName);
			data.putLong(AddWordActivity.EXTRA_BOOK_ID, bookId);
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
			i.putExtra(AddWordActivity.EXTRA_BOOK_ID, bookId);
			startActivityForResult(i, REQUEST_ADD_WORD);
		}
	}

	@Override
	public void onEditWordClicked(long wordId) {
		if (dualPane) {
			Bundle data = new Bundle();
			data.putLong(EditWordActivity.EXTRA_WORD_ID, wordId);
			Fragment f = new EditWordFragment();
			data.putLong(BOOK_ID_EXTRA, bookId);
			f.setArguments(data);
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			// ft.replace(R.id.right_column, f);
			ft.addToBackStack(null);
			ft.commit();
		} else {
			Intent i = new Intent(this, EditWordActivity.class);
			i.putExtra(EditWordActivity.EXTRA_WORD_ID, wordId);
			i.putExtra(BOOK_ID_EXTRA, bookId);
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
			GoogleAnalyticsUtils.log(e);
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
	
	public long getBookId(){
		return bookId;
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
			GoogleAnalyticsUtils.log(e);
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
			GoogleAnalyticsUtils.log(e);
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
	
	



	@Override
	public void onListItemClick(View v, long id) {
		 WordListFragment wordListFratment = (WordListFragment) getSupportFragmentManager()
                 .findFragmentById(R.id.WordListFragment);
		
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
	public void showConfirationDialog(final ActionMode mode){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
			.setTitle(R.string.confirm_delete)
			.setMessage(getString(R.string.confirm_delete_selected_words))
			.setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						 WordListFragment wordListFratment = (WordListFragment) getSupportFragmentManager()
				                 .findFragmentById(R.id.WordListFragment);
						 wordListFratment.deleteSelectedItems();
						 mode.finish();
						dialog.cancel();
					}
			})
			.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
                   public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
                   }
			});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(LECTURE_ID_EXTRA, lectureId);
		outState.putString(LECTURE_NAME_EXTRA, lectureName);
		outState.putLong(BOOK_ID_EXTRA, bookId);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		lectureId =  savedInstanceState.getLong(LECTURE_ID_EXTRA);
		lectureName = savedInstanceState.getString(LECTURE_NAME_EXTRA);
		bookId = savedInstanceState.getLong(BOOK_ID_EXTRA);
	}

	@Override
	public void onSuccessSync() {
		getWordListFragment().updateList();
	}
}
