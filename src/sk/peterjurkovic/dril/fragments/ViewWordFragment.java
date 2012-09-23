package sk.peterjurkovic.dril.fragments;

import sk.peterjurkovic.dril.EditWordActivity;
import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.ViewWordActivity;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.listener.OnChangeWordStatusListener;
import sk.peterjurkovic.dril.listener.OnDeleteWordListener;
import sk.peterjurkovic.dril.listener.OnEditWordClickedListener;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ViewWordFragment extends Fragment {

	public static final String TAG = "ViewWordFragment";
	
	private long wordId;
	
	private int active;
	
	public static int STATUS_ACTIVE = 1;
	
	public static int STATUS_DEACTIVE = 0;
	
	Button viewActive;
	Button viewDeactive; 
	
	OnEditWordClickedListener onEditWordClickedListener;
	
	OnDeleteWordListener onDeleteWordListener;
	
	OnChangeWordStatusListener onChangeWordStatusListener;
	
	 @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        try {
	        	onEditWordClickedListener = (OnEditWordClickedListener) activity;
	        	onDeleteWordListener = (OnDeleteWordListener) activity;
	        	onChangeWordStatusListener = (OnChangeWordStatusListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString()
	                    + " must implement OnEditWordClickedListener, " +
	            		"OnDeleteWordListener, OnChangeWordStatusListener");
	        }
	    }
	 
	 
	 
	  @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        View view = inflater.inflate(R.layout.word_view_layout, null);
	        
	        Bundle data = getArguments();
	        
	        if(data == null){
	        	wordId = ((ViewWordActivity)getActivity()).getWordId();
	        }else{
	        	wordId = data.getLong(EditWordActivity.EXTRA_WORD_ID, -1);
	        }
	        
	        Button viewDelete = (Button)view.findViewById(R.id.viewDelete);
	        Button viewEdit = (Button)view.findViewById(R.id.viewEdit);
	        viewActive = (Button)view.findViewById(R.id.viewActive);
	        viewDeactive = (Button)view.findViewById(R.id.viewDeactive);
	        
	        viewDelete.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	onDeleteWordListener.onDeleteClicked(wordId);
	            }
	        });
	        
	        viewEdit.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	onEditWordClickedListener.onEditWordClicked(wordId);
	            }
	        });
	        
	        viewActive.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	onChangeWordStatusListener.activeWord(wordId);
	            	setActivationButtonVisibility( STATUS_ACTIVE );
	            }
	        });
	        
	        viewDeactive.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	onChangeWordStatusListener.deactiveWord(wordId);
	            	setActivationButtonVisibility( STATUS_DEACTIVE );
	            }
	        });
	        
	        loadWordData(view);
	        setActivationButtonVisibility(active);
	        return view;
	    }
	  
	  
	  /**
	   * Simulates behavior of toggle button. 
	   * 
	   * If status is active set visibility of Activation button to GONE 
	   * and Deactivatoion button set VISIBLE.
	   * 
	   * If status is deactive set visibility of Activation button VISIBLE
	   * and Deactivaton button is set to GONE.
	   * 
	   * @param int Activation status.
	   */
	  private void setActivationButtonVisibility(int active) {
		  if(active == 0){
			  viewDeactive.setVisibility(View.GONE);
			  viewActive.setVisibility(View.VISIBLE);
		  }else{
			  viewDeactive.setVisibility(View.VISIBLE);
			  viewActive.setVisibility(View.GONE);
		  }
	  }

	  
	/**
	 * Update update word activity by given ID
	 *   
	 * @param long ID of given word witch should be updated.
	 * @param int update to value.
	 */
	public void setWordStatus(long wordId, int newStatusVal){
		Context ctx = getActivity();
		WordDBAdapter wordDbAdapter = new WordDBAdapter(ctx );
		boolean updated = false;
		try{
			updated = wordDbAdapter.updateWordActivity(wordId, newStatusVal);
	    } catch (Exception e) {
			Log.d("EditBookFragment", "ERROR: " + e.getMessage());
		} finally {
			wordDbAdapter.close();
		}
		
		int resourceId;
		if(updated){
			resourceId = (newStatusVal == STATUS_ACTIVE ? 
						R.string.word_active : R.string.word_deactive );
		}else{
			resourceId = (newStatusVal == STATUS_ACTIVE ? 
						R.string.word_not_active : R.string.word_not_deactive );
		}
		Toast.makeText(ctx, resourceId, Toast.LENGTH_LONG).show();
	}

	
	
	
	/**
	 * Return item data,
	 * 
	 * Client method have to close cursor with calling method cursor.close()
	 * 
	 * @return Cursor witch contain only one row.
	 */
	
	public void loadWordData(View view){
		// Log.d(TAG, "loadWordData() wordId: " + wordId);
		Context ctx = getActivity();
		WordDBAdapter wordDbAdapter = new WordDBAdapter(ctx );
		Cursor cursor = null;
		try{
			cursor = wordDbAdapter.getWord( wordId );
			putWordDataIntoViews(view , cursor);
	    } catch (Exception e) {
			Log.d("EditBookFragment", "ERROR: " + e.getMessage());
		} finally {
			cursor.close();
			wordDbAdapter.close();
		}
	}
		
	
	
	/**
	 * Put into the views question and answer of given word
	 * 
	 * 
	 * @param View with contains elements with IDs: R.id.viewQuestion, R.id.viewAnswer 
	 * @param Cursor cursor witch contain only 1 row.
	 */
	public void putWordDataIntoViews(View view , Cursor cursor){
		
		if(cursor.getCount() == 0){ 
			Toast.makeText( getActivity(), R.string.error, Toast.LENGTH_LONG).show();
			return;
		}
		cursor.moveToFirst();
		
		int question = cursor.getColumnIndex( WordDBAdapter.QUESTION );
		int answer = cursor.getColumnIndex( WordDBAdapter.ANSWER );
		int active = cursor.getColumnIndex( WordDBAdapter.ACTIVE );
		
		((TextView)view.findViewById(R.id.viewQuestion)).setText(cursor.getString(question));	
		
		((TextView)view.findViewById(R.id.viewAnswer)).setText(cursor.getString(answer));	
		
		this.active = cursor.getInt(active);
	}
}
