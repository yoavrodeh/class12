public class Box2Example {
	private Box<Integer> box = new Box<>();

	private class Producer implements Runnable {
		public void run() {
			for (int i = 0; i < 10000; i++) {
				box.put(i);
				Util.printMsg("Sent " + i);
			}
		}
	}

	private class Consumer implements Runnable {
		public void run() {
			for (int i = 0; i < 10000; i++) {
				Util.printMsg("Got " + box.take());
			}
		}
	}

	public static void main(String[] args) {
		Box2Example bx = new Box2Example();
		new Thread(bx.new Producer()).start();
		new Thread(bx.new Consumer()).start();
		new Thread(bx.new Producer()).start();
		new Thread(bx.new Consumer()).start();
	}
}
