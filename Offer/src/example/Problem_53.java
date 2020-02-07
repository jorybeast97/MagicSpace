package example;

import java.util.Arrays;

/**
 * 数字在排序数组中出现的次数
 * 已知数组arr已经升序排序
 */
public class Problem_53 {


    /**
     * 最简单的做法
     * @param arr
     * @param k
     * @return
     */
    public int solution(int[] arr, int k) {
        int num = 0;
        for (int t : arr) {
            if (t == k) {
                num++;
            }
        }
        return num;
    }

    /**
     * 二分查找 O(logN)
     * @param arr
     * @param k
     * @return
     */
    public int binarySolution(int[] arr, int k) {
        int index = Arrays.binarySearch(arr, k);
        int num = 1;
        for (int i = index; i < arr.length - 1 && arr[i] == k; i++) {
            num++;
        }
        for (int i = index - 1; i >= 0 && arr[i] == k; i--) {
            num++;
        }
        return num;
    }


}
