package sk.peterjurkovic.dril.dto;

import java.util.Comparator;

import sk.peterjurkovic.dril.model.Word;

public class WordWithPosition {
	
	private Word word;
	
	private int positin;

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

	public int getPositin() {
		return positin;
	}

	public void setPositin(int positin) {
		this.positin = positin;
	}
	
	
	public static class Comparators {

        public static Comparator<WordWithPosition> LAST_RATE = new Comparator<WordWithPosition>() {
            @Override
            public int compare(WordWithPosition o1, WordWithPosition o2) {
                return o1.getWord().getRate() - o2.getWord().getRate();
            }
        };
        
        public static Comparator<WordWithPosition> AVG_RATE = new Comparator<WordWithPosition>() {
            @Override
            public int compare(WordWithPosition o1, WordWithPosition o2) {
                return o1.getWord().getAvgRate() - o2.getWord().getAvgRate();
            }
        };
        
        public static Comparator<WordWithPosition> HARDEST = new Comparator<WordWithPosition>() {
            @Override
            public int compare(WordWithPosition o1, WordWithPosition o2) {
                return (o1.getWord().getHit() * o1.getWord().getAvgRate()) - (o2.getWord().getHit() * o2.getWord().getAvgRate());
            }
        };
       
    }
	
}
