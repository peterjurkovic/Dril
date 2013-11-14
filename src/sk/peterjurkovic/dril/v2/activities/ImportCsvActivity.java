package sk.peterjurkovic.dril.v2.activities;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.csv.CSVReader;
import sk.peterjurkovic.dril.db.LectureDBAdapter;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.model.Word;
import sk.peterjurkovic.dril.utils.StringUtils;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class ImportCsvActivity extends BaseActivity {
	
	
	private static final int ACTIVITY_CHOOSE_FILE = 1;
	  
	private long bookId = 0;
	private long lectureId = 0;
	private boolean createLecture = true;
	
	private TextView label = null;
	private EditText input = null;
	

	
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.v2_import_csv_activity);

	    Intent i = getIntent();
	    createLecture = i.getBooleanExtra(ImportMenuActivity.EXTRA_CREATE_LECTURE, false);
	   
	    if(createLecture){
		   bookId = i.getLongExtra(ImportMenuActivity.EXTRA_ID, 0);
		   initInputs();
		}else{
		   lectureId = i.getLongExtra(ImportMenuActivity.EXTRA_ID, 0);
		}
	    
	    Button btn = (Button) this.findViewById(R.id.importBtn);
	    btn.setOnClickListener(new OnClickListener() {
	      @Override
	      public void onClick(View v) {
	    	String lectureName = null;
	    	if(createLecture){
	    		lectureName = input.getText().toString();
	    	}
			if(createLecture && StringUtils.isBlank(lectureName)){
				input.setBackgroundColor( getResources().getColor(R.color.lightRed) );
				return;
			}
			if(createLecture && !StringUtils.isBlank(lectureName)){
				input.setBackgroundColor( getResources().getColor(android.R.color.white) );
				createLecture(lectureName);
			}
			Intent chooseFile;
			Intent intent;
			chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
			chooseFile.setType("file/*");
			intent = Intent.createChooser(chooseFile, "Choose a file");
			startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
			
	      }
	    });
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
	  
	  @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch(requestCode) {
	      case ACTIVITY_CHOOSE_FILE: {
	        if (resultCode == RESULT_OK){
	          Uri uri = data.getData();
	          String filePath = uri.getPath();
	          Log.d("FILERIEDER", filePath);
	          new ImportData(filePath, this).execute();
	        }
	      }
	    }
	  }
	  
	  
	
	  
	  private class ImportData extends AsyncTask<Void, Void, Integer>{
			  
		
			  private ProgressDialog dialog;
			  private String filePath;
			  private Context context;
			  
			  public ImportData(String filePath, Context context){
				  this.filePath = filePath;
				  this.context = context;
				  dialog = ProgressDialog.show( this.context , "" , 
							this.context.getResources().getString(R.string.loading), true);
			  }
			  
			  @Override
				protected void onPreExecute() {
				    dialog.show();
					Log.d("FILERIDER", "starting reading file");
				}
			  
			  
				@Override
				protected Integer doInBackground(Void... params) {
					List<Word> words = readFile(filePath);
					if(words == null || words.size() == 0){
						removeCreatedLecture(lectureId, context);
					}
					WordDBAdapter wordDBAdapter = null;
					try{
						wordDBAdapter = new WordDBAdapter(context);
						wordDBAdapter.saveWordList(words);
					}catch(Exception e){
						removeCreatedLecture(lectureId, context);
						return -1;
					}finally{
						if(wordDBAdapter != null){
							wordDBAdapter.close();
						}
					}
					
					
					
					return words.size();
				}
				
				@Override
					protected void onPostExecute(Integer result) {
						String resultMessage;
						if(result == 0){
							resultMessage = getResources().getString( R.string.import_failed);
						}else{
							resultMessage = getResources().getString( R.string.import_success, result);
						}
						dialog.dismiss();
						showResultDialog(resultMessage);
					}
			}
	  
	  
			
			
	  private List<Word> readFile(String fileLocation){
			CSVReader reader = null;
			List<Word> words = new ArrayList<Word>();
			try {
				reader = new CSVReader( new FileReader(fileLocation) );

			    String[] nextLine;
			    while ((nextLine = reader.readNext()) != null) {
			    	
			        if(nextLine.length == 2){
			        	words.add(new Word(nextLine[0], nextLine[1], lectureId));
			        	Log.d("CSV",  nextLine[0] + " - " + nextLine[1]);
			        }
			    	
			    } 
			} catch (FileNotFoundException e) {
				Log.e("FILERIEDER", "CSV file not found", e);

			} catch (IOException e) {
				Log.e("FILERIEDER", "PARSER ERROR", e);
			}catch (Exception e) {
				Log.e("FILERIEDER", "Parsing error: ", e);
			}finally{
				try {
					if(reader != null){
						reader.close();
					}
				} catch (IOException e) {
					Log.e("FILERIEDER", "CAN NOT CLOSE FILE", e);
				}
			}
			  return words;
		  }
	  
	  
	  public void showResultDialog(String responseMsg){
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder
				.setTitle(R.string.import_status)
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
	  
	  private void initInputs(){
		  label = (TextView)findViewById(R.id.inportLectureLabel);
		  label.setVisibility(View.VISIBLE);
		  input = (EditText)findViewById(R.id.importLectureName);
		  input.setVisibility(View.VISIBLE);
	  }
	  
	  
}
