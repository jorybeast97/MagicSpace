package medium;

import structure.TreeNode;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class LeetCode113 {
    List<List<Integer>> res = new ArrayList<>();
    public List<List<Integer>> pathSum(TreeNode root, int sum) {
        Deque<Integer> path = new LinkedList<>();
        helper(root, sum, path);
        return res;
    }

    public void helper(TreeNode node, int sum, Deque<Integer> path) {
        if (node == null) return;
        sum = sum - node.val;
        path.add(node.val);
        if (sum == 0 && node.left == null && node.right == null){
            res.add(new ArrayList<>(path));
            path.removeLast();
            return;
        }
        if (sum < 0) return;
        helper(node.left, sum, path);
        helper(node.right, sum, path);
        path.removeLast();
    }
}
