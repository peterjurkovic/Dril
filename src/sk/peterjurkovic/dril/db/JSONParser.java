package sk.peterjurkovic.dril.db;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import sk.peterjurkovic.dril.model.Book;
import sk.peterjurkovic.dril.model.Lecture;
import sk.peterjurkovic.dril.model.Word;

public class JSONParser {
	
	
	private static final String TAG_BOOKS = "books";
	private static final String TAG_NAME = "name";
	private static final String TAG_VERSION = "version";
	private static final String TAG_LECTURES = "lectures";
	private static final String TAG_LECTURE_NAME = "lecture_name";
	private static final String TAG_WORDS = "words";
	private static final String TAG_QUESTION = "question";
	private static final String TAG_ANSWER = "answer";
	
	
	
	public List<Book> parseBooks(){
		
		JSONReciever dataReciever = new JSONReciever();
		List<Book> bookList = null;
		JSONObject json = dataReciever.getJSONData();
       
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
	private List<Book> parseBooksFromJSONArray(JSONArray bookArray) throws JSONException{
		Log.d("JSON", "books: "+ bookArray.length() );
		List<Book> bookList = new ArrayList<Book>();
		for(int i = 0; i < bookArray.length(); i++){
       		JSONObject b = bookArray.getJSONObject(i);
       		Book book = new Book();
       		book.setName( b.getString( TAG_NAME ));
       		book.setVersion( b.getInt( TAG_VERSION ));
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
	private List<Lecture> parseLecturesFromJSONArray(JSONArray lectureArray) throws JSONException{
		Log.d("JSON", "lectures: "+ lectureArray.length() );
		List<Lecture> lectureList = new ArrayList<Lecture>();
		for(int i = 0; i < lectureArray.length(); i++){
   			JSONObject l = lectureArray.getJSONObject(i);
   			Lecture lecture = new Lecture();
   			lecture.setLectureName(l.getString(TAG_LECTURE_NAME));
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
	private List<Word> parseWordsFromJSONArray(JSONArray wordArray) throws JSONException{
		Log.d("JSON", "words: "+ wordArray.length() );
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
}
