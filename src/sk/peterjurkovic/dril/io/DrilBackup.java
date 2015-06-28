package sk.peterjurkovic.dril.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.DBAdapter;
import sk.peterjurkovic.dril.dto.BackupRestoreDto;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import sk.peterjurkovic.dril.v2.constants.Constants;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.Log;

public class DrilBackup extends AsyncTask<Void, Void, BackupRestoreDto>{
	
	public final static String BACKUP_DB_NAME = "backup.dril";
	private final Context context;
	
	public DrilBackup(Context context){
		this.context = context;
	}
	
	
	@Override
	protected BackupRestoreDto doInBackground(Void... params) {
		return processBackup(false);
	}
	
	
	@Override
	protected void onPostExecute(BackupRestoreDto state) {
		if(state.isSuccess()){
			Activity mActivity = ((Activity)context);
			Button button = (Button)mActivity.findViewById(R.id.restoreBackupBtn);
			button.setVisibility(View.GONE);
			TextView uriWrapp = (TextView)mActivity.findViewById(R.id.backupFileLocation);
			uriWrapp.setText((String)state.getData());
			uriWrapp.setVisibility(View.VISIBLE);
			mActivity.findViewById(R.id.backupFileLocationLabel).setVisibility(View.VISIBLE);
			Toast.makeText(context, context.getString(R.string.backup_success), Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(context, context.getString((Integer)state.getData()), Toast.LENGTH_LONG).show();
		}
	}

	
	public BackupRestoreDto processBackup(boolean loginBackup) {
		    try {
		        final String databaseFolder ="/data/"+context.getApplicationContext().getPackageName()+"/databases/";
		    	File sd = Environment.getExternalStorageDirectory();
		        File data = Environment.getDataDirectory();
		        BackupRestoreDto state = new BackupRestoreDto();
		        state.setData(R.string.error_ocurred);
		        if (sd.canWrite()) {
		        	
		        	final String currentDBPath =  databaseFolder + DBAdapter.DATABASE_NAME;	            
		        	File dataFolder = new File(sd, Constants.IO_DRIL_FOLDER_NAME);
			        if(!dataFolder.exists()){
			        	dataFolder.mkdir();
			        }
		            File currentDB = new File(data, currentDBPath);
		            
		            if(!currentDB.isFile()){
		            	return state;
		            }
		            
		            String filename = null;
		            if(loginBackup){
		            	filename = BACKUP_DB_NAME; 
		            	
		            }else{
			            Calendar calendar = Calendar.getInstance();
			            filename = calendar.get(Calendar.YEAR) + "" +
			            			  calendar.get(Calendar.MONTH) + "" + 
			            			  calendar.get(Calendar.DAY_OF_MONTH) + "_" + DBAdapter.DATABASE_VERSION + ".dril";
		            }      
		            
		            File  backupDB = new File(dataFolder, filename);
		            state.setData(backupDB.getAbsolutePath());
		            if(backupDB.exists() && backupDB.isFile()){
		            	backupDB.delete();
		            };
		            
		            copy(currentDB, backupDB);
		            state.setSuccess(true);
		            return state;
		        }else{
		        	GoogleAnalyticsUtils.logAction(
		        			context, 
		        			GoogleAnalyticsUtils.CATEGORY_PROCESSING_ACTION,
		    				GoogleAnalyticsUtils.ACTION_RESULT, 
		    				"backup",
		    				0l);
		        	state.setData(R.string.error_access_write);
		        }
		} catch (Exception e) {
			 Log.e(e);
			 GoogleAnalyticsUtils.logException(e, context);
		}
		return new BackupRestoreDto();
	}
	
	public static void copy(File source, File destication) throws IOException{
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		try{
			inStream = new FileInputStream(source);
			outStream = new FileOutputStream(destication);
	    	inChannel = inStream.getChannel();
		    outChannel = outStream.getChannel();
		    inChannel.transferTo(0, inChannel.size(), outChannel);
		}finally{
			if(inStream != null){
				  inStream.close();
    		}
            if(outStream != null){
            	outStream.close();
            }
	        if(inChannel != null){
	        	inChannel.close();
	        }
	        if(outChannel != null){
	        	outChannel.close();
	        }
		}
	}
}
