package hard;

import structure.ListNode;
import structure.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class LeetCode99 {
    List<TreeNode> list = new ArrayList<>();
    public void recoverTree(TreeNode root) {
        dfs(root);
        TreeNode x = null;
        TreeNode y = null;
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).val > list.get(i + 1).val) {
                y = list.get(i + 1);
                if (x == null) x = list.get(i);
            }
        }

        if (x != null && y != null) {
            int tmp = x.val;
            x.val = y.val;
            y.val = tmp;
        }
    }

    public void dfs(TreeNode root) {
        if (root == null) return;
        dfs(root.left);
        list.add(root);
        dfs(root.right);
    }
}
