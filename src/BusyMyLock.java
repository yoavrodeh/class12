public class BusyMyLock {
	private boolean locked = false;
	
	public void lock() {
		while (locked);
		locked = true;
	}
	
	public void unlock() {
		locked = false;
	}
}
