package hard;

import structure.ListNode;

public class LeetCode_25 {

    public ListNode reverseKGroup(ListNode head,int k) {
        ListNode l0 = new ListNode();
        l0.next = head;
        ListNode pre = l0;
        ListNode end = l0;
        while (end.next != null) {
            for (int i = 0; i < k && end != null; i++) end = end.next;
            if (end == null) break;
            ListNode start = pre.next;
            ListNode next = end.next;
            end.next = null;
            pre.next = reverse(start);
            start.next = next;
            pre = start;
            end = pre;
        }
        return l0.next;
    }

    /**
     * 反转链表
     * @param node
     * @return
     */
    public ListNode reverse(ListNode node) {
        ListNode pre = null;
        ListNode cur = node;
        while (cur != null) {
            ListNode next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        return pre;
    }
}
