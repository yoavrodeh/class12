public class MyLock {
	private boolean locked = false;
	
	public synchronized void lock() {
		while (locked)
			Util.justWait(this);
		locked = true;
	}
	
	public synchronized void unlock() {
		locked = false;
		notify();
	}
}
