package lock;

import CAS.CompareAndSweepUtils;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class MagnoliaLock {





    /**
     * 锁状态值，可重入，但是同一时间只能由一个类操作
     */
    private ReentrantLock lock = new ReentrantLock();
    private volatile int state = 0;

    private Thread lockHolder;

    private CompareAndSweepUtils compareAndSweepUtils = new CompareAndSweepUtils();

    private ConcurrentLinkedQueue<Thread> waiterQueue = new ConcurrentLinkedQueue<>();

    /**
     * 加锁操作
     */
    public void lock() {
        //如果加锁成功，则直接跳出，执行接下来的代码块
        if (aquire()) {
            return;
        }
        Thread currentThread = Thread.currentThread();
        //放入阻塞队列
        waiterQueue.add(currentThread);
        //如果不成功，应该有自旋和阻塞的操作
        while (true) {
            //公平锁,如果当前等待的线程是队列中的第一个并且成功获得了锁，才会跳出循环执行
            if (currentThread==waiterQueue.peek() && aquire()) {
                //成功获取锁后，需要移除等待队列的首个元素
                waiterQueue.poll();
                break;
            }
            //使用park是能够让Thread阻塞并让出执行权
            LockSupport.park(currentThread);
        }
    }

    public void unlock() {
        Thread currentThread = Thread.currentThread();
        if (currentThread != lockHolder) {
            throw new RuntimeException("线程不是锁持有者,不能更改state状态");
        }
        int state = getState();
        if (compareAndSweepUtils.compareAndSweepState(state, state - 1, this)) {
            if (state == 0){
                //释放锁
                setLockHolder(null);
                //唤醒队列中第一个线程
                if (waiterQueue.size() != 0) {
                    Thread firstParkThread = waiterQueue.peek();
                    LockSupport.unpark(firstParkThread);
                }
            }
        }
    }

    /**
     * 加锁的操作
     * @return
     */
    public boolean aquire() {
        //拿到当前的线程
        Thread currentThread = Thread.currentThread();
        //判断当前的锁状态是不是为0
        int temp = getState();
        if (temp == 0) {
            //修改同步器的值
            boolean res = compareAndSweepUtils.compareAndSweepState(temp, temp + 1, this);
            boolean hasThread = waiterQueue.size() != 0;
            if (res && !hasThread) {
                //修改锁的持有者,指向当前线程的引用，表示当前线程已经持有了锁
                lockHolder = currentThread;
                return true;
            }
        }
        return false;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Thread getLockHolder() {
        return lockHolder;
    }

    public void setLockHolder(Thread lockHolder) {
        this.lockHolder = lockHolder;
    }
}
