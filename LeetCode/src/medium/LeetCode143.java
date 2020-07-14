package medium;

import structure.ListNode;

import java.util.Deque;
import java.util.LinkedList;


public class LeetCode143 {
    public void reorderList(ListNode head) {
        Deque<ListNode> list = new LinkedList<>();
        ListNode cur = head;
        while (cur != null) {
            list.add(cur);
            cur = cur.next;
        }
        while (cur.next != null) {
            ListNode next = cur.next;
            ListNode temp = list.removeLast();
            cur.next = temp;
            temp.next = next;
            cur = cur.next.next;
        }
    }
}
