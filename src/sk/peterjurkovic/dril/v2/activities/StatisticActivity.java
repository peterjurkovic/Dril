package sk.peterjurkovic.dril.v2.activities;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.fragments.StatisticsListFragment;
import sk.peterjurkovic.dril.listener.OnChangedProgressListenter;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * @author Peter Jurkovič
 * @date Nov 18, 2013
 *
 */
public class StatisticActivity extends BaseActivity implements OnChangedProgressListenter{
	
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	private ProgressBar progressBar;
	private TextView progressBarLabel;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_statistics_list_layout);
        
        progressBar = (ProgressBar)findViewById(R.id.statisticsProgress);
        progressBarLabel = (TextView)findViewById(R.id.statisticsProgressLabel);
        
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
 
        Tab tab = actionBar.newTab()
                .setText("Test")
                .setTabListener(new TabListener<StatisticsListFragment>(
                        this, "artist", StatisticsListFragment.class));
        	actionBar.addTab(tab);


    }
	
	@Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
    }
	 
	    
	
	public static class TabListener<T extends Fragment> implements ActionBar.TabListener{
		
		private Fragment mFragment;
	    private final Activity mActivity;
	    private final String mTag;
	    private final Class<T> mClass;

	    public TabListener(Activity activity, String tag, Class<T> clz) {
	        mActivity = activity;
	        mTag = tag;
	        mClass = clz;
	    }
	  
	    /* The following are each of the ActionBar.TabListener callbacks */
	    public void onTabSelected(Tab tab, FragmentTransaction ft) {
	        // Check if the fragment is already initialized
	        if (mFragment == null) {
	            // If not, instantiate and add it to the activity
	            mFragment = Fragment.instantiate(mActivity, mClass.getName());
	            ft.add(android.R.id.content, mFragment, mTag);
	        } else {
	            // If it exists, simply attach it in order to show it
	            ft.attach(mFragment);
	        }
	    }
	    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	        if (mFragment != null) {
	            // Detach the fragment, because another one is being attached
	            ft.detach(mFragment);
	        }
	    }

	    public void onTabReselected(Tab tab, FragmentTransaction ft) {
	        // User selected the already selected tab. Usually do nothing.
	    }
	    

	}



	@Override
	public void showLoader() {
		progressBar.setVisibility(View.VISIBLE);
		progressBarLabel.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideLoader() {
		progressBar.setVisibility(View.GONE);
		progressBarLabel.setVisibility(View.GONE);
	}
}
