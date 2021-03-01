package sk.peterjurkovic.dril.v2.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.BookDBAdapter;
import sk.peterjurkovic.dril.sync.LoadDrilRequest;
import sk.peterjurkovic.dril.sync.SyncManager;
import sk.peterjurkovic.dril.utils.AppRater;


/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Oct 24, 2013
 * @version 2.0
 */
public class DashboardActivity extends BaseActivity {

	private Button login;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    if(!session.areLanguagesSet()){
        	Intent i = new Intent(context, ChooseLanguageActivity.class);
        	startActivity(i);
        	finish();
        	return;
        }
		setContentView(R.layout.v2_dashboard);
				
		 	Button startDrilButton = (Button) findViewById(R.id.btn_start);
	        
	        context = this;
	        
	        Button btnBook = (Button) findViewById(R.id.btn_book);

	        Button btn_stats = (Button) findViewById(R.id.btn_stats);
	        
	        Button btn_info = (Button) findViewById(R.id.btn_info);
	              
	        
	        startDrilButton.setOnClickListener(new View.OnClickListener() {
	            @Override
				public void onClick(View view) {
	            		Intent i = new Intent(context, DrilActivity.class);
	            		startActivity(i);
	            }
	        });
	        
	        btnBook.setOnClickListener(new View.OnClickListener() {
	            @Override
				public void onClick(View view) {
	            		Intent i = new Intent(context, BookListActivity.class);
	            		startActivity(i);
	            }
	        });
	        
	        btn_stats.setOnClickListener(new View.OnClickListener() {
	            @Override
				public void onClick(View view) {
	            	Intent i = new Intent(context, StatisticActivity.class);
            		startActivity(i);
		        }
		    });
	        
	        btn_info.setOnClickListener(new View.OnClickListener() {
	            @Override
				public void onClick(View view) {
	            	Intent i = new Intent(context, HelpActivity.class);
	            	startActivity(i);
		        }
		    });
	        
	        login = (Button) findViewById(R.id.login);
	      
	   
	       
	        if(session.isUserLoggedIn()){
	        	login.setVisibility(View.GONE);
	        }else{
	        	final long bookCount = new BookDBAdapter(this).getBooksCount();
	        	if(bookCount == 0){
	 	        	new LoadDrilRequest(context, session).send();
	 	        }
	        	login.setOnClickListener(new View.OnClickListener() {
	  				@Override
	  				public void onClick(View v) {
	  					Intent i = new Intent(context, LoginActivity.class);
	  	            	startActivity(i);
	  				}
	  			});
	        }
	        AppRater.app_launched(this);
	        if(session.isUserLoggedIn()){
	        	new SyncManager(context, false).execute();
	        }
	}
	
	@Override
	protected void onResume() {
		if(login != null){
		 if(session.isUserLoggedIn()){
	        	login.setVisibility(View.GONE);
	        }else{
	        	login.setOnClickListener(new View.OnClickListener() {
	  				@Override
	  				public void onClick(View v) {
	  					Intent i = new Intent(context, LoginActivity.class);
	  	            	startActivity(i);
	  				}
	  			});
	        }
		} 
		super.onResume();
	}
	
		
	
	/**
	 * If update is not available show dialog with close button only.
	 * 
	 */
	public void showNoActionDialog(String responseMsg){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
			.setTitle(R.string.update_status)
			.setMessage(responseMsg)
			.setCancelable(false)
			.setNegativeButton(R.string.ok,new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,int id) {
							dialog.cancel();
						}
			});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.v2_dashboard_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.backup :
				startActivityBackupRestore(Boolean.TRUE);
			return true;
			case R.id.restore :
				startActivityBackupRestore(Boolean.FALSE);
			return true;
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void startActivityBackupRestore(final boolean isBackup){
		Intent i = new Intent(context, BackupRestoreActivity.class);
		i.putExtra(BackupRestoreActivity.ACTION_KEY, isBackup);
		startActivity(i);
	}

	public Context getContext() {
		return context;
	}
	
		

	
}
