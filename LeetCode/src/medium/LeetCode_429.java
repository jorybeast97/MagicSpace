package medium;

import structure.TreeNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LeetCode_429 {
    public List<List<Integer>> levelOrder(Node root) {
        List<List<Integer>> result = new LinkedList<>();
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            List<Integer> list = new LinkedList<>();
            for (int i = 0; i < queue.size(); i++) {
                Node node = queue.poll();
                list.add(node.val);
                List<Node> child = node.children;
                for (int t = 0; t < child.size(); t++) {
                    queue.add(child.get(t));
                }
            }
            result.add(list);
        }
        return result;
    }
}

class Node {
    public int val;
    public List<Node> children;

    public Node() {}

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, List<Node> _children) {
        val = _val;
        children = _children;
    }
};