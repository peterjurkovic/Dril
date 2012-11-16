package sk.peterjurkovic.dril;

import java.util.List;

import org.json.JSONObject;

import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.model.Word;
import sk.peterjurkovic.dril.updater.JSONParser;
import sk.peterjurkovic.dril.updater.JSONReciever;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ImportIdActivity extends MainActivity {
	
	// VARIABLES ------------------------------------------
	public final int ACTIVITY_CHOOSE_FILE = 1;
	  
	private long lectureId;
	
	private EditText importIdInput;
	
	
	// METHODS  ------------------------------------------
	
	
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.import_id_activity);

	    Intent i = getIntent();
	    
	    lectureId = i.getLongExtra(EditLectureActivity.EXTRA_LECTURE_ID, 0);
	    
	    ((TextView)findViewById(R.id.importLectureName))
	    		.setText(WordActivity.getLectureName(this, lectureId));
	    
	    importIdInput = (EditText)findViewById(R.id.importIdInput);
	    
	    ImageButton goHome = (ImageButton) findViewById(R.id.home);
	    goHome.setOnClickListener(new View.OnClickListener() {
	          public void onClick(View v) {
	              startActivity( new Intent(ImportIdActivity.this, DashboardActivity.class) );
	          }
	    });
	    
	    Button btn = (Button) this.findViewById(R.id.importBtn);
	    btn.setOnClickListener(new OnClickListener() {
	      @Override
	      public void onClick(View v) {
	    	  startImport();
	      }
	    });
	  }
	  
	  private void startImport(){
		  String importId =  importIdInput.getText().toString();
		  if(!importId.matches("\\d{7}")){
			  Toast.makeText(this, R.string.import_id_invalid, Toast.LENGTH_LONG).show();
			  return;
		  }
		  new ImportData(this, importId).execute();
	  }


			private class ImportData extends AsyncTask<Void, Void, Integer>{
			 		
			  private ProgressDialog dialog;
			  private Context context;
			  private String importId;
			  
			  public ImportData(Context context, String importId){
				  this.context = context;
				  this.importId = importId;
				  dialog = ProgressDialog.show( this.context , "" , 
							this.context.getResources().getString(R.string.loading), true);
			  }
			  
			  @Override
				protected void onPreExecute() {
				    dialog.show();
				}
			  
		
				@Override
				protected Integer doInBackground(Void... params) {
					List<Word> words = null;
					try{
						// Retrieving
						JSONReciever jsonReciever = new JSONReciever(importId);
						JSONObject jsonData	 = jsonReciever.getJSONData( JSONReciever.FOR_OWN_WORD_ACTION );
						// Parsing
						JSONParser jsonParser = new JSONParser();
						words = jsonParser.parseWordsFromJSONArray(
									jsonData.getJSONArray(JSONParser.TAG_WORDS),
									lectureId
								);
						if(words.size() != 0){
							WordDBAdapter wordDBAdapter = new WordDBAdapter(context);
							wordDBAdapter.saveWordList(words);
						}
					}catch(Exception e){
						return -1;
					}
					
					return words.size();
				}
				
				@Override
					protected void onPostExecute(Integer result) {
						String resultMessage;
						if(result == 0){
							resultMessage = getResources().getString( R.string.import_id_not_found);
						}else if(result == -1){
							resultMessage = getResources().getString( R.string.import_id_failed);
						}else{
							resultMessage = getResources().getString( R.string.import_success, result);
						}
						dialog.dismiss();
						showResultDialog(resultMessage);
					}
			}
	  
	  
	  
	  public void showResultDialog(String responseMsg){
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder
				.setTitle(R.string.import_status)
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
}
