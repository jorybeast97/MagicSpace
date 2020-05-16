package Model;

public class SYNCPC_Model {

    public static Integer count = 0;
    public static Integer max = 30;
    public static String lock = "LOCK";

    public static void main(String[] args) {
        new Thread(new Producer()).start();
        new Thread(new Producer()).start();
        new Thread(new Producer()).start();
        new Thread(new Producer()).start();
        new Thread(new Producer()).start();
        new Thread(new Customer()).start();
        new Thread(new Customer()).start();
        new Thread(new Customer()).start();
        new Thread(new Customer()).start();
        new Thread(new Customer()).start();
        new Thread(new Customer()).start();
        new Thread(new Customer()).start();
        new Thread(new Customer()).start();
        new Thread(new Customer()).start();
        new Thread(new Customer()).start();
        new Thread(new Customer()).start();
        new Thread(new Customer()).start();
        new Thread(new Customer()).start();

    }

    static class Producer implements Runnable{

        @Override
        public void run() {
            try {
                product();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void product() throws InterruptedException {
            while (true) {
                synchronized (this) {
                    while (SYNCPC_Model.count == SYNCPC_Model.max) wait();
                    SYNCPC_Model.count++;
                    System.out.println(Thread.currentThread().getName() + "生产产品,当前产品数量为"+ SYNCPC_Model.count);
                    notifyAll();
                }
                Thread.sleep(500);
            }
        }
    }

    static class Customer implements Runnable{

        @Override
        public void run() {
            try {
                cosume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void cosume() throws InterruptedException {
            while (true) {
                synchronized (this) {
                    while (SYNCPC_Model.count == 0) wait();
                    SYNCPC_Model.count--;
                    System.out.println(Thread.currentThread().getName() + "消费产品,当前产品数量为"+ SYNCPC_Model.count);
                    notifyAll();
                }
                Thread.sleep(500);
            }
        }
    }

}


