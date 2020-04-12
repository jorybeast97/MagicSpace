package easy;

import java.util.ArrayList;
import java.util.List;

public class LeetCode_590 {
    List<Integer> result = new ArrayList<>();
    public List<Integer> postorder(Node root) {
        addVal(root, result);
        return result;
    }

    public void addVal(Node root, List<Integer> list) {
        if (root == null) return;
        list.add(root.val);
        for (Node n : root.children) {
            addVal(n, list);
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
}

