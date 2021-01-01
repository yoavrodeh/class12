import java.util.Random;

public class Util {
	static Random rand = new Random();

	public static void justSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	public static void randSleep(int atMostMillis) {
		justSleep(rand.nextInt(atMostMillis));
	}

	public static void justWait(Object monitor) {
		try {
			monitor.wait();
		} catch (InterruptedException e) {
		}
	}

	public static void justJoin(Thread t) {
		while (t.isAlive()) {
			try {
				t.join();
			} catch (InterruptedException e) {}
		}
	}

	public static void printMsg(String msg) {
		System.out.println(
				Thread.currentThread().getName()
				+ " : " + msg);
	}
}
