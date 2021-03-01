package sk.peterjurkovic.dril.io;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import sk.peterjurkovic.dril.model.Word;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import sk.peterjurkovic.dril.utils.StringUtils;

/**
 * 
 * @author Peter Jurkovič
 * @date Nov 15, 2013
 *
 */
public class XlsStorageFileReader extends CsvStorageFileReader implements StorageFileReader {

	
	public XlsStorageFileReader(Context context){
		super(context);
	}
	
	
	
	@Override
	public List<Word> readFile(final String fileLocation, long lectureId) {
		Workbook workbook = parseFile(fileLocation);
		if(workbook != null){
			try{
			 return readWords(workbook, lectureId);
			}catch(Exception e){
				GoogleAnalyticsUtils.logException(e);
				Log.e(getClass().getSimpleName(), e.getMessage());
			}
		}
		return null;
	}

	
	
	
	private Workbook parseFile(final String fileLocation){
		try {
			File xlsFile = new File(fileLocation);
			if(xlsFile.isFile()){
				 WorkbookSettings workbookSettings = new WorkbookSettings();
				 workbookSettings.setEncoding( "Cp1252" );
				return Workbook.getWorkbook(xlsFile, workbookSettings);
			}
		} catch (Exception e) {
			GoogleAnalyticsUtils.logException(e);
			Log.e(getClass().getSimpleName(), e.getMessage());
		} 
		return null;
	}
	
	
	
	
	public List<Word> readWords(final Workbook workbook, final long lectureId){
		List<Word> list = new ArrayList<Word>();
		Sheet sheet = workbook.getSheet(0);
		
		if(sheet == null){
			return list;
		}
		
	    for (int i = 0; i < sheet.getRows(); i++) {
	    	Cell question = sheet.getCell(0, i);
	    	Cell answer = sheet.getCell(1, i);
	    	
	    	if(!StringUtils.isBlank(question.getContents()) &&
	    	   !StringUtils.isBlank(answer.getContents())){
	    		list.add(new Word(question.getContents(),answer.getContents(), lectureId));
	    	}
	     }
	     
		return list;
	}
}
