package explore;

public class MyLock {

    public synchronized void testOne() {
        System.out.println(Thread.currentThread().getName() + "执行方法一");
    }

    public synchronized void testTwo() {
        System.out.println(Thread.currentThread().getName() + "执行方法二");
    }
}
