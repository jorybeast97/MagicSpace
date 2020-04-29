package medium;

import structure.TreeNode;

import java.util.*;

public class LeetCode_103 {
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new LinkedList<>();
        if (root == null) return result;
        Queue<TreeNode> queue = new LinkedList<>();
        Stack<TreeNode> stack = new Stack<>();
        int tier = 1;
        queue.add(root);
        while (!queue.isEmpty()) {
            List<Integer> list = new LinkedList<>();
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                list.add(node.val);
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
                if (tier % 2 == 0) stack.push(node);
            }
            if (tier % 2 == 0) {
                list.clear();
                while (!stack.isEmpty()) list.add(stack.pop().val);
            }
            result.add(list);
            tier++;
        }
        return result;
    }
}
