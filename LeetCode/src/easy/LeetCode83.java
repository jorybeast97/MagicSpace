package easy;

import structure.ListNode;

public class LeetCode83 {
    public ListNode deleteDuplicates(ListNode head) {
        if (head.next == null) return head;
        ListNode cur = head;
        ListNode last = head;
        while (cur != null) {
            while (last != null && cur.val == last.val) {
                last = last.next;
            }
            cur.next = last;
            cur = cur.next;
        }
        return head;
    }
}
