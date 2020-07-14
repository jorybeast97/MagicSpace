package medium;

import structure.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LeetCode173 {

}

class BSTIterator {

    List<TreeNode> list;
    int index = 0;
    public BSTIterator(TreeNode root) {
        list = new ArrayList<>();
        helper(root);
    }

    /** @return the next smallest number */
    public int next() {
        int point = list.get(index).val;
        index++;
        return point;
    }

    /** @return whether we have a next smallest number */
    public boolean hasNext() {
        if (index == list.size()) return false;
        return true;
    }

    public void helper(TreeNode root) {
        if (root == null) return;
        helper(root.left);
        list.add(root);
        helper(root.right);
    }
}
