package medium;

import structure.TreeNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LeetCode_98 {
    List<Integer> list = new LinkedList<>();
    public boolean isValidBST(TreeNode root) {
        helper(root);
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) > list.get(i+1)) return false;
        }
        return true;
    }
    public void helper(TreeNode node) {
        if (node == null) return;
        helper(node.left);
        list.add(node.val);
        helper(node.right);
    }


    int pre = Integer.MIN_VALUE;

    public boolean soluiton(TreeNode root) {
        if (root == null) return true;
        if (!soluiton(root.left)) return false;
        if (root.val <= pre) return false;
        pre = root.val;
        return soluiton(root.right);
    }

}
