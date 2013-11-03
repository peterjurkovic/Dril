package sk.peterjurkovic.dril.dao;

import java.util.ArrayList;
import java.util.List;

import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.model.Word;
import sk.peterjurkovic.dril.utils.ConversionUtils;
import android.database.Cursor;
import android.util.Log;

public class WordDaoImpl implements WordDao {
	
	private WordDBAdapter wordDBAdapter;
	
	public WordDaoImpl(){}
	
	public WordDaoImpl(WordDBAdapter wordDBAdapter){
		this.wordDBAdapter = wordDBAdapter;
	}
	
	@Override
	public List<Word> getActivatedWords() {
		List<Word> wordList = new ArrayList<Word>();
		if(wordDBAdapter == null){
			return wordList;
		}
		Cursor cursor =  wordDBAdapter.getActivatedWords();
		if(cursor != null){
			cursor.moveToFirst();
	    	while(!cursor.isAfterLast()) {
	    		wordList.add(
	    	    	new Word( 
	    				 cursor.getLong(cursor.getColumnIndex(WordDBAdapter.WORD_ID )), 
	    				 cursor.getString(cursor.getColumnIndex( WordDBAdapter.QUESTION )), 
	    				 cursor.getString(cursor.getColumnIndex( WordDBAdapter.ANSWER )), 
	    				 cursor.getInt(cursor.getColumnIndex( WordDBAdapter.HIT )), 
	    				 cursor.getInt(cursor.getColumnIndex( WordDBAdapter.LAST_RATE )),
	    				 ConversionUtils.intToBoolean(cursor.getInt(cursor.getColumnIndex( WordDBAdapter.ACTIVE ))),
	    				 cursor.getInt(cursor.getColumnIndex( WordDBAdapter.HIT )), 
	    				 cursor.getInt(cursor.getColumnIndex( WordDBAdapter.LAST_RATE ))
	    			)); 
	    	     cursor.moveToNext();
	    	}
	    	if(cursor != null){
	    		cursor.close();
	    	}
	    	wordDBAdapter.close();
		}
		return wordList;
	}

	
	@Override
	public void updateReatedWord(Word word) {
		try{
			wordDBAdapter.updateReatedWord(word);
  	    } catch (Exception e) {
  			Log.d( getClass().getName() , "ERROR: " + e.getMessage());
  		} finally {
  			wordDBAdapter.close();
  		}
	}

}
