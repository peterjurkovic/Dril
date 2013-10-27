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
import android.view.View;
import android.widget.Button;

/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Oct 24, 2013
 * @version 2.0
 */
public class DashboardActivity extends BaseActivity implements  AsyncLIstener{
	
	Button btnStart = null;
	WordDBAdapter wordAdapter = null;
	long countOfActiveWords = 0;
	private Context context;
	int pos = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.v2_dashboard);
		 wordAdapter = new WordDBAdapter(this);
	        btnStart = (Button) findViewById(R.id.btn_start);
	        
	        context = this;
	        
	        Button btnBook = (Button) findViewById(R.id.btn_book);

	        Button btn_stats = (Button) findViewById(R.id.btn_stats);
	        
	        Button btn_info = (Button) findViewById(R.id.btn_info);
	        
	        Button btn_update = (Button) findViewById(R.id.btn_update);
	        
	        btnBook.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            		Intent i = new Intent(context, BookListActivity.class);
	            		startActivity(i);
	            }
	        });
	        
	        btn_stats.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	
	            	showDownloadDialog(1);
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
						public void onClick(DialogInterface dialog,int id) {
							dialog.cancel();
						}
			})
			.setPositiveButton(R.string.yes ,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
					UpdateSaver chfu = new UpdateSaver( context );
			    	chfu.execute();
			        
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
						public void onClick(DialogInterface dialog,int id) {
							dialog.cancel();
						}
			});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.v2_main, menu);
		return true;
	}


	@Override
	public void onCheckResponse(Integer response) {
		switch(response){
		case CheckForUpdate.STATE_NO_INTERNET_CONN :
			showNoActionDialog(getResources().getString( R.string.update_no_conn));
			break;
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
}
