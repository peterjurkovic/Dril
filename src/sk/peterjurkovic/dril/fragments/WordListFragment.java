package sk.peterjurkovic.dril.fragments;

import java.util.Set;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.WordActivity;
import sk.peterjurkovic.dril.adapter.WordAdapter;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.listener.OnEditWordClickedListener;
import sk.peterjurkovic.dril.listener.OnShowWordListener;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class WordListFragment extends ListFragment{
	
	
	public static final String TAG = "WordListFragment";
	
	public static final int MENU_VIEW_ID = Menu.FIRST+1;
	public static final int MENU_EDIT_ID = Menu.FIRST+2;
	public static final int MENU_DELETE_ID = Menu.FIRST+3;
	
	
	OnEditWordClickedListener onEditWordClickedListener; 
	
	OnShowWordListener onShowWordListener; 

	WordAdapter wordAdapter;
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.word_list, container, false);
        
        Button listDelete = (Button)view.findViewById(R.id.ListDelete);
        Button listActive = (Button)view.findViewById(R.id.ListActive);
        Button listDeactive = (Button)view.findViewById(R.id.ListDeactive);
        
        listDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 deleteSelectedItems();
			}			
		});
        
        listActive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateSelectedItemStatus( ViewWordFragment.STATUS_ACTIVE );
			}
        });
        
        listDeactive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateSelectedItemStatus( ViewWordFragment.STATUS_DEACTIVE );
			}
        });
        
        return view;
    }
	
	
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	onEditWordClickedListener = (OnEditWordClickedListener) activity;
        	onShowWordListener = (OnShowWordListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEditWordListener and onShowWordListener");
        }
        
    }
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu( getListView() );
		 updateList();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		onShowWordListener.showWord(id);
	}
	
	
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.wordAction);
        menu.add(Menu.NONE, MENU_VIEW_ID, Menu.NONE, R.string.view);
        menu.add(Menu.NONE, MENU_EDIT_ID, Menu.NONE, R.string.editWord);
        menu.add(Menu.NONE, MENU_DELETE_ID, Menu.NONE, R.string.delete);
    }
	
	
	
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
	        case MENU_VIEW_ID:
	    		showWord(info.id);
	        	return true;
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
	    	cursor = wordDbAdapter.getWordByLctureId(((WordActivity)ctx).getLectureId()); 
	    	wordAdapter = new WordAdapter(ctx, cursor, 0);
	 	    setListAdapter(wordAdapter);
	    } catch (Exception e) {
			Log.d(TAG, "ERROR: " + e.getMessage());
		} finally {
			wordDbAdapter.close();
		}
	   
	}
 
	private void closeAdapterCursor(){
		try {
			if(wordAdapter != null){
				if(!wordAdapter.getCursor().isClosed())
					wordAdapter.getCursor().close();
			}
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
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
 			Log.d(TAG, "ERROR: " + e.getMessage());
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

    
    public void showWord(long wordId){
    	onShowWordListener.showWord(wordId);
    }
	
    
    
    private void deleteSelectedItems(){
    	Context ctx = getActivity();
    	Set<Long> selectedWords = wordAdapter.getCheckedItems();
    	if(isCheckBoxChecked(ctx, selectedWords)){
	        Boolean deleted = false;
	        WordDBAdapter wordDbAdapter = new WordDBAdapter(ctx);
	 	    try{
	 	    	deleted = wordDbAdapter.deleteSelected( selectedWords );
	 	    } catch (Exception e) {
	 			Log.d(TAG, "ERROR: " + e.getMessage());
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
	 			Log.d(TAG, "ERROR: " + e.getMessage());
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
    
    
}
