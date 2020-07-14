package sort;

import structure.Utils;

public class QuickSort {

    public static void main(String[] args) {
        int[] test = Utils.getArray(50, 15);
        QuickSort quickSort = new QuickSort();
        quickSort.quickSort(test, 0, test.length - 1);
        Utils.ergodic(test);
    }

    public void quickSort(int[] nums, int left, int right) {
        if (left >   right) return;
        //确定基准数
        int benchmark = nums[left];
        int i = left;
        int j = right;
        while (i != j) {
            while (nums[j] >= benchmark && j > i) {
                j--;
            }
            while (nums[i] <= benchmark && i < j) {
                i++;
            }
            if (j > i) {
                Utils.swap(nums, i, j);
            }
        }
        //将基准数和左侧最大数交换
        Utils.swap(nums, left, i);
        quickSort(nums, left, i - 1);
        quickSort(nums, j+1, right);
    }
}
