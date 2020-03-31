package medium;

import structure.Utils;

public class LeetCode_912 {



    public void sort(int[] nums, int left, int right) {
        if (left >= right) return;
        int index = nums[left];
        int i = left;
        int j = right;
        while(i != j) {
            while (nums[j] <  index) {
                j--;
            }
            while (nums[i] > index) {
                i--;
            }
            if (j > i) Utils.swap(nums, i, j);
        }
        Utils.swap(nums, left, i);
        sort(nums, left, left - 1);
        sort(nums, left+1, right);
    }
}
