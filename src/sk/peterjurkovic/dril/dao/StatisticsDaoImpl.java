package sk.peterjurkovic.dril.dao;

import sk.peterjurkovic.dril.db.StatisticDbAdapter;
import sk.peterjurkovic.dril.model.Statistics;
import android.database.Cursor;

/**
 * Statistics data access implementation
 * 
 * @author Peter Jurkovič
 * @date Nov 15, 2013
 *
 */
public class StatisticsDaoImpl implements StatisticsDao {
	
	private StatisticDbAdapter statisticDbAdapter;
	
	public StatisticsDaoImpl(){}
	
	public StatisticsDaoImpl(StatisticDbAdapter statisticDbAdapter){
		this.statisticDbAdapter = statisticDbAdapter;
	}
	
	@Override
	public Statistics getSessionStatisticsOrCreateNew() {
		final long now = System.currentTimeMillis();
		Statistics statistics = statisticDbAdapter.getSessionsStatistics(now);
		if(statistics == null){
			statistics = new Statistics();
		}
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
