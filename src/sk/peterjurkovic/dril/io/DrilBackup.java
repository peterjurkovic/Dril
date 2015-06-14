package sk.peterjurkovic.dril.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
	
	private final Context context;
	
	public DrilBackup(Context context){
		this.context = context;
	}
	
	
	@Override
	protected BackupRestoreDto doInBackground(Void... params) {
		return processBackup();
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

	
	private BackupRestoreDto processBackup() {
		    try {
		        File sd = Environment.getExternalStorageDirectory();
		        File data = Environment.getDataDirectory();
		        BackupRestoreDto state = new BackupRestoreDto();
		        state.setData(R.string.error_ocurred);
		        if (sd.canWrite()) {
		        	
		        	final String currentDBPath =  "/data/"+context.getApplicationContext().getPackageName()+"/databases/" + DBAdapter.DATABASE_NAME;	            
		            
		            File currentDB = new File(data, currentDBPath);
		            
		            if(!currentDB.isFile()){
		            	return state;
		            }
		            
		            File drilFolder = new File(sd, Constants.IO_DRIL_FOLDER_NAME);
		            if(!drilFolder.exists()){
		            	drilFolder.mkdir();
		            }
		            
		            Calendar calendar = Calendar.getInstance();
		            
		            final String filename = calendar.get(Calendar.YEAR) + "" +
		            			  calendar.get(Calendar.MONTH) + "" + 
		            			  calendar.get(Calendar.DAY_OF_MONTH) + "_" + DBAdapter.DATABASE_VERSION + ".dril";

		            File backupDB = new File(drilFolder, filename);
		            state.setData(backupDB.getAbsolutePath());
		            if(backupDB.exists() && backupDB.isFile()){
		            	backupDB.delete();
		            }
		            
		            final FileChannel src = new FileInputStream(currentDB).getChannel();
		            final FileChannel dst = new FileOutputStream(backupDB).getChannel();
		            dst.transferFrom(src, 0, src.size());
		            if(src != null){
		            	 src.close();
		    		}
		            if(dst != null){
		            	dst.close();
		            }
		            state.setSuccess(true);
		            GoogleAnalyticsUtils.logAction(
		        			context, 
		        			GoogleAnalyticsUtils.CATEGORY_PROCESSING_ACTION,
		    				GoogleAnalyticsUtils.ACTION_RESULT, 
		    				"backup",
		    				1l);
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
}
