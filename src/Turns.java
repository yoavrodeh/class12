import java.util.ArrayList;
import java.util.List;

public class Turns {
	List<Thread> threads = new ArrayList<>();
	int current = 0;
	
	public synchronized void add() {
		threads.add(Thread.currentThread());
	}
	
	private boolean myTurn() {
		return threads.get(current) == 
				Thread.currentThread();
	}
	
	public synchronized void waitForTurn() {
		while (!myTurn())
			Util.justWait(this);			
	}
	
	public synchronized void done() {
		if (!myTurn())
			throw new RuntimeException();
		current++;
		if (current == threads.size())
			current = 0;
		notifyAll();
	}
}
