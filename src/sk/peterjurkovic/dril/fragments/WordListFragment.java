package sk.peterjurkovic.dril.fragments;

import java.util.Set;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.adapter.WordAdapter;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.listener.OnDeleteSelectedWordsListener;
import sk.peterjurkovic.dril.listener.OnEditWordClickedListener;
import sk.peterjurkovic.dril.listener.OnWordClickListener;
import sk.peterjurkovic.dril.v2.activities.WordActivity;
import sk.peterjurkovic.dril.v2.constants.Constants;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

import com.google.analytics.tracking.android.Log;

public class WordListFragment extends ListFragment 	implements OnClickListener{
	
	
	public static final String TAG = "WordListFragment";
	
	public static final int MENU_EDIT_ID = Menu.FIRST+1;
	public static final int MENU_DELETE_ID = Menu.FIRST+2;
	
	
	protected OnEditWordClickedListener onEditWordClickedListener; 
	
	protected OnWordClickListener onWordClickListener;

	protected WordAdapter wordAdapter;
	
	private ActionMode actionMode;
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View listView  = inflater.inflate(R.layout.v2_word_list, container, false);
        return listView;
    }
	
	
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	if(onEditWordClickedListener == null){
        		onEditWordClickedListener = (OnEditWordClickedListener) activity;
        	}
        	if(onWordClickListener == null){
        		onWordClickListener = (OnWordClickListener) activity;
        	}
        } catch (ClassCastException e) {
        	throw new ClassCastException(activity.toString()
                    + " must implement OnEditWordListener, OnWordClickListener and onShowWordListener");
        }
        
    }
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu( getListView() );
		updateList();
	}
	

	
	
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.wordAction);
        menu.add(Menu.NONE, MENU_EDIT_ID, Menu.NONE, R.string.edit);
        menu.add(Menu.NONE, MENU_DELETE_ID, Menu.NONE, R.string.delete);
    }
	
	
	
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
	        case MENU_EDIT_ID:
	        	onEditBookClicked(info.id);
	            return true;
	        case MENU_DELETE_ID:
	        	deleteWord(info.id);
	            return true;
	        default:
	            return super.onContextItemSelected(item);
        }
    }
    
 
    
    
    private void onEditBookClicked(long wordId) {
    	onEditWordClickedListener.onEditWordClicked(wordId);
	}

    
	public void updateList() {
		closeAdapterCursor();
		Context ctx = getActivity();		
		Cursor cursor = null;
	    WordDBAdapter wordDbAdapter = new WordDBAdapter(ctx);
	    try{
	    	if(isWordListActivity()){
	    		cursor = wordDbAdapter.getWordByLctureId(((WordActivity)ctx).getLectureId());
	    	}else{
	    		cursor = wordDbAdapter.getProblematicsWords();
	    	}
	    	wordAdapter = new WordAdapter(ctx, cursor, 0);
	    	wordAdapter.setOnClickListener(this);
	 	    setListAdapter(wordAdapter);
	    } catch (Exception e) {
	    	 Log.e(e);
		} finally {
			wordDbAdapter.close();
		}
	   
	}
	
	private boolean isWordListActivity(){
		return (getActivity() instanceof WordActivity);
	}
	
	public void closeAdapterCursor(){
		try {
			if(wordAdapter != null){
				if(!wordAdapter.getCursor().isClosed())
					wordAdapter.getCursor().close();
			}
		} catch (Exception e) {
			 Log.e(e);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onStop();
		closeAdapterCursor();
	}
    
	
    public void deleteWord(long wordId){
    	 Context ctx = getActivity();
         Boolean deleted = false;
         WordDBAdapter wordDbAdapter = new WordDBAdapter(ctx);
 	    try{
 	    	deleted = wordDbAdapter.deleteWord(wordId);
 	    } catch (Exception e) {
 	    	 Log.e(e);
 		} finally {
 			wordDbAdapter.close();
 		}
         
         if(deleted){
             Toast.makeText(ctx, R.string.deleted, Toast.LENGTH_SHORT).show();
             updateList();
         } else{
             Toast.makeText(ctx, R.string.lecture_not_deleted, Toast.LENGTH_SHORT).show();
         }
    }

    public void deleteSelectedItems(){
    	Context ctx = getActivity();
    	Set<Long> selectedWords = wordAdapter.getCheckedItems();
    	if(isCheckBoxChecked(ctx, selectedWords)){
	        Boolean deleted = false;
	        WordDBAdapter wordDbAdapter = new WordDBAdapter(ctx);
	 	    try{
	 	    	deleted = wordDbAdapter.deleteSelected( selectedWords );
	 	    } catch (Exception e) {
	 	    	 Log.e(e);
	 		} finally {
	 			wordDbAdapter.close();
	 		}
	 	    
	 	    if(deleted){
	 	    	Toast.makeText(ctx, R.string.deleted, Toast.LENGTH_SHORT).show();
	 	    	updateList();
	 	    }else{
	 	    	Toast.makeText(ctx, R.string.error, Toast.LENGTH_SHORT).show();
	 	    }
    	}
    }
    

    private void updateSelectedItemStatus(int newStatusval){
    	Context ctx = getActivity();
    	Set<Long> selectedWords = wordAdapter.getCheckedItems();
    	if(isCheckBoxChecked(ctx, selectedWords)){
	        Boolean updated = false;
	        WordDBAdapter wordDbAdapter = new WordDBAdapter(ctx);
	 	    try{
	 	    	updated = wordDbAdapter.updateActivitySelected(selectedWords, newStatusval );
	 	    } catch (Exception e) {
	 	    	 Log.e(e);
	 		} finally {
	 			wordDbAdapter.close();
	 		}
	 	    
	 	    if(updated){
	 	    	Toast.makeText(ctx, R.string.saved_ok, Toast.LENGTH_SHORT).show();
	 	    	updateList();
	 	    }else{
	 	    	Toast.makeText(ctx, R.string.error, Toast.LENGTH_SHORT).show();
	 	    }
    	}
    }
    
    private boolean isCheckBoxChecked(Context ctx, Set<Long> selectedWords){
    	if(selectedWords.size() == 0){
    		Toast.makeText(ctx, R.string.word_not_selected, Toast.LENGTH_LONG).show();
    		return false;
    	}
    	return true;
    }
    
    
    protected class ActionModeCallback implements ActionMode.Callback {
      	
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
            switch (item.getItemId()) {
	            case R.id.menu_delete:
	            	try{
	            		((OnDeleteSelectedWordsListener)getActivity()).showConfirationDialog(mode);
	            	}catch(ClassCastException e){
	            		 Log.e(e);
	            	}
	                return true;
	    		case R.id.menu_active:
	    			updateSelectedItemStatus(Constants.STATUS_ACTIVE);
	    			mode.finish(); 
	    			return true;
	    		case R.id.menu_deactive:
	    			updateSelectedItemStatus(Constants.STATUS_DEACTIVE);
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



	@Override
	public void onClick(View view) {
		long wordId = wordAdapter.updateItemState(view);
		onWordClickListener.onListItemClick(view, wordId);
	}



	public ActionMode getActionMode() {
		return actionMode;
	}



	public void setActionMode(ActionMode actionMode) {
		this.actionMode = actionMode;
	}



	public WordAdapter getWordAdapter() {
		return wordAdapter;
	}



	public void setWordAdapter(WordAdapter wordAdapter) {
		this.wordAdapter = wordAdapter;
	}
	
	
	public boolean hasSelectedItems(){
		if(wordAdapter != null && wordAdapter.hasSelectedItems()){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_deactive:
			updateSelectedItemStatus(Constants.STATUS_DEACTIVE);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


}
