package sk.peterjurkovic.dril.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.DBAdapter;
import sk.peterjurkovic.dril.dto.BackupRestoreDto;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.analytics.tracking.android.Log;

public class DrilRestore extends AsyncTask<Void, Void, BackupRestoreDto>{
	
	private Context context;
	private String filepath;
	
	public DrilRestore(Context context, final String filepath){
		this.context = context;
		this.filepath = filepath;
	}
	
	@Override
	protected BackupRestoreDto doInBackground(Void... params) {
		return processRestore();
	}

	@Override
	protected void onPostExecute(BackupRestoreDto state) {
		if(state.isSuccess()){
			Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_LONG).show();;
		}else{
			Toast.makeText(context, context.getString((Integer)state.getData()), Toast.LENGTH_LONG).show();;
		}
		
		
	}
	
	private BackupRestoreDto processRestore(){
		BackupRestoreDto state = new BackupRestoreDto();
		File data = Environment.getDataDirectory();
		final String databasePath =  "/data/" +context.getApplicationContext().getPackageName()+ "/databases/";
		final String databasePathWithName = databasePath + DBAdapter.DATABASE_NAME;
		final String databasePathWithBackupName =  databasePathWithName+".backup";
		try{
			
			final String ext = MimeTypeMap.getFileExtensionFromUrl(filepath);
			final int startIndex = filepath.indexOf("_");
			final int endIndex = filepath.indexOf(".");
			
			if(startIndex == -1 || endIndex == -1 || startIndex > endIndex){
				state.setData(R.string.error_invalid_filename);
				return state;
			}
			
			final String version = filepath.substring(startIndex + 1, endIndex);
			
			if(!version.matches("\\d") || Integer.valueOf(version) < DBAdapter.DATABASE_VERSION){
				state.setData(R.string.error_invalid_version);
				return state;
			}
			
			if(!ext.equals("dril")){
				state.setData(R.string.error_invalid_file);
				return state;
			}
			
			File externalDatabaseFile = new File(filepath);
			
			if(!externalDatabaseFile.exists() || !externalDatabaseFile.isFile()){
				state.setData(R.string.error_notExists);
				return state;
			}

			
			File databaseFile = new File(data, databasePathWithName);
			File databaseBackUp = new File(data, databasePathWithBackupName);
			
			databaseFile.renameTo(databaseBackUp);
			
			File newDatabaseFile = new File(data, databasePathWithName);
			
			FileChannel src = new FileInputStream(externalDatabaseFile).getChannel();
            FileChannel dst = new FileOutputStream(newDatabaseFile).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
			
            databaseBackUp.delete();
            state.setSuccess(true);
		}catch(Exception e){
			state.setData(R.string.error_ocurred);
			Log.e(e);
			GoogleAnalyticsUtils.logException(e, context);
		}
		return state;
	}
}
