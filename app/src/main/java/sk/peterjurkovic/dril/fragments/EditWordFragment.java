package sk.peterjurkovic.dril.fragments;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.listener.OnEditWordListener;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import sk.peterjurkovic.dril.utils.StringUtils;
import sk.peterjurkovic.dril.v2.activities.EditWordActivity;

public class EditWordFragment extends Fragment {
	 
	//private OnEditWordClickedListener onEditWordClickedListener;
	
	private OnEditWordListener editWordListener;
	
	long wordId;
	
	private EditText questionElement = null;
	
	private EditText answerElement = null;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	editWordListener = (OnEditWordListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onAddWordListener and OnEditWordClickedListener");
        }
    }
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.v2_word_edit_layout, container);        
        
        Button submit = (Button)view.findViewById(R.id.submitEditQuestion);
        
        Bundle data = getArguments();
        
        if(data == null){
        	wordId = ((EditWordActivity)getActivity()).getWordId();
        }else{
        	wordId = data.getLong(EditWordActivity.EXTRA_WORD_ID, -1);
        }
        
        
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
                onSubmitClicked();
            }
        });
        
        loadWordData(view);
        return view;
    }
	
	
	
	public void onSubmitClicked(){
        
        String question = questionElement.getText().toString();
        String answer =  answerElement.getText().toString();
        
        if(StringUtils.isBlank(question) || StringUtils.isBlank(answer)){
        	return;
        }else{
        	editWordListener.saveEditedWord(wordId, question, answer);
        }
    }
	
	
	
	public void loadWordData(View view){
		WordDBAdapter wordDbAdapter = new WordDBAdapter( getActivity() );
		Cursor cursor = null;
		try{
			cursor = wordDbAdapter.getWord( wordId );
			putWordDataIntoViews(view , cursor);
	    } catch (Exception e) {
			GoogleAnalyticsUtils.logException(e);
			Log.e(getClass().getSimpleName(), e.getMessage());
		} finally {
			cursor.close();
			wordDbAdapter.close();
		}
	}
	
	
	public void putWordDataIntoViews(View view , Cursor cursor){
		if(cursor.getCount() == 0){ 
			Toast.makeText(getActivity(), R.string.error_no_data, Toast.LENGTH_LONG).show();
			return;
		}
		
		cursor.moveToFirst();
		
		int question = cursor.getColumnIndex( WordDBAdapter.QUESTION );
		int answer = cursor.getColumnIndex( WordDBAdapter.ANSWER );
		
		questionElement = (EditText)view.findViewById(R.id.editQestion);
		questionElement.setText(cursor.getString(question));	
		
		answerElement = (EditText)view.findViewById(R.id.editAnswer);
		answerElement.setText(cursor.getString(answer));	
	}

	
	

}
