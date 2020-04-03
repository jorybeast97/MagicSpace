package structure;

import java.util.Arrays;
import java.util.Random;

public class Utils {

    /**
     * 创建一个随机数组
     * @param bound
     * @param length
     * @return
     */
    public static int[] getArray(int bound, int length) {
        if (length <= 0) {
            return null;
        }
        int[] result = new int[length];
        Random random = new Random();
        for (int i = 0; i < result.length; i++) {
            result[i] = random.nextInt(bound);
        }
        return result;
    }

    /**
     * 遍历数组
     * @param arr
     */
    public static void ergodic(int[] arr) {
        System.out.print("数组遍历结果为 : ");
        for (int t : arr) {
            System.out.print(t + " ");
        }
        System.out.println();
    }

    /**
     * 交换元素
     * @param arr
     * @param a
     * @param b
     */
    public static void swap(int[] arr, int a, int b) {
            int temp = arr[a];
            arr[a] = arr[b];
            arr[b] = temp;
    }




}
