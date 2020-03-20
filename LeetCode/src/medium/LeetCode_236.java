package medium;

import structure.TreeNode;

public class LeetCode_236 {

    public TreeNode solution(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) return null;
        if (root == p || root == q) return root;
        //寻找qp两个节点，这两个节点可能不存在，当其中一个不存在时，另一个就是自己的父节点
        TreeNode left = solution(root.left, p, q);
        TreeNode right = solution(root.right, p, q);
        //当二者都存在的时候
        if (left != null && right != null) {
            return root;
        } else if (left != null) {
            return left;
        } else if (right != null) {
            return right;
        }
        return null;
    }
}
