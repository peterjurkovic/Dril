package sk.peterjurkovic.dril.listener;

public interface OnEditWordListener {
	
	void saveEditedWord(long wordId, String question, String answer);

	
}
