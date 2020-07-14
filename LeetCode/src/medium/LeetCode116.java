package medium;

import java.util.LinkedList;
import java.util.Queue;

public class LeetCode116 {

    class Node {
        public int val;
        public medium.Node left;
        public medium.Node right;
        public medium.Node next;

        public Node() {}

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, medium.Node _left, medium.Node _right, medium.Node _next) {
            val = _val;
            left = _left;
            right = _right;
            next = _next;
        }
    }




}



