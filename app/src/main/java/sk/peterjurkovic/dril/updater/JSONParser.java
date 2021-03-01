package sk.peterjurkovic.dril.updater;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sk.peterjurkovic.dril.model.Book;
import sk.peterjurkovic.dril.model.Language;
import sk.peterjurkovic.dril.model.Lecture;
import sk.peterjurkovic.dril.model.Word;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;

public class JSONParser {
	
	
	public static final String TAG_BOOKS = "books";
	public static final String TAG_NAME = "name";
	public static final String TAG_BOOK_LANG_QUESTION = "lang_question";
	public static final String TAG_BOOK_LANG_ANSWER = "lang_answer";
	public static final String TAG_LECTURES = "lectures";
	public static final String TAG_LECTURE_NAME = "lecture_name";
	public static final String TAG_WORDS = "words";
	public static final String TAG_QUESTION = "q";
	public static final String TAG_ANSWER = "a";
	public static final String TAG_COUNT = "count";
	
	
	public int getCountOfNewBooks(JSONObject json){
		int count = 0;
		try {
			count = json.getInt(TAG_COUNT);
		}catch(JSONException e){
			Log.e(getClass().getName(), e.getMessage());
			GoogleAnalyticsUtils.logException(e);
		}
		return count;
	}
	
	
	public List<Book> parseBooks(final JSONObject json){
	   List<Book> bookList = null;
	   if(json != null){
	       try {
	         bookList = parseBooksFromJSONArray( json.getJSONArray(TAG_BOOKS) );
	       }catch(JSONException e){
			   Log.e(getClass().getName(), e.getMessage());
			   GoogleAnalyticsUtils.logException(e);
	       }		
	   }
       return bookList;
	}
	

	public List<Book> parseBooksFromJSONArray(final JSONArray bookArray) throws JSONException{
		//Log.d("JSON", "books: "+ bookArray.length() );
		List<Book> bookList = new ArrayList<Book>();
		for(int i = 0; i < bookArray.length(); i++){
       		JSONObject b = bookArray.getJSONObject(i);
       		Book book = new Book();
       		book.setName( b.getString( TAG_NAME ));
       		book.setQuestionLang( Language.getById( b.getInt( TAG_BOOK_LANG_QUESTION )));
       		book.setAnswerLang(Language.getById( b.getInt( TAG_BOOK_LANG_ANSWER)));
       		book.setLectures( parseLecturesFromJSONArray( b.getJSONArray(TAG_LECTURES) ));
       		bookList.add(book);
       	}
		return bookList;
	}
	

	public List<Lecture> parseLecturesFromJSONArray(final JSONArray lectureArray) throws JSONException{
		//Log.d("JSON", "lectures: "+ lectureArray.length() );
		List<Lecture> lectureList = new ArrayList<Lecture>();
		for(int i = 0; i < lectureArray.length(); i++){
   			JSONObject l = lectureArray.getJSONObject(i);	
   			Lecture lecture = new Lecture();
   			lecture.setName(l.getString(TAG_LECTURE_NAME));
   			lecture.setWords(parseWordsFromJSONArray( l.getJSONArray(TAG_WORDS) ));
   			lectureList.add(lecture);
   		}
		return lectureList;
	}
 	

	public List<Word> parseWordsFromJSONArray(final  JSONArray wordArray) throws JSONException{
		List<Word> wordList = new ArrayList<Word>();
		for(int i = 0; i < wordArray.length(); i++){
				JSONObject w = wordArray.getJSONObject(i);
				wordList.add(
						new Word(
								w.getString(TAG_QUESTION),
								w.getString(TAG_ANSWER))
					);
			}
		return wordList;
	}

	public List<Word> parseWordsFromJSONArray(JSONArray wordArray,final long lectureId) throws JSONException{
		List<Word> wordList = new ArrayList<Word>();
		for(int i = 0; i < wordArray.length(); i++){
				JSONObject w = wordArray.getJSONObject(i);
				wordList.add(
						new Word(
								w.getString(TAG_QUESTION),
								w.getString(TAG_ANSWER),
								lectureId)
					);
			}
		return wordList;
	}
	
}
