package easy;

import structure.TreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 中序遍历或者使用链表存储
 */
public class LeetCode_interview54 {
    int res = 0;
    public int kthLargest(TreeNode root, int k) {
        bianli(root, k);
        return res;
    }

    /**
     * 右左根遍历,顺序就是最大到最小
     * @param root
     * @param k
     */
    public void bianli(TreeNode root, int k) {
        if (root == null) return;
        bianli(root.right, k);
        if (k == 0) return;
        if (k == 1) res = root.val;
        bianli(root.left, k);
    }
}
