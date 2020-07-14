package easy;

import structure.TreeNode;

public class LeetCode404 {
    public int sumOfLeftLeaves(TreeNode root) {
        if (root == null) return 0;
        int res = 0;
        if (root.left != null && root.left.left == null && root.left.right == null) {
            res = res + root.left.val;
        }
        return sumOfLeftLeaves(root.left) + sumOfLeftLeaves(root.right);
    }
}
