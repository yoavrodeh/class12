public class BusySyncPoint {
	private int count;

	public BusySyncPoint(int numThreads) {
		count = numThreads;
	}
	
	public void waitForEveryone() {
		count--;
		while (count > 0);
	}
}
