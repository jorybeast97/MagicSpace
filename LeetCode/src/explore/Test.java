package explore;

import java.util.concurrent.*;

public class Test {


    public static void main(String[] args) {

        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(5, 10, 5000, TimeUnit.HOURS,
                        new ArrayBlockingQueue<>(100));
        Future<Object> future = (Future<Object>) threadPoolExecutor.submit(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
            }
            System.out.println("工作已经完成");
        });
        for (int i = 0; i < 100000000; i++) {

        }
        System.out.println(future.cancel(true));
    }




}
