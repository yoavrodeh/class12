public class TurnsExample implements Runnable {
	private static Turns turns = new Turns();

	public void run() {
		turns.add();
		Util.justSleep(1000);
		for (int i = 0; i < 3; i++) {
			turns.waitForTurn();
			Util.printMsg("Hi " + i);
			turns.done();
		}
	}
	
	public static void main(String[] args) {
		new Thread(new TurnsExample()).start();
		new Thread(new TurnsExample()).start();
		new Thread(new TurnsExample()).start();
	}
	
//	Thread-1 : Hi 0
//	Thread-0 : Hi 0
//	Thread-2 : Hi 0
//	Thread-1 : Hi 1
//	Thread-0 : Hi 1
//	Thread-2 : Hi 1
//	Thread-1 : Hi 2
//	Thread-0 : Hi 2
//	Thread-2 : Hi 2
}
