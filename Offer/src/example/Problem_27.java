package example;

import structure.TreeNode;

/**
 * 二叉树镜像
 * 给出一棵二叉树，将其反转为它的镜像
 */
public class Problem_27 {

    /**
     * 二叉树反转
     * @param root
     */
    public void solution(TreeNode root) {
        if (root == null) {
            return;
        }
        exchange(root);
        solution(root.leftNode);
        exchange(root.rightNode);
    }

    /**
     * 交换二者
     * @param root
     * @return
     */
    public TreeNode exchange(TreeNode root) {
        if (root == null) {
            return null;
        }
        if (root.leftNode != null || root.rightNode != null) {
            TreeNode temp = root.leftNode;
            root.leftNode = root.rightNode;
            root.rightNode = temp;
        }
        return root;
    }
}
