public class SimpleMonitors {
	private String message = null;
	private Object monitor = new Object();

	public static void main(String[] args) {
		new SimpleMonitors().runExample();
	}

	public void runExample() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (monitor) {
					while (message == null)
						try {
							monitor.wait();
						} catch (InterruptedException e) {
						}
				}
				System.out.println("Got: " + message);
			}
		});
		t.start();
		synchronized(monitor) {
			monitor.notify();
			message = "Hi man";
		}
	}

}
