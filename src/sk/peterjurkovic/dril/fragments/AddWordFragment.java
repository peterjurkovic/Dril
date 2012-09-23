package sk.peterjurkovic.dril.fragments;

import sk.peterjurkovic.dril.AddWordActivity;
import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.listener.OnAddWordListener;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddWordFragment extends Fragment {
	 
	
	OnAddWordListener onAddWordListener; 
	
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.word_add_layout, null);

        Bundle data = getArguments();
        
        String wordLabel;
        if(data == null){
        	wordLabel = ((AddWordActivity)getActivity()).getWordLabel();
        }else{
        	wordLabel = getString(R.string.word_add_label) + " " 
        				+ data.getString(AddWordActivity.EXTRA_LECTURE_NAME);
        }
        
        Button submit = (Button)view.findViewById(R.id.submitAddQuestion);
        
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSubmitClicked();
            }
        });
        
        ((TextView) view.findViewById(R.id.wordAddLabel)).setText(wordLabel);

        
        return view;
    }
    
    
    
    
    public void onSubmitClicked(){
        View root = getView();
        String question = ((EditText)root.findViewById(R.id.addQestion)).getText().toString();
        String answer = ((EditText)root.findViewById(R.id.addAnswer)).getText().toString();
        if(question.length() == 0 || answer.length() == 0) return;
        onAddWordListener.saveNewWord(question, answer);
    }
    
}
