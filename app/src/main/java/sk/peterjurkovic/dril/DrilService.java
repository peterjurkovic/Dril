package sk.peterjurkovic.dril;

//import com.google.analytics.tracking.android.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import sk.peterjurkovic.dril.dao.WordDao;
import sk.peterjurkovic.dril.dao.WordDaoImpl;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.dto.WordWithPosition;
import sk.peterjurkovic.dril.exceptions.DrilUnexpectedFinishedException;
import sk.peterjurkovic.dril.model.Statistics;
import sk.peterjurkovic.dril.model.Word;
import sk.peterjurkovic.dril.utils.NumberUtils;

public class DrilService {

	private final static int WORD_THRESHOLD = 7;
	private final static int WORDS_HITS_THRESHOLD = 8;
	private final int HISTORY_SIZE = 3;
	
	private int position = 0;
	private int hits = 0;
	private WordDao wordDao;
	private Statistics statistics;
	private final List<Word> activatedWords = new LinkedList<Word>();
	private final Deque<Integer> history = new ArrayDeque<Integer>(HISTORY_SIZE);
	
	
	public DrilService(WordDBAdapter wordDbAdapter){
		if(wordDbAdapter == null){
			throw new Error("Word db adapter can not be null");
		}
		this.wordDao = new WordDaoImpl(wordDbAdapter);
		loadActivatedWords();
	}
	
	private void loadActivatedWords(){ 
  	    try{
  	    	history.clear();
  	    	activatedWords.addAll( wordDao.getActivatedWords());
  	    } catch (Exception e) {
//  	    	 Log.e(e);
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
		return activatedWords != null && !activatedWords.isEmpty();
	}
	
	public Word getNext() throws DrilUnexpectedFinishedException {
		int listSize = activatedWords.size();
		if(listSize == 0){
			return null;
		}
		if(listSize <= WORD_THRESHOLD || WORDS_HITS_THRESHOLD > getSumOfHits()){
			nextPostion();
		}else{
			selectAppropriatePosition();
		}
		
		if(position >= activatedWords.size()){
//			Log.e("IndexOutOfBoundsException catched. Position: " + position + " countOfWords: " + activatedWords.size());
			position = 0;
		}
		
		updateHistory();
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
	
	
	private void selectAppropriatePosition() throws DrilUnexpectedFinishedException {
		if(hits % 10 == 0){
			selectHardestWord();
		}else{
			selectAppropriatePosition(seletRandomPositions());
		} 
    }
	
	private Set<Integer> seletRandomPositions(){
		final Set<Integer> randWords = new HashSet<Integer>(3);	
		int size = activatedWords.size() - 1;
		int i = 0;
		do{
			if(i > 9){
				break;
			}
			int pos = NumberUtils.randInt(0, size);
			if(!isInHistory(pos)){
				randWords.add(pos);
			}
			i++;
		}while(randWords.size() < 3);
		return randWords;
	}
	
	private void selectAppropriatePosition(final Set<Integer> postions) throws DrilUnexpectedFinishedException {
		//Log.i(TAG, "selectAppropriatePosition ..");
		List<WordWithPosition> words = new ArrayList<WordWithPosition>();
		for(Integer pos : postions){
			WordWithPosition w = new WordWithPosition();
			w.setPositin(pos);
			w.setWord(activatedWords.get(pos));
			words.add(w);
		}
		if(words.size() == 0){
			if(activatedWords.size() == 0){
				throw new DrilUnexpectedFinishedException("Dril unexpected at selectAppropriatePosition");
			}else{
			this.position = 0;
			}
			return ;
		}
		
		if(System.currentTimeMillis() % 4 == 0){
			Collections.sort(words, WordWithPosition.Comparators.LAST_RATE);
		}
		this.position =  words.get(words.size() - 1).getPositin();
	}
	
	
	
	private void selectHardestWord() throws DrilUnexpectedFinishedException {
		final List<WordWithPosition> wordPositionList = cloneList();
		Collections.sort(wordPositionList, WordWithPosition.Comparators.LAST_RATE);
		
		if(wordPositionList.size() == 0){
			selectAppropriatePosition(seletRandomPositions());
			return;
		}
		
		for(int i = wordPositionList.size() -1 ; i > -1; i--){
			WordWithPosition withPosition = wordPositionList.get(i);
			int pos = withPosition.getPositin();
			if(!isInHistory(pos)){
				this.position = pos;
				return;
			}
		}
		
	
		if(position >= this.activatedWords.size()){
			throw new DrilUnexpectedFinishedException("Position \"" + position + "\" is out of range in selectHardestWord");
		}
		
		this.position = 0;
	}
	
	
	
	private List<WordWithPosition> cloneList(){
		List<WordWithPosition> wordPositionList = new ArrayList<WordWithPosition>(activatedWords.size());
		for(int pos = 0; pos < activatedWords.size(); pos++ ){
			if(activatedWords.get(pos).getHit() > 0){
				WordWithPosition wp = new WordWithPosition();
				wp.setPositin(pos);
				wp.setWord(activatedWords.get(pos));
				wordPositionList.add(wp);
			}
		}
		return wordPositionList;
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
	
	private void updateHistory(){
		if(history.size() == HISTORY_SIZE){
			history.removeLast();
		}
		history.push(position);
	}
	
	private boolean isInHistory(final Integer pos){
		return history.contains(pos);
	}

}
;