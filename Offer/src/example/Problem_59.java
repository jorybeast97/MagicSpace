package example;

import java.util.ArrayList;
import java.util.List;

/**
 * 滑动窗口最大值
 */
public class Problem_59 {

    /**
     * 遍历法
     * @param arr
     * @param k
     * @return
     */
    public List<Integer> solution(int[] arr, int k) {
        List<Integer> list = new ArrayList<>();
        int start = 0;
        for (int i = k - 1; i < arr.length; i++) {
            list.add(maxValue(arr, start, i));
            start++;
        }
        return list;
    }

    public int maxValue(int[] arr, int star, int end) {
        int result = Integer.MIN_VALUE;
        for (int i = star; i < end - 1; i++) {
            result = Math.max(result, arr[i]);
        }
        return result;
    }
}
