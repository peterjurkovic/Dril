package sk.peterjurkovic.dril.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import sk.peterjurkovic.dril.utils.DeviceUtils;

public abstract class DatabaseHelper extends SQLiteOpenHelper {
	
	public final static String[] SYNC_TABLES = new String[] {
		BookDBAdapter.TABLE_BOOK,
		LectureDBAdapter.TABLE_LECTURE,
		WordDBAdapter.TABLE_WORD
	};	
	
	public static final String DEVICE_ID = "deviceId";
	public static final String SERVER_LAST_SYNC = "server_lastsync";
	public static final String CLIENT_LAST_SYNC = "client_lastsync";
	
	public final static String INIT_TIME = "1970-01-01 00:00:00";
	
	public static final int DATABASE_VERSION = 4;
	public static final String DATABASE_NAME = "dril";
	
	public static final String ID = "_id";
	public static final String SERVER_ID = "sid";
	public static final String LAST_CHANGED = "last_changed";
	
	protected final static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	protected final static Lock r = rwl.readLock();
	protected final static Lock w = rwl.writeLock();

	public static final String TAG = "DBAdapter";
	
	protected final Context context;
	
     DatabaseHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
         this.context = context;
     }
    
     
     @Override
     public void onCreate(SQLiteDatabase db) {
     	 createTables(db);
     	 initSyncSettings();
     }

     @Override
     public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {               
     	if (newVersion > oldVersion){          
     		try {              
     		
     		switch(oldVersion){
	        		case 2:
	        			db.beginTransaction();
	        			db.execSQL("ALTER TABLE book ADD COLUMN author TEXT");
	        			db.execSQL("ALTER TABLE book ADD COLUMN answer_lang_fk INTEGER NOT NULL DEFAULT (0)");
	        			db.execSQL("ALTER TABLE book ADD COLUMN question_lang_fk INTEGER NOT NULL DEFAULT (0)");
	        			db.execSQL("ALTER TABLE book ADD COLUMN changed INTEGER DEFAULT (0)");
	        			db.execSQL("ALTER TABLE book ADD COLUMN created INTEGER DEFAULT (0)");
	        			db.execSQL("ALTER TABLE book ADD COLUMN sync INTEGER NOT NULL DEFAULT (0)");
	        			
	        			db.execSQL("ALTER TABLE lecture ADD COLUMN changed INTEGER DEFAULT (0)");
	        			db.execSQL("ALTER TABLE lecture ADD COLUMN created INTEGER DEFAULT (0)");
	        			
	        			db.execSQL("ALTER TABLE word ADD COLUMN changed INTEGER DEFAULT (0)");
	        			db.execSQL("ALTER TABLE word ADD COLUMN favorite INTEGER DEFAULT (0)");
	        			db.execSQL("ALTER TABLE word ADD COLUMN created INTEGER DEFAULT (0)");
	        			db.execSQL("ALTER TABLE word ADD COLUMN avg_rate REAL NOT NULL DEFAULT (0)");
	        			
	        			db.execSQL("DROP TABLE IF EXISTS statistic");
	        			db.execSQL(StatisticDbAdapter.TABLE_STATISTIC_CREATE);
	        			db.setTransactionSuccessful();
	        		break;
	        		case 3:
	        			Log.i(getClass().getName(),"Updating prev version");
        				db.beginTransaction();
        				db.execSQL("ALTER TABLE "+ WordDBAdapter.TABLE_WORD+" RENAME TO tmp;");
        				createSyncTable(db, WordDBAdapter.TABLE_WORD_CREATE);
        				db.execSQL("INSERT INTO word SELECT _id,question,answer,active,lecture_id,rate,hit,avg_rate,null,(datetime('now')) FROM tmp;");
        				db.execSQL("DROP TABLE tmp;");
        				
        				db.execSQL("ALTER TABLE "+ LectureDBAdapter.TABLE_LECTURE+" RENAME TO tmp;");
        				createSyncTable(db, LectureDBAdapter.TABLE_LECTURE_CREATE);
        				db.execSQL("INSERT INTO lecture SELECT _id, lecture_name, book_id,null,(datetime('now')) FROM tmp;");
        				db.execSQL("DROP TABLE tmp;");
        				
        				db.execSQL("ALTER TABLE "+ BookDBAdapter.TABLE_BOOK+" RENAME TO tmp;");
        				createSyncTable(db, BookDBAdapter.TABLE_BOOK_CEREATE);
        				db.execSQL("INSERT INTO book SELECT _id, book_name, answer_lang_fk, question_lang_fk, 0, null, 0, null,(datetime('now')) FROM tmp;");
        				db.execSQL("DROP TABLE tmp;");
        				addIndexes(db);      			
        				addSyncManagementTables(db);
        				db.setTransactionSuccessful();
						Log.i(getClass().getName(),"Update finished.");
        				initSyncSettings();
	        		break;
	        	}
     		
     		} catch (SQLException e) {
     			 Log.e(TAG, e.getMessage());
     		}finally {
                 db.endTransaction();
             }
     	}
     }


     
     private void addIndexes(SQLiteDatabase db){
    	 db.execSQL("CREATE INDEX lecture_fk_idx ON word (lecture_id);");
		 db.execSQL("CREATE INDEX book_fk_idx ON lecture (book_id);");
     }
               
     private void createTables(SQLiteDatabase db){
		db.beginTransaction();
		createSyncTable(db, BookDBAdapter.TABLE_BOOK_CEREATE);
		createSyncTable(db, LectureDBAdapter.TABLE_LECTURE_CREATE);
		createSyncTable(db, WordDBAdapter.TABLE_WORD_CREATE);
		db.execSQL(StatisticDbAdapter.TABLE_STATISTIC_CREATE);
		addIndexes(db);
		addSyncManagementTables(db);
		db.setTransactionSuccessful();
		db.endTransaction();
     }
     
     
     /*
 	 * Modifies CREATE TABLE sql query for synchronization and creates table
 	 */
 	protected void createSyncTable(SQLiteDatabase db, String createSQL) {
 		
 		int posA = createSQL.lastIndexOf(")");
 		int posB = createSQL.indexOf("CREATE TABLE ") + 13;
 		int posC = createSQL.indexOf(" (");
 		
 		String tableName = createSQL.substring(posB, posC);
 		
 		// if _syncID is NULL, mean its not yet in global database - global DB generates this id	
 		// if _lastChange is NULL, means no change since last synchronization
 		String modCreateSQL = createSQL.substring(0, posA) + 
 				"," + SERVER_ID + " INTEGER DEFAULT NULL," +
 				LAST_CHANGED + " TIMESTAMP DEFAULT (datetime('now'))," +
 				"UNIQUE(" + SERVER_ID + ")" +	
 				")";
 		
 		db.execSQL(modCreateSQL);
 		
 		db.execSQL("CREATE TRIGGER _sync_delete_" + tableName + " " +
 			     "AFTER DELETE ON " + tableName + " " +
 			     "FOR EACH ROW WHEN OLD."+ SERVER_ID + " IS NOT NULL BEGIN " +
 			     "INSERT INTO deleted_rows(tableName," + SERVER_ID + ") " +
 			     "VALUES('" + tableName + "',OLD." + SERVER_ID + "); " +
 			     "END;");		
 		
 	}
     
     /*
 	 * Adds synchronization tables
 	 */
 	protected void addSyncManagementTables(SQLiteDatabase db) {
 		db.execSQL("CREATE TABLE deleted_rows( " +
 				"tableName VARCHAR(100) NOT NULL," +
 				SERVER_ID + " INTEGER NOT NULL," +
 				"deleted TIMESTAMP NOT NULL DEFAULT (datetime('now'))" +			
 				")");
 	}	
 	
 	

 	protected void initSyncSettings() {
		Log.i(TAG,"Init sync settings");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String deviceID = preferences.getString(DEVICE_ID, null);
		
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(SERVER_LAST_SYNC, INIT_TIME);
		editor.putString(CLIENT_LAST_SYNC, INIT_TIME);
		if (deviceID == null) {
			editor.putString(DEVICE_ID, DeviceUtils.getUniquePsuedoID());
		}
		editor.commit();
	}
	

}
