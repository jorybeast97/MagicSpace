package sort;

import structure.Utils;

public class BubbleSort {

    public static void main(String[] args) {
        int[] arr = {5,8,5,9,6,7,5,8,5,11,45,8};
        solution(arr);
        Utils.ergodic(arr);
    }

    public static void solution(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            for (int j = i; j < nums.length; j++) {
                if (nums[j] < nums[i]) {
                    int temp = nums[j];
                    nums[j] = nums[i];
                    nums[i] = temp;
                }
            }
        }
    }
}
