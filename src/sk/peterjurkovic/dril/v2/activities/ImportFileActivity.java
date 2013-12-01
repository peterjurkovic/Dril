package sk.peterjurkovic.dril.v2.activities;

import java.util.List;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.LectureDBAdapter;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.io.CsvStorageFileReader;
import sk.peterjurkovic.dril.io.StorageFileReader;
import sk.peterjurkovic.dril.io.XlsStorageFileReader;
import sk.peterjurkovic.dril.model.Word;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
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
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 
 * 
 * @author Peter Jurkovič
 * @date Nov 24, 2013
 *
 */
public class ImportFileActivity extends BaseActivity {
	
	private final static String CREATE_LECTURE_KEY = "importCreateLecture";
	private final static String BOOK_ID_KEY = "importBookId";
	private final static String LECTURE_ID_KEY = "importLectureId";
	private final static String IS_CSV_KEY = "importIsCsv";

	
	private static final int ACTIVITY_CHOOSE_FILE = 1;
	  
	private long bookId = 0;
	private long lectureId = 0;
	private boolean createLecture = true;
	private boolean isCsvImport = true;
	
	private TextView label = null;
	private EditText input = null;
	private Context context;
	private StorageFileReader storageFileReader;
	

	
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.v2_import_file_activity);
	    context = this;
	    
	    if(savedInstanceState != null){
	    	restoreDate(savedInstanceState);
	    }else{
		    Intent i = getIntent();
		    createLecture = i.getBooleanExtra(ImportMenuActivity.EXTRA_CREATE_LECTURE, false);
		    isCsvImport = i.getBooleanExtra(ImportMenuActivity.EXTRA_IS_CSV, true);
		    
		    if(createLecture){
			   bookId = i.getLongExtra(ImportMenuActivity.EXTRA_ID, 0);
			}else{
			   lectureId = i.getLongExtra(ImportMenuActivity.EXTRA_ID, 0);
			}
	    }
	    
	    if(createLecture){
	    	initInputs();
	    }
	    
	    if(isCsvImport){
	    	setVisibleCsvInfo();
	    }else{
	    	setVisibleXlsInfo();
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
				Toast.makeText(context, R.string.error_blank_lecture_name, Toast.LENGTH_LONG).show();
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
	  
	private void restoreDate(Bundle instanceState){  
		createLecture = instanceState.getBoolean(CREATE_LECTURE_KEY);
		bookId = instanceState.getLong(BOOK_ID_KEY);
		lectureId = instanceState.getLong(LECTURE_ID_KEY);
		isCsvImport = instanceState.getBoolean(IS_CSV_KEY);
	}
	
	private void backupData(Bundle outState){
		outState.putBoolean(CREATE_LECTURE_KEY, createLecture);
		outState.putLong(BOOK_ID_KEY, bookId);
		outState.putLong(LECTURE_ID_KEY, lectureId);
		outState.putBoolean(IS_CSV_KEY, isCsvImport);
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
	          final String filePath = uri.getPath();
	          if(isFileValid(filePath)){
	        	  new ImportData(filePath, this).execute();
	          }else{
	        	  Toast.makeText(this, R.string.error_invalid_file, Toast.LENGTH_LONG).show();
	          }
	        }
	      }
	    }
	  }
	  
	  
	  private boolean isFileValid(final String filePath){
		  final String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
		  if(isCsvImport){
			  return extension.equals("csv");
		  }
		  return extension.equals("xls");
	  }
	  
	
	  
	  private class ImportData extends AsyncTask<Void, Void, Integer>{
			  
		
			  private ProgressDialog dialog;
			  private String filePath;
			  private Context context;
			  
			  public ImportData(final String filePath, Context context){
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
					List<Word> words = null;
					
					if(isCsvImport){
						storageFileReader = new CsvStorageFileReader(context);
					}else{
						storageFileReader = new XlsStorageFileReader(context);
					}
					words = storageFileReader.readFile(filePath, lectureId);
					if(words == null || words.size() == 0){
						removeCreatedLecture(lectureId, context);
						return 0;
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
						if(result < 1){
							resultMessage = getResources().getString( R.string.import_failed);
						}else{
							resultMessage = getResources().getString( R.string.import_success, result);
						}
						logResult(result);
						dialog.dismiss();
						showResultDialog(resultMessage, result);
					}
			}
	  
	  
			
			
	 
	  
	  
	  public void showResultDialog(final String responseMsg, final int result){
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
	  
	  private void initInputs(){
		  label = (TextView)findViewById(R.id.inportLectureLabel);
		  label.setVisibility(View.VISIBLE);
		  input = (EditText)findViewById(R.id.importLectureName);
		  input.setVisibility(View.VISIBLE);
	  }
	  
	  
	  private void gotIntoLecture(){
		  Intent i = new Intent(this,  WordActivity.class);
		  i.putExtra( WordActivity.LECTURE_ID_EXTRA, lectureId);
		  startActivity(i);
	  }
	  
	  private void setVisibleXlsInfo(){
		  findViewById(R.id.xlsImportHelp).setVisibility(View.VISIBLE);
	  }
	  
	  private void setVisibleCsvInfo(){
		  findViewById(R.id.csvImportHelp).setVisibility(View.VISIBLE);
	  }
	  
	
	  @Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		backupData(outState);
	}
	  
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		restoreDate(savedInstanceState);
	}
	
	private void logResult(final Integer result){
		GoogleAnalyticsUtils.logAction(this, 
				GoogleAnalyticsUtils.CATEGORY_PROCESSING_ACTION,
				GoogleAnalyticsUtils.ACTION_RESULT,
				getImportType(), 
				Long.valueOf(result));
	}
	
	private String getImportType(){
		return isCsvImport ? "csv" : "xls";
	}
	  
}
