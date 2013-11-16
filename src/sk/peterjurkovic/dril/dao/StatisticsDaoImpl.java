package sk.peterjurkovic.dril.dao;

import sk.peterjurkovic.dril.db.StatisticDbAdapter;
import sk.peterjurkovic.dril.model.Statistics;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * Statistics data access implementation
 * 
 * @author Peter Jurkovič
 * @date Nov 15, 2013
 *
 */
public class StatisticsDaoImpl implements StatisticsDao {
	
	private static final int SESSION_TIMEOUT = 900000;
	
	private StatisticDbAdapter statisticDbAdapter;
	
	public StatisticsDaoImpl(){}
	
	public StatisticsDaoImpl(StatisticDbAdapter statisticDbAdapter){
		this.statisticDbAdapter = statisticDbAdapter;
	}
	
	public StatisticsDaoImpl(Context context){
		this.statisticDbAdapter = new StatisticDbAdapter(context);
	}
	
	@Override
	public Statistics getSessionStatisticsOrCreateNew() {
		final long threshold = System.currentTimeMillis() - SESSION_TIMEOUT;
		Statistics statistics = statisticDbAdapter.getSessionsStatistics(threshold);
		if(statistics == null){
			statistics = new Statistics();
			create(statistics);
		}
		Log.i("StatisticsDao", statistics.toString());
		return statistics;
	}

	@Override
	public void updateStatistics(Statistics statistics) {
		statistics.setChanged(System.currentTimeMillis());
		statisticDbAdapter.updateStatistics(statistics);
	}

	@Override
	public void removeAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Cursor getAllStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void create(Statistics statistics) {
		statisticDbAdapter.createStatistics(statistics);
	}

	@Override
	public Statistics getById(final long id) {
		return statisticDbAdapter.getById(id);
	}

	

}
