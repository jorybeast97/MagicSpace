package easy;

import java.util.List;

public class LeetCode_559 {

    public int maxDepth(Node root) {
        int max = 0;
        if (root == null) return 0;
        List<Node> list = root.children;
        for (int i = 0; i < list.size(); i++) {
            int depth = maxDepth(list.get(i));
            max = Math.max(max, depth);
        }
        return max + 1;
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

