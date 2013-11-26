package sk.peterjurkovic.dril.v2.activities;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.fragments.GeneralStatisticsFragment;
import sk.peterjurkovic.dril.fragments.ProblematicWordsListFragment;
import sk.peterjurkovic.dril.fragments.StatisticsListFragment;
import sk.peterjurkovic.dril.fragments.WordListFragment;
import sk.peterjurkovic.dril.listener.OnDeleteSelectedWordsListener;
import sk.peterjurkovic.dril.listener.OnEditWordClickedListener;
import sk.peterjurkovic.dril.listener.OnWordClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.view.ActionMode;
import android.view.View;

/**
 * 
 * @author Peter Jurkovič
 * @date Nov 18, 2013
 *
 */
public class StatisticActivity extends BaseActivity implements
		OnWordClickListener,
		OnEditWordClickedListener,
		OnDeleteSelectedWordsListener{
	
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static final String TAG_KEY_STATISTICS = "sessionStats";
	private static final String TAG_KEY_PROBLEMATIC_WORD = "problematicsWords";
	private static final String TAG_KEY_GENERAL_STATS = "generalStats";
	

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_statistics_list_layout);
        
       
        
        final ActionBar actionBar = getSupportActionBar();
        
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        
        Tab tab = actionBar.newTab()
                .setText(R.string.stats_sessions)
                .setTabListener(new TabListener<StatisticsListFragment>(this, TAG_KEY_STATISTICS, StatisticsListFragment.class));
       
        actionBar.addTab(tab);
        
        Tab tab2 = actionBar.newTab()
                .setText(R.string.stats_problematics_words)
                .setTabListener(new TabListener<ProblematicWordsListFragment>(this, TAG_KEY_PROBLEMATIC_WORD, ProblematicWordsListFragment.class));
      
        actionBar.addTab(tab2);
        
        Tab tab3 = actionBar.newTab()
                .setText(R.string.stats_general)
                .setTabListener(new TabListener<GeneralStatisticsFragment>(this, TAG_KEY_GENERAL_STATS, GeneralStatisticsFragment.class));
      
        actionBar.addTab(tab3);
        if (savedInstanceState != null) {
        	actionBar.setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM, 0));
        }
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
		
		private final FragmentActivity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private final Bundle mArgs;
        private Fragment mFragment;

        public TabListener(FragmentActivity activity, String tag, Class<T> clz) {
            this(activity, tag, clz, null);
        }

        public TabListener(FragmentActivity activity, String tag, Class<T> clz, Bundle args) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mArgs = args;

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
         
            mFragment = mActivity.getSupportFragmentManager().findFragmentByTag(mTag);
            if (mFragment != null && !mFragment.isDetached()) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.detach(mFragment);
                ft.commit();
            }
        }

        @Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
            if (mFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                ft.attach(mFragment);
            }
            
        }

        @Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                ft.detach(mFragment);
            }
        }

        @Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) { }
        
       
	}



	

	@Override
	public void onListItemClick(View v, long id) {
		ProblematicWordsListFragment wordListFratment = (ProblematicWordsListFragment) getSupportFragmentManager()
				.findFragmentByTag(TAG_KEY_PROBLEMATIC_WORD);
		if (wordListFratment != null) {
			if (wordListFratment.hasSelectedItems()
					&& wordListFratment.getActionMode() == null) {
				ActionMode actionMode = startSupportActionMode(wordListFratment
						.getActionModeCallback());
				actionMode.setTitle(wordListFratment.getWordAdapter()
						.getCountOfSelected() + "");
				wordListFratment.setActionMode(actionMode);
			} else if (!wordListFratment.hasSelectedItems()
					&& wordListFratment.getActionMode() != null) {
				wordListFratment.getActionMode().finish();
			} else if (wordListFratment.getActionMode() != null) {
				wordListFratment.getActionMode().setTitle(
						wordListFratment.getWordAdapter().getCountOfSelected()
								+ "");
			}
		}
		
	}

	@Override
	public void onEditWordClicked(long wordId) {
		Intent i = new Intent(this, EditWordActivity.class);
		i.putExtra(EditWordActivity.EXTRA_WORD_ID, wordId);
		startActivityForResult(i, WordActivity.REQUEST_EDIT_WORD);
	}

	@Override
	public void showConfirationDialog(final ActionMode mode){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
			.setTitle(R.string.confirm_delete)
			.setMessage(getString(R.string.confirm_delete_selected_words))
			.setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						ProblematicWordsListFragment fratment = (ProblematicWordsListFragment) getSupportFragmentManager()
				                 .findFragmentByTag(TAG_KEY_PROBLEMATIC_WORD);
						 fratment.deleteSelectedItems();
						 mode.finish();
						dialog.cancel();
					}
			})
			.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
                   public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
                   }
			});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	
	
	
}
