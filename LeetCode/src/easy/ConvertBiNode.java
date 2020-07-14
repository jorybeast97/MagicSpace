package easy;

import structure.TreeNode;

public class ConvertBiNode {

    TreeNode pre = new TreeNode(-1);
    public TreeNode convertBiNode(TreeNode root) {
        helper(root);
        return root.right;
    }

    public void helper(TreeNode root) {
        if (root == null) return;
        helper(root.left);
        pre.right = root;
        root.left = null;
        pre = pre.right;
        helper(root.right);
    }
}
