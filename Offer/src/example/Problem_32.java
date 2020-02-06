package example;

import structure.TreeNode;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 从上到下打印二叉树
 * <p>
 * 就是广度优先遍历
 */
public class Problem_32 {

    public void solution(TreeNode root) {
        if (root == null) {
            return;
        }
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node.leftNode != null) {
                queue.add(node.leftNode);
            }
            if (node.rightNode != null) {
                queue.add(node.rightNode);
            }
            System.out.println(node);
        }
    }

}
