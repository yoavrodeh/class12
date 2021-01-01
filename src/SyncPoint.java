public class SyncPoint {
	private int count;

	public SyncPoint(int numThreads) {
		count = numThreads;
	}
	public synchronized void waitForEveryone() {
		if (--count == 0)
			notifyAll();
		else
			while (count > 0)
				Util.justWait(this);
	}
}
