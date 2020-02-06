package example;

import java.util.Arrays;

/**
 * 最小的K个数
 */
public class Problem_40 {

    /**
     * 排序后输出O(nlogn)
     * @param arr
     * @param k
     * @return
     */
    public int solution_1(int[] arr , int k) {
        Arrays.sort(arr);
        return arr[k - 1];
    }


}
