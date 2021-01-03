# Concurrency
## wait & notify

---
## Today
1. monitors : `wait` & `notify`.
1. producer - consumer.
1. Synchronization point objects.
1. Locks.
1. A peek into the Java concurrency Library.


---
## Resources
1. Oracle Java Tutorials - [Concurrency](https://docs.oracle.com/javase/tutorial/essential/concurrency/)
1. Jenkov.com - [Java Concurrency and Multithreading Tutorial](http://tutorials.jenkov.com/java-concurrency/index.html)


---
### Last Lecture
We learned about simple ways for communication between threads. 

Today we'll learn the basic Java mechanisms that assist synchronization between threads, and we'll see how to build interesting tools to help us when programming with many threads.

Still - this is just an introduction. Multi-threaded programming is a very deep subject, and if you ever program more complex code, study this subject well!


---
We learned about synchronized blocks:
```java code-noblend
synchronized(obj) {
	// our code
}
```
+ To enter a synchronized block, a thread must first acquire the lock of `obj`.
+ If any other thread has this lock already. This thread will wait.
+ Only when leaving the block, the lock is released.
+ A method can be synchronized, and this is as if its code is in a `synchronized(this)` block.



---
### Monitors

Any java object can be used as a monitor.
+ `wait()` on a monitor makes a thread wait, until it is notified to wake up.
+ `notify()` on a monitor makes one of the threads waiting on the monitor wake up.
+ `notifyAll()` makes all the waiting threads on the monitor wake up.

Let's  see how this is used:



---
@code[java code-max code-noblend](src/SimpleMonitors.java)
@[1-7](A shared `message` and `monitor` object. Ignore the `synchronized` statements for now.)
@[9-22](If the `message` is not ready, wait on the monitor.)
@[23-28](the main thread.)


---

@box[rounded](A thread must be inside a `synchronized(monitor)` block to be able to call `monitor`'s `wait`, `notify` and `notifyAll` methods.) 

Why? looking at the last example without the synchronized blocks,
+ if the message is ready and notify is called exactly after the condition in the while, the `notify` is lost and `wait` will be forever.

	
---
How can the main thread enter the synchronized block if we are stuck in wait?
@box[rounded](When a thread calls `wait` it releases the lock automatically.)
+ When calling `notifyAll` on a monitor, all of the threads that wait on it are woken up.
+ When calling `notify`, only one of them is (we don't know who).


---
### Waking up

To complete a `monitor.wait()`, a thread:
+ Has to be woken up as explained,
+ Then it has to acquire the lock of `monitor`, just like it enters a `synchronized(monitor)` block
  + After all, the wait is inside such a block.
+ If another thread sends an `interrupt` to the waiting `thread`, `wait` will throw an `InterruptedException`, just like `Thread.sleep`.

@css[fragment](*To conclude, let's see a possible run of the previous example.*)



---
@code[java code-max code-noblend](src/SimpleMonitors.java)
@[23](The main thread starts the secondary thread.)
@[12-14](The secondary thread takes the `monitor` lock and checks the condition)
@[23-24](The main thread cannot enter the synchronized block.)
@[12-16](The secondary thread calls `wait` and releases the `monitor`.)
@[23-26](The main thread acquires the `monitor` lock, sets the message and calls `notify` on the monitor.)
@[12-16](The secondary thread wakes up, but now waits for the `monitor` lock so it can reenter the synchronized block.)
@[23-27](The main leaves the block, and so releases `monitor`.)
@[12-21](The secondary thread gets the lock, and so `wait` completes and the thread continues. It then prints the message.)



---
### Guarded Block Idiom
Also called a **spin lock**. It is a very common coordination mechanism between threads.

One thread wants to wait until some condition is set by another thread. 


---
The waiting thread:
```java code-noblend
synchronized(monitor) {
	while(!cond)
		try {
			monitor.wait();
        } catch (InterruptedException e) {}
}
```
The other one:
```java code-noblend
synchronized(monitor) {
	cond = true;
	monitor.notify();
}
```


---
### Questions
1. Why use a `while` and not an `if`? 
@css[fragment](*This is because we may wake up because of a different reason.*)
1. Why doesn't Java allow using one object for `wait` and `notify` and another for `synchronized`?
@css[fragment](*because this way, `wait` does not release the synchronized object and we will be stuck forever.*) 
1. Can we do `synchronized` only on `notify`, putting the `cond = true` before the block?
@css[fragment](*We can.*)





---
### Spurious Wakeups

Why do we use a `while` and not an `if` when checking the condition in the spin lock?
+ It makes the code safer - someone may call `notify` and the condition is not really set.
+ Our thread might have been woken up for unknown reasons.
  + This is rare but it happens.

Therefore, always recheck the condition for waking up.
  


---
### Producer - Consumer

A very standard situation in multi-threading is that some threads produce data, and other threads need this data. 

In the simple case, there is one box to put values in, so:
+ The consumer waits if the box is empty, and the producer waits if it is full.
+ The producer notifies when it fills the box, and the consumer when it takes a value.

@css[fragment](*First, a simple utility class:*)




---
@code[java code-max code-noblend](src/Util.java)
@[3-14](So we don't have to catch `InterruptedException` when we just want to sleep. The random sleeping will be useful for our examples.)
@[16-21](Here as well.)
@[23-29](Notice the check for spurious wakeups.)
@[31-35](So we can see who is writing the message.)

@css[fragment](*Now we can write the box:*)



---
@code[java code-max code-noblend](src/Box.java)
@[1-11](Our monitor is the instance of `Box` itself, so we use a synchronized method and simply call `notifyAll`.)
@[13-19](Does the order of `notifyAll` and `empty = true` matter?)



---
@code[java code-max code-noblend](src/BoxExample.java)
@[1-12](Sleeping is just so it is a little more interesting.)
@[14-21]
@[23-29](This is the way to create an non-static inner class from outside: since it is always related to an instance of an outer class, it must be created from it.)
@[31-42]

---
### An Improvement

In our `Box`, we had to use `notifyAll` because there may be many threads waiting, some for `put` and some for `take`. Calling `notify` may wake the wrong one and we will be stuck.

How can we solve it? @css[fragment](have two different monitors, which is pretty tricky. See `Box2.java` for a possible solution.)



---
### Example

As another example for a spin-lock, let's write a class that is used for making sure all threads reach a certain point before continuing.

Here is how it is used:



---
@code[java code-noblend](src/SyncPointExample.java)

How would you write the class `SyncPoint`?

Try it first by using a **busy wait**.


---
@code[java code-noblend](src/BusySyncPoint.java)
This is highly unefficient, and you should **never use it**. But it is a good starting point for the real thing:

---
@code[java code-noblend](src/SyncPoint.java)
Notice the `while` loop, which checks for spurious wakeups.


---
### `CountDownLatch`
A part of the Java concurrency package, it is very similar to our `SyncPoint`.
+ The constructor has an `int count` parameter.
+ `await()` makes the current thread wait until the count is 0.
+ `countDown()` decrements the count, possibly releasing all threads that are waiting for it to reach 0.



---
### Example

An important example is a different kind of locking object:
+ It has two methods `lock` and `unlock`. 
+ Once an instance is locked, every call to `lock` is blocked (waits), until `unlock()` is called.

@css[fragment](*Here is a simple example of how to use it:*)



---
@code[java code-max code-noblend](src/MyLockExample.java)
@[1-13](`counter` will be common to many threads.)
@[15-25](This indeed prints 3000.)
@css[fragment](How would you write `MyLock`? Again, start by using a busy wait.)


---
@code[java code-noblend](src/BusyMyLock.java)
But what if two threads are waiting on the loop, and leave it at the same time? so we can improve:

---
@code[java code-noblend](src/BusyMyLock2.java)
@[4-12]
This works, but **don't** do this! now we can write the real thing: 

---
@code[java code-noblend](src/MyLock.java)
Here we can use `notify` and not `notifyAll`. 

This is actually simpler than `BusyMyLock2`, because `wait` automatically releases the `synchronized` lock.



---
### Problems:
1. One thread may lock `MyLock` and a different one unlocks it. 
1. It is not *reentrant*: if a thread aquires the lock, and tries to do it again, it locks itself.

In other words, we want a thread that calls `lock` to **own** the lock.


---
@code[java code-max code-noblend](src/MyReentrantLock.java)
@[1-12](Again, the method is synchronized on `this`, and therefore we can call `this.wait` and `this.notify` inside of it.)
@[14-21]



---
Java really has an interface `Lock` with implementation `ReentrantLock` which is like what we saw, but has more methods. For example, they can have different conditions connected with them, allowing threads to wait on a specific condition of a lock.
+ An interesting advantage `Lock` gives over using `synchronized` blocks, is that the unlocking can happen in a different method than the locking. 
+ Care should be taken to always call `unlock` - normally done in a `finally` block.



---
### Last Example

A class that supports giving turns to threads. It has methods:
1. `add()` which adds the current thread to the game.
1. `waitForTurn()` which makes the calling thread block (wait) until it is its turn.
1. `done()` which is called by a thread when it finishes what it had to do in its turn.



---
@code[java code-noblend](src/TurnsExample.java)
@[1-12](The `justSleep` is so that all the threads call `add` before starting. This is a bad solution - never do it this way!)
@[14-18]
@[20-28](Note how the order of threads is consistent.)
@css[fragment](How should we do this?)



---
@code[java code-max code-noblend](src/Turns.java)
@[4-10](`current` is the index of the thread who's turn is now. `add` is synchronized, because it changes the list.)
@[12-20](There is no need for `myTurn` to be synchronized. )
@[22-29](Never forget to notify!) 




---
## Further

In these classes we have only scratched the surface of Java's support for concurrency.

There are many more things to know, and here is a very brief overview.



---
## Java Library for Concurrency

Java offers many classes suitable for using with multi-threading. Under normal circumstances, it is better to use them then implement your own.
+ They are efficient.
+ And correct!



---
### `BlockingQueue` 
Useful for sending messages between threads.
+ It blocks or times out when you attempt to add to a full queue,
+ or retrieve from an empty queue. 

This is a lot like our `Box`, but can have a large capacity.



---
### Collections
If you are sharing any collection (`List`, `Set`, etc.) between threads, it is best to use a thread safe collection. For example,
+ If `list` is some `List`, then 
`Collections.synchronizedList(list)`, returns a thread-safe version of it.
See [here](https://howtodoinjava.com/java/collections/arraylist/synchronize-arraylist/) for more information.
+ `ConcurrentHashMap` supports full concurrency of retrievals and high expected concurrency for updates. 



---
### Executors

Handle thread creation and management for you. 
+ Most executors use thread pools, which consist of worker threads. This kind of thread exists separately from the `Runnable` it executes and is often used to execute multiple tasks.
+ Using worker threads minimizes the overhead due to thread creation. In large applications, allocating and deallocating many thread objects creates a significant overhead.



---
@code[java code-max code-noblend](src/ExecutorExample.java)
@[5-12]
@[14-27](`shutdown` does not kill the threads, only stops accepting new threads.)

---
### JavaFX multiThreading

+ The JavaFX scene graph is **not thread-safe** and can only be accessed and modified from the JavaFX Application thread. 
+ This means that using the main thread for long calculations will freeze the GUI.
+ The problem is other threads cannot directly change GUI elements.

For a full explanation, see Oracle's [tutorial](https://docs.oracle.com/javase/8/javafx/interoperability-tutorial/concurrency.htm#JFXIP546).
The simplest solution is:


---
#### `Platform.runLater(Runnable)`
+ Asks the main thread to execute the `run` of `Runnable` when it can.
+ Normally, a thread will run some long command (calculation or I/O), and when it wants to update the GUI, it will call `runLater` with the update commands.

In the following example, we implement a simple counter. Note that calling `Thread.sleep` is not allowed within the main thread.



---
@code[java code-max code-noblend](src/JavaFXExample.java)
@[7-13]
@[15-21](This will be called from the secondary thread.)
@[23-36](Closing the window of the application will not close the secondary thread, and it will keep counting without the GUI.)



