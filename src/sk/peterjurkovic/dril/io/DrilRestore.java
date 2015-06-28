package sk.peterjurkovic.dril.io;

import java.io.File;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.DBAdapter;
import sk.peterjurkovic.dril.dto.BackupRestoreDto;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import sk.peterjurkovic.dril.v2.constants.Constants;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.analytics.tracking.android.Log;

public class DrilRestore extends AsyncTask<Void, Void, BackupRestoreDto>{
	
	private final Context context;
	private final String filepath;
	
	public DrilRestore(Context context, final String filepath){
		this.context = context;
		this.filepath = filepath;
	}
	
	public DrilRestore(Context context){
		this.context = context;
		this.filepath = null;
	}
	
	@Override
	protected BackupRestoreDto doInBackground(Void... params) {
		return processRestore(false);
	}

	@Override
	protected void onPostExecute(BackupRestoreDto state) {
		if(state.isSuccess()){
			Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_LONG).show();;
		}else{
			Toast.makeText(context, context.getString((Integer)state.getData()), Toast.LENGTH_LONG).show();;
			logError();
		}
		
		
	}
	
	public BackupRestoreDto processRestore(boolean loginRestore){
		BackupRestoreDto state = new BackupRestoreDto();
		File data = Environment.getDataDirectory();
		final String databasePath =  "/data/" +context.getApplicationContext().getPackageName()+ "/databases/";
		final String databasePathWithName = databasePath + DBAdapter.DATABASE_NAME;
		final String databasePathWithBackupName =  databasePathWithName+".backup";
		try{
			
			File externalDatabaseFile = null;
			if(loginRestore){
				File sd = Environment.getExternalStorageDirectory();
				if(!sd.canWrite()){
					return state;
				}	
				externalDatabaseFile = new File(sd.getAbsolutePath() +File.separator+ Constants.IO_DRIL_FOLDER_NAME +File.separator+ DrilBackup.BACKUP_DB_NAME);
				if(!externalDatabaseFile.isFile()){
					return state;
				}
			}else{
			
				final String ext = MimeTypeMap.getFileExtensionFromUrl(filepath  );
				final int startIndex = filepath.indexOf("_");
				final int endIndex = filepath.indexOf(".");
				
				if(startIndex == -1 || endIndex == -1 || startIndex > endIndex){
					state.setData(R.string.error_invalid_filename);
					return state;
				}
				
				final String version = filepath.substring(startIndex + 1, endIndex);
				
				if(!version.matches("\\d") || (Integer.valueOf(version) != 3 || Integer.valueOf(version) != 4)){
					state.setData(R.string.error_invalid_version);
					
				}
				
				if(!ext.equals("dril")){
					state.setData(R.string.error_invalid_file);
					return state;
				}
				
				externalDatabaseFile = new File(filepath);
				
				if(!externalDatabaseFile.exists() || !externalDatabaseFile.isFile()){
					state.setData(R.string.error_notExists);
					return state;
				}
			}
			
			File databaseFile = new File(data, databasePathWithName);
			File databaseBackUp = new File(data, databasePathWithBackupName);
			
			databaseFile.renameTo(databaseBackUp);
			
			File newDatabaseFile = new File(data, databasePathWithName);
			DrilBackup.copy(externalDatabaseFile, newDatabaseFile);
            databaseBackUp.delete();
            
            state.setSuccess(true);
            GoogleAnalyticsUtils.logAction(
        			context, 
        			GoogleAnalyticsUtils.CATEGORY_PROCESSING_ACTION,
    				GoogleAnalyticsUtils.ACTION_RESULT, 
    				"restore",
    				1l);
		}catch(Exception e){
			state.setData(R.string.error_ocurred);
			 Log.e(e);
		}
		return state;
	}
	
	private void logError(){
		 GoogleAnalyticsUtils.logAction(
     			context, 
     			GoogleAnalyticsUtils.CATEGORY_PROCESSING_ACTION,
 				GoogleAnalyticsUtils.ACTION_RESULT, 
 				"restore",
 				0l);
	}
	
}
