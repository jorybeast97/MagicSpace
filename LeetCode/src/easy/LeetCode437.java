package easy;

import structure.TreeNode;


public class LeetCode437 {

    int res = 0;
    public int pathSum(TreeNode root, int sum) {
        if (root == null) return 0;
        dfs(root, sum);
        pathSum(root.left, sum);
        pathSum(root.right, sum);
        return res;
    }

    public void dfs(TreeNode root, int sum) {
        if (root == null) return;
        sum = sum - root.val;
        if (sum == 0) res++;
        dfs(root.left, sum);
        dfs(root.right, sum);
    }
}
