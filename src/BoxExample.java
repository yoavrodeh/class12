public class BoxExample {
	private Box<Integer> box = new Box<>();
	
	private class Producer implements Runnable {
		public void run() {
			for (int i = 0; i < 3; i++) { 
				box.put(i);
				Util.printMsg("Sent " + i);
				Util.randSleep(1000);
			}
		}
	}
	
	private class Consumer implements Runnable {
		public void run() {
			for (int i = 0; i < 3; i++) { 
				Util.randSleep(1000);
				Util.printMsg("Got " + box.take());
			}
		}
	}
	
	public static void main(String[] args) {
		BoxExample bx = new BoxExample();
		new Thread(bx.new Producer()).start();
		new Thread(bx.new Consumer()).start();
		new Thread(bx.new Producer()).start();
		new Thread(bx.new Consumer()).start();
	}

//	Thread-0 : Sent 0
//	Thread-3 : Got 0
//	Thread-0 : Sent 1
//	Thread-1 : Got 1
//	Thread-2 : Sent 0
//	Thread-3 : Got 0
//	Thread-2 : Sent 1
//	Thread-1 : Got 1
//	Thread-0 : Sent 2
//	Thread-1 : Got 2
//	Thread-2 : Sent 2
//	Thread-3 : Got 2
}
