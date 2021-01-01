public class MyReentrantLock {
	private int count = 0;
	private Thread owner = null;
	
	public synchronized void lock() {
		if (Thread.currentThread() != owner) {
			while (count > 0)
				Util.justWait(this);
			owner = Thread.currentThread();
		}
		count++;
	}
	
	public synchronized void unlock() {
		if (Thread.currentThread() != owner)
			throw new RuntimeException(
					"Releasing an unowned lock");
		count--;
		if (count == 0)
			notify();
	}
}
