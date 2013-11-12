package sk.peterjurkovic.dril.v2.activities;

import android.R;
import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 *  
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Oct 24, 2013
 * @version 2.0
 */
public class ActionBarListActivity extends BaseActivity {

	
	private ListView mListView;
	
	protected ListAdapter mAdapter;
	
	private boolean mFinishedStart = false;
	
	private Handler mHandler = new Handler();
	
	private Runnable mRequestFocus = new Runnable() {
        public void run() {
        	mListView.focusableViewAvailable(mListView);
        }
    };
	
	protected ListView getListView() {
	    if (mListView == null) {
	        mListView = (ListView) findViewById(android.R.id.list);
	    }
	    return mListView;
	}

	protected void setListAdapter(ListAdapter adapter) {
	    getListView().setAdapter(adapter);
	}

	protected ListAdapter getListAdapter() {
	    ListAdapter adapter = getListView().getAdapter();
	    if (adapter instanceof HeaderViewListAdapter) {
	        return ((HeaderViewListAdapter)adapter).getWrappedAdapter();
	    } else {
	        return adapter;
	    }
	}
	
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(getClass().getName(), "ID: "  + id);
    }
	
	
	/**
     * Updates the screen state (current list and other views) when the
     * content changes.
     *
     * @see Activity#onContentChanged()
     */
	@Override
	public void onContentChanged() {
        super.onContentChanged();
        View emptyView = findViewById(R.id.empty);
        mListView = (ListView)findViewById(android.R.id.list);
        if (mListView == null) {
            throw new RuntimeException(
                    "Your content must have a ListView whose id attribute is " +
                    "'android.R.id.list'");
        }
        if (emptyView != null) {
            mListView.setEmptyView(emptyView);
        }
        mListView.setOnItemClickListener(mOnClickListener);
        if (mFinishedStart) {
            setListAdapter(mAdapter);
        }
        mHandler.post(mRequestFocus);
        mFinishedStart = true;
	    
	}
	
	
	private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id){
            onListItemClick((ListView)parent, v, position, id);
        }
    };
	
}
