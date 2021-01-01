public class SyncPointExample implements Runnable {
	static SyncPoint syncPoint;
	
	public void run() {
		Util.printMsg("before");
		syncPoint.waitForEveryone();
		Util.printMsg("after");
	}
	
	public static void main(String[] args) {
		syncPoint = new SyncPoint(5);
		for (int i = 0; i < 5; i++)
			new Thread(new SyncPointExample()).start();
	}
}
