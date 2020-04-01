package medium;

import structure.ListNode;

public class leetCode_92 {
    public ListNode reverseBetween(ListNode head, int m, int n) {
        if (head == null) return null;
        ListNode l0 = new ListNode(0);
        l0.next = head;
        ListNode pre = l0;
        for (int i = 1; i < m; i++) {
            pre = pre.next;
        }
        ListNode cur = pre.next;
        //头插法,每次都把cur后面的节点插入pre.next
        for (int i = m; i < n; i++) {
            ListNode next = cur.next;
            //将当前指针指向next后一个
            cur.next = next.next;
            //next的指针指向pre的下一个,这一步是关键,而非指向cur
            next.next = pre.next;
            //将pre指向next,完成头插
            pre.next = next;
        }
        return l0.next;

    }


}
