package medium;

import structure.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class LeetCode_230 {

    int num = 0;
    int result = 0;
    public int kthSmallest(TreeNode root, int k) {
        num = k;
        return result;

    }

    public void help(TreeNode root) {

        if (root == null || num < 0) return;
        help(root.left);
        num--;
        if (num == 0) {
            result = root.val;
        }
        help(root.right);

    }
}
