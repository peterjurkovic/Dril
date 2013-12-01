package sk.peterjurkovic.dril.io;

import java.util.List;

import sk.peterjurkovic.dril.model.Word;

public interface StorageFileReader {
	
	List<Word> readFile(String fileLocation, long lectureId);
	
}
