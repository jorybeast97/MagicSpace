package treePro;


import structure.TreeNode;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

public class basicErgodic {

    /**
     * 先序遍历 , 根左右
     * @param root
     */
    public void preorderingErgodic(TreeNode root) {
        if (root == null) {
            return;
        }
        System.out.println(root.getValue());
        preorderingErgodic(root.getLeftNode());
        preorderingErgodic(root.getRightNode());
    }

    /**
     * 中序遍历，左根右
     * @param root
     */
    public void middleErgodic(TreeNode root) {
        if (root == null) {
            return;
        }
        middleErgodic(root.getLeftNode());
        System.out.println(root.getValue());
        middleErgodic(root.getRightNode());
    }

    /**
     * 后序遍历，左右根
     * @param root
     */
    public void lastErgodic(TreeNode root) {
        if (root == null) {
            return;
        }
        lastErgodic(root.getLeftNode());
        lastErgodic(root.getRightNode());
        System.out.println(root.getValue());
    }

    //非递归方法实现

    /**
     * 先序遍历 , 根左右
     * 其余遍历方式相同，调整顺序即可
     * 同时该方法也是深度优先遍历
     * @param root
     */
    public void preOrder(TreeNode root) {
        if (root == null) {
            return;
        }
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.empty()) {
            TreeNode node = stack.pop();
            System.out.println(node);
            //因为栈原因，所以先进后出，左边的要后进入
            if (node.getRightNode() != null) {
                stack.push(node.getRightNode());
            }
            if (node.getLeftNode() != null) {
                stack.push(node.getLeftNode());
            }
        }
    }

    /**
     * 二叉树广度优先遍历方法 ， 利用队列
     *
     * @param root
     */
    public void breadthOrder(TreeNode root) {
        if (root == null) {
            return;
        }
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node.getLeftNode() != null) {
                queue.add(node.getLeftNode());
            }
            if (node.getRightNode() != null) {
                queue.add(node.getRightNode());
            }
            System.out.println(node.getValue());
        }
    }


}
