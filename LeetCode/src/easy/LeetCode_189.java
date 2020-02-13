package easy;

import java.util.Arrays;

/**
 * 旋转数组
 */
public class LeetCode_189 {

    public static void main(String[] args) {
        int[] a = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        rotate(a, 2);
        for (int i : a) {
            System.out.println(i);
        }
    }

    public static void rotate(int[] nums, int k) {
        //防止出现k大于数组长度的情况
        k =k % nums.length;
        reverse(nums, 0, nums.length - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, nums.length - 1);
    }
    public static void reverse(int[] nums, int start, int end) {
        while (start < end) {
            int temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;
            start++;
            end--;
        }
    }
}
