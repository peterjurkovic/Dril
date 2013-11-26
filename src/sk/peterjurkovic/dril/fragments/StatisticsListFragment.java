package sk.peterjurkovic.dril.fragments;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.adapter.StatisticAdapter;
import sk.peterjurkovic.dril.db.StatisticDbAdapter;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 
 * @author Peter Jurkovič
 * @date Nov 17, 2013
 *
 */
public class StatisticsListFragment extends ListFragment implements StatisticsHeader{


	private StatisticAdapter statisticAdapter;
	private StatisticDbAdapter statisticDbAdapter;
	private Context mContext;
	private String title;

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view  = inflater.inflate(R.layout.v2_statistics_list_fragment, container, false);
		((TextView)view.findViewById(R.id.statisticSessionHeader)).setText(getString(R.string.header_stats_sessions));
		return view;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i("sa", "onActivityCreated");
		registerForContextMenu( getListView() );
		statisticDbAdapter = new StatisticDbAdapter(mContext);
		updateList();
	}
	
	public void updateList(){
		//new LoadData().execute();
		Cursor cursor = statisticDbAdapter.getAllStatistics();
		if(cursor != null && cursor.getCount() > 0){
			statisticAdapter = new StatisticAdapter(mContext, cursor, true);
			setListAdapter(statisticAdapter);
			statisticAdapter.notifyDataSetChanged();
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		setListAdapter(null);
	}
	
	@Override
	public String getTitle() {
		if(title == null){
			return "";
		}
		return title;
	}
		

}
