package easy;

import structure.ListNode;

public class LeetCode203 {
    public ListNode removeElements(ListNode head, int val) {
        ListNode res = new ListNode(-1);
        ListNode pre = res;
        while (head != null) {
            if (head.val != val) {
                ListNode node = new ListNode(head.val);
                res.next = node;
                res = res.next;
            }
            head = head.next;
        }
        return pre.next;
    }
}
