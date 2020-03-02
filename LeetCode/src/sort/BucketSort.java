package sort;

import structure.Utils;

public class BucketSort {

    public static void main(String[] args) {
        int[] test = Utils.getArray(50, 15);
        Utils.ergodic(test);
        BucketSort bucketSort = new BucketSort();
        bucketSort.bucketSort(test, 0, 50);
        Utils.ergodic(test);
    }

    public void bucketSort(int[] nums, int start, int end) {
        int[] bucket = new int[end - start + 1];
        for (int t : nums) {
            int index = t - start;
            bucket[index] = bucket[index] + 1;
        }
        int index = 0;
        for (int i = 0; i < bucket.length; i++) {
            if (bucket[i] == 0) {
                continue;
            }else {
                for (int t = 0; t < bucket[i]; t++) {
                    nums[index] = i;
                    index++;
                }
            }
        }
    }
}
