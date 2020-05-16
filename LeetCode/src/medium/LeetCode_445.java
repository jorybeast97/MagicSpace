package medium;

import structure.ListNode;
import structure.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LeetCode_445 {
    public TreeNode sortedArrayToBST(int[] nums) {
        if (nums.length == 0) return null;
        return build(nums, 0, nums.length - 1);
    }

    public TreeNode build(int[] nums, int left, int right) {
        if (left > right) return null;
        int mideum = (left + right) / 2;
        int rootVal = nums[mideum];
        TreeNode node = new TreeNode(rootVal);
        node.left = build(nums, left, mideum - 1);
        node.right = build(nums, mideum + 1, right);
        return node;
    }

    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int index = nums1.length - 1;
        while (m > 0 || n > 0) {
            if (nums1[m] > nums2[n]) {
                nums1[index] = nums1[m];
                m--;
                index--;
            }else {
                nums1[index] = nums2[n];
                n--;
                index--;
            }
        }
    }
}
