package sort;

import structure.Utils;

public class MergeSort {

    public static void main(String[] args) {
        int[] test = Utils.getArray(50, 15);
        MergeSort mergeSort= new MergeSort();
        mergeSort.mergeSort(test, 0, test.length - 1);
        Utils.ergodic(test);
    }

    public void merge(int[] nums, int left, int mid, int right) {
        int[] helper = new int[right-left+1];
        int leftPoint = left;
        int rightPoint = mid + 1;
        int k = 0;
        while (leftPoint <= mid && rightPoint <= right) {
            if (nums[leftPoint] <= nums[rightPoint]) {
                helper[k] = nums[leftPoint];
                k++;
                leftPoint++;
            }else {
                helper[k] = nums[rightPoint];
                k++;
                rightPoint++;
            }
        }
        while (leftPoint <= mid) {
            helper[k] = nums[leftPoint];
            k++;
            leftPoint++;
        }
        while (rightPoint <= mid) {
            helper[k] = nums[rightPoint];
            k++;
            rightPoint++;
        }
        for (int i = 0; i < helper.length; i++) {
            nums[left+i] = helper[i];
        }
    }

    public void mergeSort(int[] nums, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(nums, left, mid);
            mergeSort(nums, mid + 1, right);
            merge(nums, left, mid, right);
        }
    }
}
