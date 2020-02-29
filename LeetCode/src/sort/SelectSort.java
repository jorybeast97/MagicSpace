package sort;

import structure.Utils;

public class SelectSort {

    public static void main(String[] args) {
        int[] arr = Utils.getArray(100, 15);
        solution(arr);
        for (int t : arr) {
            System.out.println(t);
        }
    }



    public static void solution(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            int minIndex = i;
            for (int j = i; j < nums.length; j++) {
                if (nums[j] < nums[minIndex]) {
                    minIndex = j;
                }
            }
            int temp = nums[i];
            nums[i] = nums[minIndex];
            nums[minIndex] = temp;
        }
    }
}
