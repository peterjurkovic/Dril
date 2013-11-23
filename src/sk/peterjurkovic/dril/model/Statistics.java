package sk.peterjurkovic.dril.model;

/**
 * 
 * @author Peter Jurkovič
 * @date Nov 15, 2013
 *
 */
public class Statistics extends AbstractEntity{
	
	private int hits = 0;
	private double avgSessionRate = 0;
	private double avgGlobalRate = 0;
	private int learnedCards = 0;
	private int sumOfRate = 0;
	private boolean finished;
	
	public Statistics(){
		setChanged(System.currentTimeMillis());
		setCreated(System.currentTimeMillis());
	}
	
	public int getHits() {
		return hits;
	}
	public void setHits(int hits) {
		this.hits = hits;
	}
	public double getAvgSessionRate() {
		return avgSessionRate;
	}
	public void setAvgSessionRate(double avgSessionRate) {
		this.avgSessionRate = avgSessionRate;
	}
	public double getAvgGlobalRate() {
		return avgGlobalRate;
	}
	public void setAvgGlobalRate(double avgGlobalRate) {
		this.avgGlobalRate = avgGlobalRate;
	}
	public int getLearnedCards() {
		return learnedCards;
	}
	public void setLearnedCards(int learnedCards) {
		this.learnedCards = learnedCards;
	}
	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public int getSumOfRate() {
		return sumOfRate;
	}

	public void setSumOfRate(int sumOfRate) {
		this.sumOfRate = sumOfRate;
	}

	public synchronized void incrementHit(int rate){
		this.hits++;
		sumOfRate = sumOfRate + rate;
		if(rate == 1){
			learnedCards++;
		}
	}

	@Override
	public String toString() {
		return "Statistics [id="+getId()+", hits=" + hits + ", avgSessionRate="
				+ avgSessionRate + ", avgGlobalRate=" + avgGlobalRate
				+ ", learnedCards=" + learnedCards + ", sumOfRate=" + sumOfRate
				+ "]";
	}
	
	
}
