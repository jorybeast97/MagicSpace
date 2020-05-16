package Model;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Runner {
    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3, new Runnable() {
            @Override
            public void run() {

                try {
                    System.out.println("裁判准备");
                    Thread.sleep(1000);
                    System.out.println("裁判吹哨");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t1 = new Thread(new StudentRunner(cyclicBarrier, "李林"));
        Thread t2 = new Thread(new StudentRunner(cyclicBarrier, "张强"));
        Thread t3 = new Thread(new StudentRunner(cyclicBarrier, "赵勇"));
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(t1);
        executorService.execute(t2);
        executorService.execute(t3);
        executorService.shutdown();
    }
}

class StudentRunner implements Runnable {

    private CyclicBarrier cyclicBarrier;
    private String name;

    public StudentRunner(CyclicBarrier cyclicBarrier, String name) {
        this.cyclicBarrier = cyclicBarrier;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(new Random().nextInt(2000));
            System.out.println(name + "准备就绪");
            cyclicBarrier.await();
            System.out.println(name + "开始跑步");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
