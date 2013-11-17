package sk.peterjurkovic.dril.dao;

import java.util.List;

import sk.peterjurkovic.dril.model.Statistics;
import sk.peterjurkovic.dril.model.Word;

public interface WordDao {
	
	List<Word> getActivatedWords();
	
	void updateReatedWord(Word word, Statistics statistics);
}
