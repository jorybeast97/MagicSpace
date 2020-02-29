package hard;

import structure.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class LeetCode_94 {
    List<Integer> result = new ArrayList<>();
    public List<Integer> inorderTraversal(TreeNode root) {
        if (root == null) return null;
        dfs(root);
        return result;
    }

    public void dfs(TreeNode root) {
        if (root == null) {
            return;
        }
        dfs(root.left);
        result.add(root.val);
        dfs(root.right);
    }
}
