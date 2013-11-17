package sk.peterjurkovic.dril.fragments;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.adapter.StatisticAdapter;
import sk.peterjurkovic.dril.db.StatisticDbAdapter;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author Peter Jurkovič
 * @date Nov 17, 2013
 *
 */
public class StatisticsListFragment extends ListFragment{


	private StatisticAdapter statisticAdapter;
	private StatisticDbAdapter statisticDbAdapter;
	private Context mContext;
	
	

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		statisticDbAdapter = new StatisticDbAdapter(activity);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View listView  = inflater.inflate(R.layout.v2_statistics_list_fragment, container, false);
		return listView;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu( getListView() );
		updateList();
	}
	
	public void updateList(){
		new LoadData().execute();
	}
	
	private void closeAdapterCursor(){
		try {
			if(statisticAdapter != null){
				if(!statisticAdapter.getCursor().isClosed())
					statisticAdapter.getCursor().close();
			}
		} catch (Exception e) {
			
		}
	}
	

	
	private class LoadData extends AsyncTask<Void, Void, Cursor>{
		@Override
		protected Cursor doInBackground(Void... params) {
			try {
				closeAdapterCursor();
				return statisticDbAdapter.getAllStatistics();
			} catch (Exception e) {
			}
			return null;
		}
		
		@Override
        protected void onPostExecute(Cursor cursor){
			if(cursor != null){
				statisticAdapter = new StatisticAdapter(mContext, cursor, true);
				setListAdapter(statisticAdapter);
				statisticAdapter.notifyDataSetChanged();
			}
		}
		
	}
	
	
	
}
