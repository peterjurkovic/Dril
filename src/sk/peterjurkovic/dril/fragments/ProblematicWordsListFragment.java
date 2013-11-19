package sk.peterjurkovic.dril.fragments;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.adapter.WordAdapter;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.listener.OnChangedProgressListenter;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ProblematicWordsListFragment extends ListFragment {
	
	private Context mContext;
	private OnChangedProgressListenter onChangedProgressListenter;
	private WordAdapter wordAdapter;
	private WordDBAdapter wordDbAdapter;
	private ActionMode actionMode;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
		try{
			onChangedProgressListenter = (OnChangedProgressListenter) activity;
		}catch(ClassCastException e){
			 throw new ClassCastException(activity.toString()
	                    + " must implement OnChangedProgressListenter");
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.v2_problematic_word_fragment, container, false);
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i("sa", "onActivityCreated");
		registerForContextMenu( getListView() );
		wordDbAdapter = new WordDBAdapter(mContext);
		updateList();
	}
	
		
	public void updateList(){
		 Cursor cursor = wordDbAdapter.getProblematicsWords();
		if(cursor != null && cursor.getCount() > 0){
			wordAdapter = new WordAdapter(mContext, cursor, 0);
			setListAdapter(wordAdapter);
			getListView().setVisibility(View.VISIBLE);
			wordAdapter.notifyDataSetChanged();
		}

	}

	public void deleteWord(long wordId){
   	 Context ctx = getActivity();
        Boolean deleted = false;
        if(deleted){
            Toast.makeText(ctx, R.string.deleted, Toast.LENGTH_SHORT).show();
            updateList();
        } else{
            Toast.makeText(ctx, R.string.lecture_not_deleted, Toast.LENGTH_SHORT).show();
        }
   }
	
	 private class ActionModeCallback implements ActionMode.Callback {
	      	
	        @Override
	        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	            mode.getMenuInflater().inflate(R.menu.v2_context_menu, menu);
	            return true;
	        }
	 
	        @Override
	        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	            return false;
	        }
	 
	        @Override
	        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	        	Log.d("test", "id" + item.getTitle());
	            switch (item.getItemId()) {
		            case R.id.menu_delete:
		            	//deleteSelectedItems();
		                mode.finish(); 
		                return true;
		    		case R.id.menu_active:
		    			//updateSelectedItemStatus(Constants.STATUS_ACTIVE);
		    			mode.finish(); 
		    			return true;
		    		case R.id.menu_deactive:
		    			//updateSelectedItemStatus(Constants.STATUS_DEACTIVE);
		    			mode.finish(); 
		    			return true;
		    		default:
		    			// return super.o(item);
		    		}
	            return false;
	        }
	        
	        @Override
	        public void onDestroyActionMode(ActionMode mode) {
	        	actionMode = null;
	        }
	    }
	    
	    public ActionModeCallback getActionModeCallback(){
	    	return new ActionModeCallback();
	    }
	    
	    
	    public boolean hasSelectedItems(){
			if(wordAdapter != null && wordAdapter.hasSelectedItems()){
				return true;
			}
			return false;
		}
}
