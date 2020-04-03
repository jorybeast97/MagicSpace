package easy;

import structure.TreeNode;

public class LeetCode_530 {

    TreeNode pre = null;
    int result = Integer.MAX_VALUE;
    public int getMinimumDifference(TreeNode root) {
        if (root == null) return 0;
        helper(root);
        return result;
    }

    public void helper(TreeNode root) {
        if (root == null) return;
        helper(root.left);
        if (pre != null) {
            result = Math.min(result, Math.abs(pre.val - root.val));
        }
        pre = root;
        helper(root.right);
    }
}
