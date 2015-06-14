package sk.peterjurkovic.dril.v2.activities;


import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.io.DrilBackup;
import sk.peterjurkovic.dril.io.DrilRestore;
import sk.peterjurkovic.dril.utils.StringUtils;
import sk.peterjurkovic.dril.v2.constants.Constants;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author Peter Jurkovič
 * @date Dec 1, 2013
 *
 */
public class BackupRestoreActivity extends BaseActivity {
	
	public static final String ACTION_KEY = "backuprestoreaction"; 
	private static final int ACTIVITY_CHOOSE_FILE = 1;

	
	private boolean isBackupAction;
	private Button processBtn;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.v2_backup_layout);
		
		Intent intent =  getIntent();
		isBackupAction = intent.getBooleanExtra(ACTION_KEY, true);
		processBtn = (Button)findViewById(R.id.restoreBackupBtn);
		
		if(!isBackupAction){
			((TextView)findViewById(R.id.restoreAlert)).setVisibility(View.VISIBLE);
			processBtn.setText(R.string.restoreSelectFile);
		}
	
		processBtn.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				if(isBackupAction){
					new DrilBackup(context).execute();
				}else{
					showRestoreConfirmationDialog();
				}
				
			}
		});
		
		((LinearLayout)findViewById(R.id.backupRestoreHelp)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Uri uriUrl = Uri.parse(Constants.BACKUP_RESTORE_HELP_URL);
		        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
		        startActivity(launchBrowser);
			}
		});
	}
	
	
	public void showRestoreConfirmationDialog(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
			.setTitle(R.string.confirmRestore)
			.setMessage(getString(R.string.confirmRestoreMsg))
			.setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						processFileSelection();
						dialog.cancel();
					}
			})
			.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
                   public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
                   }
			});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	
	private void processFileSelection(){
		Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
		chooseFile.setType("file/*");
		Intent intent = Intent.createChooser(chooseFile, "Choose a file");
		startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
	}
	
	
	@Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch(requestCode) {
	      case ACTIVITY_CHOOSE_FILE: {
	        if (resultCode == RESULT_OK){
	          Uri uri = data.getData();
	          final String filepath = uri.getPath();
	          if(!StringUtils.isBlank(filepath)){
	        	  new DrilRestore(context, filepath).execute();
	          }
	        }
	      }
	    }
	  }
}
