package hard;

import structure.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class LeetCode_145 {
    List<Integer> result = new ArrayList<>();
    public List<Integer> postorderTraversal(TreeNode root) {
        helper(root);
        return result;
    }

    public void helper(TreeNode root) {
        if (root == null) return;
        helper(root.left);
        helper(root.right);
        result.add(root.val);
    }
}
