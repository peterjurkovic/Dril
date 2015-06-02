package sk.peterjurkovic.dril.sync.requests;

import java.util.ArrayList;
import java.util.List;

public class SyncRequest {
	
	private String deviceId;
	private String serverLastSync;
	private String clientLastSync;
	
	private List<SyncWord> wordList;
	private List<SyncLecture> lectureList;
	private List<SyncBook> bookList;
	private List<SyncDeleted> deletedList = new ArrayList<SyncDeleted>();
	
	public List<SyncWord> getWordList() {
		return wordList;
	}
	public void setWordList(List<SyncWord> wordList) {
		this.wordList = wordList;
	}
	public List<SyncLecture> getLectureList() {
		return lectureList;
	}
	public void setLectureList(List<SyncLecture> lectureList) {
		this.lectureList = lectureList;
	}
	public List<SyncBook> getBookList() {
		return bookList;
	}
	public void setBookList(List<SyncBook> bookList) {
		this.bookList = bookList;
	}
	public List<SyncDeleted> getDeletedList() {
		return deletedList;
	}
	public void setDeletedList(List<SyncDeleted> deletedList) {
		this.deletedList = deletedList;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getServerLastSync() {
		return serverLastSync;
	}
	public void setServerLastSync(String serverLastSync) {
		this.serverLastSync = serverLastSync;
	}
	public String getClientLastSync() {
		return clientLastSync;
	}
	public void setClientLastSync(String clientLastSync) {
		this.clientLastSync = clientLastSync;
	}
	
	
	
	
	
}
