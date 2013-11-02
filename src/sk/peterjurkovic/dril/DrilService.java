package sk.peterjurkovic.dril;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.model.Word;
import android.util.Log;

public class DrilService {

	private final static int WORD_THRESHOLD = 5;
	private final static int WORDS_HITS_THRESHOLD = 8;
	private final static Pattern clean = Pattern.compile("(\\s(n|v|adj|adv|st|conj)(\\s)?)|(\\s(\\(n\\)|\\(v\\)|\\(adj\\)|\\(adv\\)|\\(conj\\))(\\s)?)|(\\[.*\\])");
	
	private int position = 0;
	private int hits = 0;
	private WordDBAdapter wordDbAdapter;
	private List<Word> activatedWords = new ArrayList<Word>();
	
	
	public DrilService(WordDBAdapter wordDbAdapter){
		if(wordDbAdapter == null){
			throw new Error("Word db adapter can not be null");
		}
		this.wordDbAdapter = wordDbAdapter;
		loadActivatedWords();
	}
	
	private void loadActivatedWords(){ 
		if(wordDbAdapter != null){
	  	    try{
	  	    	activatedWords = wordDbAdapter.getActivatedWords();
	  	    } catch (Exception e) {
	  			Log.e( getClass().getName() , "ERROR: " + e.getMessage());
	  		} finally {
	  			wordDbAdapter.close();
	  		}
		}
	}
	
	public void precessRating(int rate){
		Word word = getCurrentWord();
		if(word != null){
			word.updateAvgRate(rate);
			word.setRate(rate);
			word.increaseHit();
			if(rate == 1){
				word.setActive(Boolean.FALSE);
				activatedWords.remove(position);
			}
			Log.d("RATE", word.toString());
			updateRatedWord(word);
		}
	}
	
	private void updateRatedWord(Word word){
  	    try{
  	    	wordDbAdapter.updateReatedWord(word);
  	    } catch (Exception e) {
  			Log.d( getClass().getName() , "ERROR: " + e.getMessage());
  		} finally {
  			wordDbAdapter.close();
  		}
    }
	
	
	public boolean hasNext(){
		return activatedWords != null && activatedWords.size() > 0;
	}
	
	public Word getNext(){
		int listSize = activatedWords.size();
		if(listSize == 0){
			return null;
		}
		if(listSize <= WORD_THRESHOLD || WORDS_HITS_THRESHOLD > getSumOfHits()){
			nextPostion();
		}else{
			selectRandomPostion();
		}
		hits++;
		return activatedWords.get(position);
	}
	
	private void nextPostion(){
    	if((position + 1) == activatedWords.size()){ 
    		position = 0;
    	}else{
    		position++;
    	}
    }
	
	
	private void selectRandomPostion(){
	  int listSize = activatedWords.size();
	  if(listSize == 0){
		  return;
	  }
	  
	  List<Word> clonedList = new ArrayList<Word>(activatedWords);
	  if(hits % 6 == 0){
		  Collections.sort(clonedList, Word.Comparators.AVG_RATE);
		  position =  getRandomPosition(clonedList);
	  }else if(hits % 7 == 0){
		  Collections.sort(clonedList, Word.Comparators.HARDEST);
		  position =  getRandomPosition(clonedList);
	  }else{
		  position = getRandomPosition();
	  }
		  
	  
	}

	private int getRandomPosition(List<Word> collection){
		if(!collection.isEmpty()){
			int size = collection.size();
			if(size > 5){
				size = 5;
			}
			int randIndex = getRandomPostion(size);
			Word word = collection.get(randIndex);
			return findPositionByWordId(word.getId());
			
		}
		return 0;
	}
	
	private int getRandomPosition(){	
    	List<Integer> randWords = new ArrayList<Integer>();	
    		while(randWords.size() < 2 ){
    			int pos = getRandomPostion();
    			if(pos > (position +1) || pos < (position - 1))
    				randWords.add(pos);
    		}
    		
    		Word word0 = activatedWords.get(randWords.get(0));
    		if(word0.getRate() == 0){
    			return randWords.get(0);
    		}else{
    			Word word1 = activatedWords.get(randWords.get(1));
    			if(word1.getRate() == 0){
    				return randWords.get(1);
    			}else{
    				return( word0.getRate() > word1.getRate() ? randWords.get(0) : randWords.get(1));
    			}
    		}
    }
	
	
	private int getRandomPostion(){
    	return getRandomPostion(activatedWords.size());
    }
	
	private int getRandomPostion(int limit){
    	Random r = new Random();
    	return r.nextInt( limit );
    }
	
	
	private int findPositionByWordId(long id){
		for(int i = 0; i < activatedWords.size();i++){
			if(activatedWords.get(i).getId() == id){
				return i;
			}
		}
		return -1;
	}
	
	private int getSumOfHits(){
		int sum = 0;
		for(Word w : activatedWords){
			sum += w.getHit();
		}
		return sum;
	}
	
	public Word getCurrentWord(){
		if(activatedWords.size() > 0){
			return activatedWords.get(position);
		}
		return null;
	}
}
;