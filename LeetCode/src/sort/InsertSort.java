package sort;

import structure.Utils;

import javax.rmi.CORBA.Util;

public class InsertSort {

    public static void main(String[] args) {
        int[] test = Utils.getArray(50, 10);
        InsertSort insertSort = new InsertSort();
        insertSort.solution(test);
        Utils.ergodic(test);
    }
    public void solution(int[] nums) {
        if (nums == null){
            return;
        }
        for (int i = 0; i < nums.length-1; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[j] < nums[i]){
                    swap(nums, j, i);
                }else {
                    continue;
                }
            }
        }
    }

    public void swap(int[] nums, int n, int m) {
        int temp = nums[n];
        nums[n] = nums[m];
        nums[m] = temp;
    }
}
