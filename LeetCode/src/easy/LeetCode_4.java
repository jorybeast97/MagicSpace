package easy;

import java.util.Arrays;

public class LeetCode_4 {

    public static void main(String[] args) {
        System.out.println(5/2);
    }
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        double[] res = new double[nums1.length + nums2.length];
        int index = 0;
        for (; index < nums1.length; index++) {
            res[index] = nums1[index];
        }
        for (int i = 0; i < nums2.length; i++) {
            res[index] = nums2[i];
            index++;
        }
        Arrays.sort(res);
        if (res.length % 2 == 0) {
            return (res[res.length / 2] + res[res.length / 2 - 1])/2;
        }else {
            return res[res.length / 2];
        }
    }
}
