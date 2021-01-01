public class Box2<T> {
	private T value;
	private boolean empty = true;
	private Object monitor1 = new Object();
	private Object monitor2 = new Object();

	public void put(T value) {
		synchronized (monitor1) {
			while (!empty)
				Util.justWait(monitor1);
			synchronized (monitor2) {
				empty = false;
				this.value = value;
				monitor2.notify();
			}
		}
	}

	public T take() {
		synchronized (monitor2) {
			while (empty)
				Util.justWait(monitor2);
			synchronized (monitor1) {
				empty = true;
				monitor1.notify();
				return value;
			}
		}
	}
}
