package medium;

import structure.ListNode;

import java.util.ArrayList;
import java.util.List;

public class LeetCode_86 {
    public ListNode partition(ListNode head, int x) {
        List<Integer> smlist = new ArrayList<>();
        List<Integer> biglist = new ArrayList<>();
        ListNode cur = head;
        while (cur != null) {
            if (cur.val < x) smlist.add(cur.val);
            cur = cur.next;
        }
        cur = head;
        while (cur != null) {
            if (cur.val >= x) biglist.add(cur.val);
            cur = cur.next;
        }
        for (int c : biglist) {
            smlist.add(c);
        }
        ListNode pre = new ListNode(-1);
        cur = pre;
        for (int t : smlist) {
            ListNode listNode = new ListNode(t);
            cur.next = listNode;
            cur = cur.next;
        }
        return pre.next;
    }
}
