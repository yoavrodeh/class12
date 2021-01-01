public class MyLockExample {
	private MyLock lock = new MyLock();
	private int counter = 0;

	private class Runner implements Runnable {
		public void run() {
			for (int i = 0; i < 1000; i++) {
				lock.lock();
				counter++;
				lock.unlock();
			}
		}
	}

	public void runExample() {
		Runner r = new Runner();
		Thread[] threads = new Thread[3];
		for (int i = 0; i < 3; i++) {
			threads[i] = new Thread(r);
			threads[i].start();
		}
		for (Thread t : threads)
			Util.justJoin(t);
		System.out.println(counter);
	}

	public static void main(String[] args) {
		new MyLockExample().runExample();
	}
}
