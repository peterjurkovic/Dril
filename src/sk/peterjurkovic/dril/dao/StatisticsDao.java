package sk.peterjurkovic.dril.dao;

import sk.peterjurkovic.dril.model.Statistics;
import android.database.Cursor;

/**
 * 
 * @author Peter Jurkovič
 * @date Nov 15, 2013
 *
 */
public interface StatisticsDao {
	
	Statistics getSessionStatisticsOrCreateNew();
	
	void updateStatistics(Statistics statistics);
	
	void removeAll();
	
	Cursor getAllStatistics();
	
	void create(Statistics statistics);
	
	Statistics getById(long id);
	
	
	
}
