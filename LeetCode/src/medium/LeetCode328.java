package medium;

import structure.ListNode;

public class LeetCode328 {
    public ListNode oddEvenList(ListNode head) {
        if (head == null) return null;
        if (head.next == null) return head;
        ListNode dummy = new ListNode(-1);
        ListNode temp = dummy;
        ListNode cur = head;
        while (cur.next != null) {
            temp.next = new ListNode(cur.val);
            cur = cur.next.next;
            temp = temp.next;
        }
        cur = head.next;
        while (cur.next != null) {
            temp.next = new ListNode(cur.val);
            cur = cur.next.next;
            temp = temp.next;
        }
        return dummy.next;
    }
}
