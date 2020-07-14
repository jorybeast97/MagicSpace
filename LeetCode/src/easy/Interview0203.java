package easy;

import structure.ListNode;

public class Interview0203 {
    public void deleteNode(ListNode node) {
        ListNode pre = node;
        ListNode cur = node;
        while (pre.next != null) {
            pre = pre.next.next;
            cur = cur.next;
        }
        cur.next = cur.next.next;
    }
}
