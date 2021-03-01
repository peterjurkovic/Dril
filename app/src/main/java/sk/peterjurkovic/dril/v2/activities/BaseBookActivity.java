package sk.peterjurkovic.dril.v2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.dao.BookDao;
import sk.peterjurkovic.dril.dao.BookDaoImpl;
import sk.peterjurkovic.dril.db.BookDBAdapter;
import sk.peterjurkovic.dril.dto.State;
import sk.peterjurkovic.dril.model.Book;
import sk.peterjurkovic.dril.model.Language;
import sk.peterjurkovic.dril.model.Level;
import sk.peterjurkovic.dril.model.SpinnerState;

public abstract class BaseBookActivity extends BaseActivity{
	
	protected BookDao bookDao;
	protected Spinner questionSpinner;
	protected Spinner answerSpinner;
	protected Spinner levelSpinner;
	protected CheckBox shareCheckbox;
	protected EditText bookNameInput;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 bookDao = new BookDaoImpl(new BookDBAdapter( context ));
	}
	 
	
	protected void onCancelClicked() {
		finish();
	}
	
	protected void onSuccess() {
		Intent result = new Intent();
		setResult(RESULT_OK, result);
		finish();
	}
	
	protected abstract void onSubmitClicked();
	
	protected Book prepareBoook(Book book) {
		String bookName = bookNameInput.getText().toString();
	    if(StringUtils.isBlank(bookName) || bookName.length() < 8) {
	    	showMessage( R.string.err_book_name );
	    	return null;
	    };
    	book.setName(bookName);
    	Language langQuestion = Language.getById(((State)questionSpinner.getSelectedItem()).getId());
    	Language langAnswer = Language.getById(((State)answerSpinner.getSelectedItem()).getId());
    	if(langQuestion == langAnswer){
    		Toast.makeText(this, R.string.error_same_languages, Toast.LENGTH_LONG).show();
    	}
    	book.setQuestionLang(langQuestion);
    	book.setAnswerLang(langAnswer);
    	book.setLevel(Level.getById(((State) levelSpinner.getSelectedItem()).getId()));
    	book.setShared(shareCheckbox.isChecked());
    	return book;
	}
	

	protected void prepareSpinners(){
		ArrayAdapter<State> adapter = languageAdapter();
		questionSpinner.setAdapter(adapter);
		answerSpinner.setAdapter(adapter);
		questionSpinner.setSelection(session.getLocaleId() - 1);
		answerSpinner.setSelection( session.getTargetLocaleId() - 1);
		levelSpinner.setAdapter(levelAdapter());
	}

	protected ArrayAdapter<State> languageAdapter(){
		return getAddpter( Language.getAllStates() );
	}

	
	protected ArrayAdapter<State> levelAdapter(){
		return getAddpter( Level.getAllStates() );
	}
	
	protected ArrayAdapter<State> getAddpter(List<SpinnerState> itemList){
		List<State> list = new ArrayList<State>(itemList.size());
		for(SpinnerState lang : itemList){
			list.add( new State(lang.getId(), context.getResources().getString(lang.getResource())));
		}
		ArrayAdapter<State> adapter =  new ArrayAdapter<State>(this,  R.layout.v2_spinner,  list);
		adapter.setDropDownViewResource(R.layout.v2_spinner_dropdown);	
		return adapter;
	}
	
	protected void showMessage(int resource) {
		Toast.makeText(context, resource, Toast.LENGTH_LONG).show();
	}

}
