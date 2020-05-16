package Model;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentlockPC_Model {

    static int size = 0;
    static int max = 50;
    static ReentrantLock lock = new ReentrantLock();
    static Condition proCondition = lock.newCondition();
    static Condition cusCondition = lock.newCondition();

    public static void main(String[] args) {
        new Thread(new producer()).start();
        new Thread(new producer()).start();
        new Thread(new producer()).start();
        new Thread(new customer()).start();
        new Thread(new customer()).start();
        new Thread(new customer()).start();
        new Thread(new customer()).start();
    }

    static class producer implements Runnable{
        @Override
        public void run() {
            product();
        }

        public void product() {
            while (true) {
                try {
                    lock.lock();
                    while (size == max) proCondition.await();
                    size++;
                    System.out.println(Thread.currentThread().getName() + "生产产品"+ size);
                    cusCondition.signalAll();
                } catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    lock.unlock();
                }
            }
        }
    }

    static class customer implements Runnable{
        @Override
        public void run() {
            cusume();
        }

        public void cusume() {
            while (true) {
                try {
                    lock.lock();
                    while (size == 0) cusCondition.await();
                    size--;
                    System.out.println(Thread.currentThread().getName() + "消费产品"+ size);
                    cusCondition.signalAll();
                } catch (Exception e) {
                    //ingnore
                }finally {
                    lock.unlock();
                }
            }
        }
    }


}
