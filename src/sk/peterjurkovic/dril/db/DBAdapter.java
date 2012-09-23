package sk.peterjurkovic.dril.db;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter{
	
	
	public static final int DATABASE_VERSION = 1;
	
	public static final String DATABASE_NAME = "dril";
	
	public static final String TAG = "DBAdapter";
    
	private DatabaseHelper openHelper;
	

	SQLiteDatabase cdb;
	
	
    /**
     * Constructor
     * @param context
     */
    public DBAdapter(Context context){
        openHelper = new DatabaseHelper(context);
        openHelper.initializeDataBase();
    }
    
   
	 private static class DatabaseHelper extends SQLiteOpenHelper {
		   
		   /*
		     * The Android's default system path of the application database in internal
		     * storage. The package of the application is part of the path of the
		     * directory.
		     */
		    private static String DB_DIR = "/data/data/sk.peterjurkovic.dril/databases/";
		    private static String DB_NAME = "dril";
		    private static String DB_PATH = DB_DIR + DB_NAME;
		    private static String OLD_DB_PATH = DB_DIR + "old_" + DB_NAME;

		    private final Context myContext;

		    private boolean createDatabase = false;
		    private boolean upgradeDatabase = false;

		    
		   
	        DatabaseHelper(Context context) {
	            super(context, DATABASE_NAME, null, DATABASE_VERSION);
	            myContext = context;
	            DB_PATH = myContext.getDatabasePath(DB_NAME).getAbsolutePath();
	        }
	        /**
	         * Upgrade the database in internal storage if it exists but is not current. 
	         * Create a new empty database in internal storage if it does not exist.
	         */
	        
	        public void initializeDataBase() {
	            /*
	             * Creates or updates the database in internal storage if it is needed
	             * before opening the database. In all cases opening the database copies
	             * the database in internal storage to the cache.
	             */
	            getWritableDatabase();

	            if (createDatabase) {
	            	Log.d(TAG, "OnCreate() - copying database ...");
	                /*
	                 * If the database is created by the copy method, then the creation
	                 * code needs to go here. This method consists of copying the new
	                 * database from assets into internal storage and then caching it.
	                 */
	                try {
	                    /*
	                     * Write over the empty data that was created in internal
	                     * storage with the one in assets and then cache it.
	                     */
	                    copyDataBase();
	                } catch (IOException e) {
	                    throw new Error("Error copying database");
	                }
	            } else if (upgradeDatabase) {
	            	Log.d(TAG, "OnUpgrade() - upgrading database ...");
	                /*
	                 * If the database is upgraded by the copy and reload method, then
	                 * the upgrade code needs to go here. This method consists of
	                 * renaming the old database in internal storage, create an empty
	                 * new database in internal storage, copying the database from
	                 * assets to the new database in internal storage, caching the new
	                 * database from internal storage, loading the data from the old
	                 * database into the new database in the cache and then deleting the
	                 * old database from internal storage.
	                 */
	                try {
	                    FileHelper.copyFile(DB_PATH, OLD_DB_PATH);
	                    copyDataBase();
	                    SQLiteDatabase old_db = SQLiteDatabase.openDatabase(OLD_DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
	                    SQLiteDatabase new_db = SQLiteDatabase.openDatabase(DB_PATH,null, SQLiteDatabase.OPEN_READWRITE);
	                    /*
	                     * Add code to load data into the new database from the old
	                     * database and then delete the old database from internal
	                     * storage after all data has been transferred.
	                     */
	                } catch (IOException e) {
	                    throw new Error("Error copying database");
	                }
	            }

	        }

	        /**
	         * Copies your database from your local assets-folder to the just created
	         * empty database in the system folder, from where it can be accessed and
	         * handled. This is done by transfering bytestream.
	         * */
	        private void copyDataBase() throws IOException {
	            /*
	             * Close SQLiteOpenHelper so it will commit the created empty database
	             * to internal storage.
	             */
	            close();

	            /*
	             * Open the database in the assets folder as the input stream.
	             */
	            InputStream myInput = myContext.getAssets().open(DB_NAME);

	            /*
	             * Open the empty db in interal storage as the output stream.
	             */
	            OutputStream myOutput = new FileOutputStream(DB_PATH);

	            /*
	             * Copy over the empty db in internal storage with the database in the
	             * assets folder.
	             */
	            FileHelper.copyFile(myInput, myOutput);

	            /*
	             * Access the copied database so SQLiteHelper will cache it and mark it
	             * as created.
	             */
	            getWritableDatabase().close();
	        }
	        
	        @Override
	        public void onCreate(SQLiteDatabase db) {
	        	//Log.d(TAG, "onCreate creating db...");
	        	 createDatabase = true;
	        }

	        @Override
	        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
	        int newVersion) {               
	        	 upgradeDatabase = true;
	        	
	        }
	          
	        public void importData(SQLiteDatabase db, String fileName){
	        	 try {
			         InputStream is = myContext.getResources().getAssets().open(fileName);
			         String[] statements = FileHelper.parseSqlFile(is);
			         for (String statement : statements) {
			        	 db.execSQL(statement);
			         }
			    }catch(Exception ex) {
			         ex.printStackTrace();
			    }
	        }
	        

	 } 
	 
	public SQLiteDatabase openReadableDatabase(){
		return openHelper.getReadableDatabase();
	}
	
	public SQLiteDatabase openWriteableDatabase(){
		return openHelper.getWritableDatabase();
	}
	
	/**
	 * close the db 
	 * return type: void
	 */
	public void close(){
		if(cdb != null){
			cdb.close();
		}
		if(openHelper != null){
			openHelper.close();
		}
	}
	
	
	
	public Cursor getLecturesIdByBookId(long id){
	   SQLiteDatabase db = openReadableDatabase();
	   String[] selectionArgs = { String.valueOf(id)};
	   Cursor result = db.rawQuery("SELECT "+ LectureDBAdapter.LECTURE_ID +" FROM "+ 
			   LectureDBAdapter.TABLE_LECTURE + " " +
	           "WHERE " + LectureDBAdapter.FK_BOOK_ID + "=? " , selectionArgs);
	   return result;
	}
	
	
	
	 
	
	
}
