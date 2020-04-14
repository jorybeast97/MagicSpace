package condition;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionTest<T> {

    private ReentrantLock lock = new ReentrantLock();
    private Condition producer = lock.newCondition();
    private Condition consumer = lock.newCondition();
    private Deque<T> list = new LinkedList<>();
    private final Integer maxSize = 50;
    private Integer count = 0 ;

    public void put(T t) {
        lock.lock();
        try {
            while (list.size() >= maxSize) {
                producer.await();
            }
            list.add(t);
            count++;
            consumer.signalAll();
        } catch (Exception e) {
            e.getStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public T get() {
        T res = null;
        lock.lock();
        try {
            while (list.size() <= 0) {
                consumer.await();
            }
            res = list.removeFirst();
            count--;
            producer.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return res;
    }

    public static void main(String[] args) throws InterruptedException {
        ConditionTest<Object> test = new ConditionTest<>();
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        System.out.println(Thread.currentThread().getName()+"消费"+test.get());
                        System.out.println("剩余库存"+test.count);
                    }
                }
            }).start();
        }


        for (int i = 0; i < 2; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 250; i++) {
                        Object obj = new Object();
                        test.put(obj);
                        System.out.println(Thread.currentThread().getName()+"生产"+obj);
                        System.out.println("现有库存"+test.count);
                    }
                }
            }).start();
        }
    }
}
