package medium;

import structure.ListNode;
import structure.TreeNode;

import java.util.List;

/**
 * 有序链表转为高度平衡的二叉树
 */
public class LeetCode_109 {
    public TreeNode sortedListToBST(ListNode head) {
        ListNode cur = head;
        int length = 0;
        while (cur != null) {
            length++;
            cur = cur.next;
        }
        int[] arr = new int[length];
        int index = 0;
        while (head != null) {
            arr[index] = head.val;
            head = head.next;
            index++;
        }
        return helper(arr, 0, arr.length -1);
    }

    public TreeNode helper(int[] nums, int left, int right) {
        if (left > right) {
            return null;
        }
        //根节点
        int mun = (left + right) / 2;
        TreeNode node = new TreeNode(nums[mun]);
        node.left = helper(nums, left, mun - 1);
        node.right = helper(nums, mun + 1, right);
        return node;
    }
}
