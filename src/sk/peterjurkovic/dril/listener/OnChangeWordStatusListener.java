package sk.peterjurkovic.dril.listener;

public interface OnChangeWordStatusListener {
	
	void activeWord(long wordId);

	void deactiveWord(long wordId);
}
