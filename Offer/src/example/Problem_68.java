package example;

import structure.TreeNode;

/**
 * 二叉树两个节点的最近公共节点
 */
public class Problem_68 {

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) {
            return root;
        }
        if (root.value > p.value && root.value > q.value) {
            return lowestCommonAncestor(root.leftNode, p, q);
        }
        if (root.value < p.value && root.value < q.value) {
            return lowestCommonAncestor(root.rightNode, p, q);
        }
        return root;
    }
}
