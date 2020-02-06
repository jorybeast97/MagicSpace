package example;

import structure.TreeNode;

/**
 * 二叉搜索树和双向链表
 *
 * 使用中序遍历,保证从左端开始
 */
public class Problem_36 {

    private TreeNode pre = null;
    private TreeNode head = null;

    public TreeNode Convert(TreeNode root) {
        inOrder(root);
        return head;
    }

    private void inOrder(TreeNode node) {
        if (node == null)
            return;
        inOrder(node.leftNode);
        node.leftNode = pre;
        if (pre != null)
            pre.rightNode = node;
        pre = node;
        if (head == null)
            head = node;
        inOrder(node.rightNode);
    }

}
