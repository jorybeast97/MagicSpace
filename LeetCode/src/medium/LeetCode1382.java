package medium;

import structure.ListNode;
import structure.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeetCode1382 {

    List<Integer> list = new ArrayList<>();
    public TreeNode balanceBST(TreeNode root) {
        int[] arr = new int[list.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i);
        }
        return build(0, arr.length - 1, arr);
    }

    public void helper(TreeNode root) {
        if (root == null) return;
        helper(root.left);
        list.add(root.val);
        helper(root.right);
    }

    public TreeNode build(int left, int right,int[] nums) {
        if (left > right) return null;
        int mid = (left + right) / 2;
        int temp = nums[mid];
        TreeNode node = new TreeNode(temp);
        node.left = build(left, mid - 1, nums);
        node.right = build(mid + 1, right, nums);
        return node;
    }
}
