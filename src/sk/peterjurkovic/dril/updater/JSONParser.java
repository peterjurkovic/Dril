package sk.peterjurkovic.dril.updater;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import sk.peterjurkovic.dril.model.Book;
import sk.peterjurkovic.dril.model.Language;
import sk.peterjurkovic.dril.model.Lecture;
import sk.peterjurkovic.dril.model.Word;
import sk.peterjurkovic.dril.utils.ConversionUtils;

public class JSONParser {
	
	
	public static final String TAG_BOOKS = "books";
	public static final String TAG_NAME = "name";
	public static final String TAG_BOOK_LANG_QUESTION = "lang_question";
	public static final String TAG_BOOK_LANG_ANSWER = "lang_answer";
	public static final String TAG_BOOK_SYNC = "sync";
	public static final String TAG_VERSION = "version";
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
	       	e.printStackTrace();
	       }
		return count;
	}
	
	
	public List<Book> parseBooks(final JSONObject json){
	   List<Book> bookList = null;
       try {
         bookList = parseBooksFromJSONArray( json.getJSONArray(TAG_BOOKS) );
       }catch(JSONException e){
       	e.printStackTrace();
       }		
       return bookList;
	}
	
	
	/**
	 * Parse books form json 
	 * 
	 * @param JSONARRAY root node of books
	 * @return List<Book> parsed books
	 * @throws JSONException if some error occurred
	 */
	public List<Book> parseBooksFromJSONArray(final JSONArray bookArray) throws JSONException{
		//Log.d("JSON", "books: "+ bookArray.length() );
		List<Book> bookList = new ArrayList<Book>();
		for(int i = 0; i < bookArray.length(); i++){
       		JSONObject b = bookArray.getJSONObject(i);
       		Book book = new Book();
       		book.setName( b.getString( TAG_NAME ));
       		book.setVersion( b.getInt( TAG_VERSION ));
       		book.setSync( ConversionUtils.intToBoolean( b.getInt( TAG_BOOK_SYNC )));
       		book.setQuestionLang( Language.getById( b.getInt( TAG_BOOK_LANG_QUESTION )));
       		book.setAnswerLang(Language.getById( b.getInt( TAG_BOOK_LANG_ANSWER)));
       		Log.i("BOOK", book.getName());
       		book.setLectures( parseLecturesFromJSONArray( b.getJSONArray(TAG_LECTURES) ));
       		bookList.add(book);
       	}
		return bookList;
	}
	
	
	/**
	 * Extract lecture data from current node in JSON data
	 * 
	 * @param JSONArray current book node
	 * @return List<Lecture> parsed lecture of current book
	 * @throws JSONException if some error occurred
	 */
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
 	
	
	/**
	 * Extract question and answer from given lecture node in JSON data.
	 * 
	 * @param JSONArray current lecture node
	 * @return List<Word> parse Words 
	 * @throws JSONException if some error occurred
	 */
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
	
	/**
	 * Extract question and answer from given lecture node in JSON data.
	 * 
	 * @param JSONArray current lecture node
	 * @param long id of lecture
	 * @return List<Word> parse Words 
	 * @throws JSONException if some error occurred
	 */
	public List<Word> parseWordsFromJSONArray(JSONArray wordArray, long lectureId) throws JSONException{
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
