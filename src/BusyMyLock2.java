public class BusyMyLock2 {
	private boolean locked = false;
	
	public void lock() {
		while (true)
			synchronized(this) {
				if (!locked) {
					locked = true;
					return;
				}
			}
	}
	
	public void unlock() {
		locked = false;
	}
}
