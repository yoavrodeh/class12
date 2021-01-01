import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorExample {
	
	private static class Runner implements Runnable {
		@Override
		public void run() {
			Util.printMsg("Hi");
		}
	}
	
	public static void main(String[] args) {
		ExecutorService e = 
				Executors.newFixedThreadPool(10);
		e.execute(new Runner());
		e.execute(new Runner());
		e.shutdown();
		try {
			e.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e1) {}
		if (e.isTerminated())
			System.out.println("All threads are done.");
		else
			System.out.println("Tired of waiting.");
	}
}
