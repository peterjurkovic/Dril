package sk.peterjurkovic.dril.fragments;


import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.SessionManager;
import sk.peterjurkovic.dril.dao.BookDao;
import sk.peterjurkovic.dril.dao.BookDaoImpl;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.listener.OnAddWordListener;
import sk.peterjurkovic.dril.model.Book;
import sk.peterjurkovic.dril.utils.StringUtils;
import sk.peterjurkovic.dril.v2.activities.AddWordActivity;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.Log;

public class AddWordFragment extends Fragment {
	 
	
	private OnAddWordListener onAddWordListener;
	
	private EditText questionElement ;
	
	private EditText answerElement;
	
		
	public static final String TAG = "AddNewWordFragment";
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	onAddWordListener = (OnAddWordListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onAddWordListener");
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.v2_word_add_layout, container);
        final Activity activity = getActivity();
        final Bundle data = getArguments();
        
        String wordLabel;
        if(data == null){
        	wordLabel = ((AddWordActivity)activity).getWordLabel();
        }else{
        	wordLabel = getString(R.string.word_add_label) + " " 
        				+ data.getString(AddWordActivity.EXTRA_LECTURE_NAME);
        }
        
        Button submit = (Button)view.findViewById(R.id.submitAddQuestion);
        
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
                onSubmitClicked();
            }
        });
        
        ((TextView) view.findViewById(R.id.wordAddLabel)).setText(wordLabel);
        
        questionElement = (EditText)view.findViewById(R.id.addQestion);
        answerElement = (EditText)view.findViewById(R.id.addAnswer);
        
   
        return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	 final Activity activity = getActivity();
    	 final long bookId = ((AddWordActivity)activity).getBookId();
         if(bookId > 0){
 	        final BookDao bookDao = new BookDaoImpl(getActivity());
 	        final Book book = bookDao.getById( bookId );
 	        if(book != null){
 	        	final String questionLang = activity.getString(book.getQuestionLang().getResource());
 	        	questionElement.setHint(activity.getString(R.string.questionIn) + " " + questionLang);	
 	        	final String answerLang = activity.getString(book.getAnswerLang().getResource());
 	        	answerElement.setHint(activity.getString(R.string.answerIn) + " " + answerLang);
 	        }else{
 	        	Log.w("Book was not found under ID: " + bookId);
 	        }
 	
         }
    }
    
    
    
    public void onSubmitClicked(){
        View root = getView();
        String question = ((EditText)root.findViewById(R.id.addQestion)).getText().toString();
        String answer = ((EditText)root.findViewById(R.id.addAnswer)).getText().toString();
        if(StringUtils.isBlank(question) || StringUtils.isBlank(answer)){
        	return;
        }
        final Context context = getActivity();
        SessionManager sessionManage = new SessionManager(context);
        if(sessionManage.isUserLoggedIn() && !sessionManage.isUserUnlimited()){
        	final WordDBAdapter db = new WordDBAdapter(context);
        	final long count = db.getCountOfStoredWords();
        	if(count + 1 > sessionManage.getWordLimit()){
        		Toast.makeText(context, context.getString(R.string.err_word_limit, sessionManage.getWordLimit()), Toast.LENGTH_LONG).show();
        		return;
        	}
        }
        onAddWordListener.saveNewWord(question, answer);
    }
    
}
