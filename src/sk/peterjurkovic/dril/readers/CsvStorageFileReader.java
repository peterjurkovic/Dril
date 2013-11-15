package sk.peterjurkovic.dril.readers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sk.peterjurkovic.dril.csv.CSVReader;
import sk.peterjurkovic.dril.model.Word;
import sk.peterjurkovic.dril.utils.StringUtils;
import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;

/**
 * 
 * @author Peter Jurkovič
 * @date Nov 15, 2013
 *
 */
public class CsvStorageFileReader implements StorageFileReader {

	private Context context;
	
	public CsvStorageFileReader(Context context){
		this.context = context;
	}
	
	@Override
	public List<Word> readFile(final String fileLocation, final long lectureId) {

		
		CSVReader reader = null;
		List<Word> words = new ArrayList<Word>();
		
		if(StringUtils.isBlank(fileLocation)){
			return words;
		}
		try {
			File file = new File(fileLocation);
			if(file.isFile()){
				reader = new CSVReader( new FileReader( file ) );
			    String[] nextLine;
			    while ((nextLine = reader.readNext()) != null) {
			    	
			        if(nextLine.length == 2){
			        	words.add(new Word(nextLine[0], nextLine[1], lectureId));
			        }
			    	
			    } 
			}
		} catch (FileNotFoundException e) {
			logException(e);
		} catch (IOException e) {
			logException(e);
		}catch (Exception e) {
			logException(e);
		}finally{
			try {
				if(reader != null){
					reader.close();
				}
			} catch (IOException e) {
				logException(e);
			}
		}
		  return words;
	}
	
	
	
	
	protected void logException(Exception e){
		 EasyTracker easyTracker = EasyTracker.getInstance(context);
		  easyTracker.send(MapBuilder
			      .createException(new StandardExceptionParser(context, null)             
			      .getDescription(Thread.currentThread().getName(),  e),  false)                                              
			      .build()
			    );

	}

}
