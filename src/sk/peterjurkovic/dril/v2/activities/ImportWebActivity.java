package sk.peterjurkovic.dril.v2.activities;

import java.util.List;

import org.json.JSONObject;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.LectureDBAdapter;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.model.Word;
import sk.peterjurkovic.dril.updater.JSONParser;
import sk.peterjurkovic.dril.updater.JSONReciever;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
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
import android.widget.LinearLayout;
import android.widget.Toast;

public class ImportWebActivity extends BaseActivity {
	

	public final int ACTIVITY_CHOOSE_FILE = 1;
	private final static String GA_ACTION_NAME = "ImportId";
	
	private long bookId = 0;
	private long lectureId = 0;
	private boolean createLecture = true;
	
	private EditText importIdInput;
	
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.v2_import_id_activity);

	    Intent i = getIntent();
	    createLecture = i.getBooleanExtra(ImportMenuActivity.EXTRA_CREATE_LECTURE, false);
	    
	    if(createLecture){
		   bookId = i.getLongExtra(ImportMenuActivity.EXTRA_ID, 0);
		}else{
		   lectureId = i.getLongExtra(ImportMenuActivity.EXTRA_ID, 0);
		}
	    
	    Button btn = (Button) this.findViewById(R.id.importBtn);
	    btn.setOnClickListener(new OnClickListener() {
	      @Override
	      public void onClick(View v) {
	    	  startImport();
	      }
	    });
	    importIdInput = (EditText)findViewById(R.id.importIdInput);
	    LinearLayout info = (LinearLayout)findViewById(R.id.importInfo);
	    info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToHomePage(v);
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
				  dialog = ProgressDialog.show( this.context , "" , this.context.getResources().getString(R.string.loading), true);
			  }
			  
			  @Override
				protected void onPreExecute() {
				    dialog.show();
				}
			  
		
				@Override
				protected Integer doInBackground(Void... params) {
					List<Word> words = null;
					WordDBAdapter wordDBAdapter = null;
					try{
						// Retrieving
						JSONReciever jsonReciever = new JSONReciever(importId);
						JSONObject jsonData	 = jsonReciever.getJSONData( JSONReciever.FOR_OWN_WORD_ACTION );
						// Parsing
						JSONParser jsonParser = new JSONParser();
						if(createLecture){
							String lectureName = jsonData.getString(JSONParser.TAG_NAME);
							createLecture(lectureName);
						}
						words = jsonParser.parseWordsFromJSONArray(
									jsonData.getJSONArray(JSONParser.TAG_WORDS),
									lectureId
								);
						
						if(words.size() != 0){
							wordDBAdapter = new WordDBAdapter(context);
							wordDBAdapter.saveWordList(words);
						}
					}catch(Exception e){
						logException(e.getMessage(), false);
						if(createLecture){
							removeCreatedLecture(lectureId, context);
						}
						return -1;
					}finally{
						if(wordDBAdapter != null){
							wordDBAdapter.close();
						}
					}
					
					return words.size();
				}
				
				@Override
					protected void onPostExecute(final Integer result) {
						String resultMessage;
						if(result == 0){
							resultMessage = getResources().getString( R.string.import_id_not_found);
						}else if(result == -1){
							resultMessage = getResources().getString( R.string.import_id_failed);
						}else{
							resultMessage = getResources().getString( R.string.import_success, result);
							
						}
						logResult(result);
						dialog.dismiss();
						showResultDialog(resultMessage, result);
					}
			}
	  
	private void logResult(final Integer result){
		GoogleAnalyticsUtils.logAction(this, 
				GoogleAnalyticsUtils.CATEGORY_PROCESSING_ACTION,
				GoogleAnalyticsUtils.ACTION_RESULT,
				GA_ACTION_NAME, 
				Long.valueOf(result));
	}
	  
	  
	  public void showResultDialog(final String responseMsg,final Integer result){
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder
				.setTitle(R.string.import_status)
				.setMessage(responseMsg)
				.setCancelable(false)
				.setNegativeButton(R.string.ok,new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int id) {
								dialog.cancel();
								if(result > 0){
									gotIntoLecture();
								}
							}
				});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
	  
	  
	  public void createLecture(final String lectureName) {
		 if(bookId == 0 && createLecture){
			 return;
		 }
		try {
			LectureDBAdapter lectureDbAdapter = new LectureDBAdapter(this);
			lectureId = lectureDbAdapter.insertLecture(bookId, lectureName);
		} catch (Exception e) {
			logException(e.getMessage(), false);
		} 
	}
	  
	
	  public void removeCreatedLecture(final long id, Context context) {
		  if(id == 0 && createLecture){
			  return;
		  }
		  try {
				LectureDBAdapter lectureDbAdapter = new LectureDBAdapter(context);
				lectureDbAdapter.deleteLecture(id);
			} catch (Exception e) {
				logException(e.getMessage(), false);
			} 
		}  
	  
	  private void gotIntoLecture(){
		  Intent i = new Intent(this,  WordActivity.class);
		  i.putExtra( WordActivity.LECTURE_ID_EXTRA, lectureId);
		  startActivity(i);
	  }
}
