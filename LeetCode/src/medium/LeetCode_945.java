package medium;

import java.util.Arrays;

public class LeetCode_945 {

    public static void main(String[] args) {
        int[] arr = {3, 2, 1, 2, 1, 7};
        System.out.println(new LeetCode_945().solution(arr));
    }

    public int solution(int[] arr) {
        if (arr == null || arr.length<=1) return 0;
        Arrays.sort(arr);
        int result = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] <= arr[i-1]) {
                int pre = arr[i];
                arr[i] = arr[i - 1] + 1;
                result = result + (arr[i] - pre);
            }
        }
        return result;
    }
}
