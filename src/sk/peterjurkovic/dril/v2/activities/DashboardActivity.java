package sk.peterjurkovic.dril.v2.activities;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.listener.AsyncLIstener;
import sk.peterjurkovic.dril.updater.CheckForUpdate;
import sk.peterjurkovic.dril.updater.UpdateSaver;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;



/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Oct 24, 2013
 * @version 2.0
 */
public class DashboardActivity extends BaseActivity implements  AsyncLIstener{
	
	private WordDBAdapter wordAdapter = null;
	private  long countOfActiveWords = 0;
	private Context context;
	private int pos = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.v2_dashboard);
		 	wordAdapter = new WordDBAdapter(this);
		 	//wordAdapter.getCountOfActiveWords();
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
	}
	
	
	/**
	 * Dialog, if updates are available
	 * 
	 * @param response
	 */
	public void showDownloadDialog(Integer response){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder
			.setTitle(R.string.update_status)
			.setMessage(R.string.update_available)
			.setCancelable(false)
			.setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,int id) {
							dialog.cancel();
						}
			})
			.setPositiveButton(R.string.yes ,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
					UpdateSaver updater = new UpdateSaver( context );
					updater.sendRequest();
			        
				}
			  })
			;

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
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
			case R.id.checkUpdateMenu :
				checkForUpdate();
			return true;
			
		}
		return super.onOptionsItemSelected(item);
	}
	

	@Override
	public void onCheckResponse(Integer response) {
		switch(response){
		case CheckForUpdate.STATE_NO_INTERNET_CONN :
		case CheckForUpdate.STATE_PARSING_ERROR :
			showNoActionDialog(getResources().getString( R.string.update_failed));
			break;
		case CheckForUpdate.STATE_NO_UPDATE:
			showNoActionDialog(getResources().getString( R.string.up_to_date));
			break;
		default:
			showDownloadDialog(response);
	}
	
	}


	@Override
	public void onUpdatedResponse(Integer response) {
		if(response > 0){
			showNoActionDialog(getResources().getString( R.string.successfully_updated, response));
		}else{
			onCheckResponse(response);
		}
	}


	public Context getContext() {
		return context;
	}
	
	
	private void checkForUpdate(){
		 CheckForUpdate chfu = new CheckForUpdate( context );
         chfu.execute(); 
	}
	
	
}
