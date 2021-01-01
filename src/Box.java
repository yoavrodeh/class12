public class Box<T> {	
    private T value;
    private boolean empty = true;

    public synchronized void put(T value) {
        while (!empty)
        	Util.justWait(this);
        this.value = value;
        empty = false;
        notifyAll();
    }

    public synchronized T take() {
        while (empty) 
        	Util.justWait(this);
        empty = true;
        notifyAll();
        return value;
    }
}