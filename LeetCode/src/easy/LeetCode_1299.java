package easy;

import java.util.Map;

public class LeetCode_1299 {

    public int[] solution(int[] arr) {
        if (arr == null || arr.length<=1) return arr;
        for (int i = 0; i < arr.length; i++) {
            arr[i] = getMax(arr, i + 1, arr.length - 1);
        }
        return arr;
    }

    public int getMax(int[] arr, int left, int right) {
        int max = -1;
        for (int i = left; i < right; i++) {
            max = Math.max(max, arr[i]);
        }
        return max;
    }
}
