package Multithreading;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ThreadTest implements Runnable {

    public static void main(String[] args) {
        ThreadTest ts1 = new ThreadTest();
        Thread t1 = new Thread(ts1);
        Thread t2 = new Thread(ts1);
        t1.start();
        t2.start();
    }

    public void run() {
        printSomthing();
    }

    int index = 1;

    public synchronized void printSomthing() {
        for (; index <= 50; index++) {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + "  " + index);
            this.notifyAll();
            try {
                this.wait();
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
