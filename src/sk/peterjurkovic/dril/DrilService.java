package sk.peterjurkovic.dril;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sk.peterjurkovic.dril.dao.WordDao;
import sk.peterjurkovic.dril.dao.WordDaoImpl;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.model.Statistics;
import sk.peterjurkovic.dril.model.Word;
import sk.peterjurkovic.dril.utils.NumberUtils;
import android.util.Log;

public class DrilService {

	private final static int WORD_THRESHOLD = 5;
	private final static int WORDS_HITS_THRESHOLD = 8;
	
	
	private int position = 0;
	private int hits = 0;
	private WordDao wordDao;
	private Statistics statistics;
	private List<Word> activatedWords = new ArrayList<Word>();
	
	
	public DrilService(WordDBAdapter wordDbAdapter){
		if(wordDbAdapter == null){
			throw new Error("Word db adapter can not be null");
		}
		this.wordDao = new WordDaoImpl(wordDbAdapter);
		loadActivatedWords();
	}
	
	private void loadActivatedWords(){ 
  	    try{
  	    	activatedWords = wordDao.getActivatedWords();
  	    } catch (Exception e) {
  			Log.e( getClass().getName() , "ERROR: " + e.getMessage());
  		}
	}
	
	public void precessRating(final int rating){
		Word word = getCurrentWord();
		if(word != null){
			word.updateAvgRate(rating);
			word.setRate(rating);
			word.increaseHit();
			if(rating == 1){
				word.setActive(Boolean.FALSE);
				activatedWords.remove(position);
				if( WORD_THRESHOLD >= activatedWords.size() && position != 0){
					position--;
				}
			}
			Log.d("RATE", word.toString());
			updateRatedWord(word);
		}
	}
	
	private  void updateRatedWord(final Word word){
		Thread thread = new Thread()
    	{
    	    @Override
    	    public void run() {
    	    	wordDao.updateReatedWord(word, statistics);
    	    }
    	};
    	thread.start();    
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
	  if(hits % 4 == 0){
		  Collections.sort(clonedList, Word.Comparators.AVG_RATE);
		  position =  getRandomPosition(clonedList);
	  }else if(hits % 5 == 0){
		  Collections.sort(clonedList, Word.Comparators.HARDEST);
		  position =  getRandomPosition(clonedList);
	  }else{
		  position = getRandomPosition();
	  }
		 	  
	}

	
	private int getRandomPosition(List<Word> collection){
		if(!collection.isEmpty()){
			final int max = collection.size() - 1;
			int min = max - 3;
			if(min > 0){
				min = 0;
			}
			Word word = collection.get( NumberUtils.randInt(min, max) );
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
    	return  NumberUtils.getRandomPostion(activatedWords.size());
    }
	
	
	
	
	private int findPositionByWordId(final long id){
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
	
	public int getCountOfWords(){
		return activatedWords.size();
	}

	public Statistics getStatistics() {
		return statistics;
	}

	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}

	

}
;