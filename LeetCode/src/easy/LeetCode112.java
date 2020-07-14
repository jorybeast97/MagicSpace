package easy;

import structure.TreeNode;

public class LeetCode112 {

    boolean res = false;
    public boolean hasPathSum(TreeNode root, int sum) {
        helper(root, sum);
        return res;
    }

    public void helper(TreeNode root, int sum) {
        if (root == null) return;
        sum = sum - root.val;
        if (root.left == null && root.right == null && sum == 0){
            res = true;
            return;
        }
        helper(root.left, sum);
        helper(root.right, sum);
    }
}
