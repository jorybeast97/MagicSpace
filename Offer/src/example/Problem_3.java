package example;

import java.util.Arrays;

/**
 * 数组中重复的数字
 */
public class Problem_3 {

    public int solution(int[] arr) {
        Arrays.sort(arr);
        for (int i = 0; i < arr.length-1; i++) {
            if (arr[i] == arr[i + 1]) {
                return arr[i];
            }
        }
        return 0;
    }
}
