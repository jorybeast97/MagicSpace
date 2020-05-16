package hard;

import structure.ListNode;

import java.util.ArrayList;
import java.util.List;

public class LeetCode_61 {


    /**
     * 笨办法,会超时
     * @param head
     * @param k
     * @return
     */
    public ListNode rotateRight(ListNode head, int k) {
        if (head == null) return head;
        List<Integer> list = new ArrayList<>();
        while (head != null) {
            list.add(head.val);
            head = head.next;
        }

        if (k > list.size()){
            while (true) {
                if (k < list.size()) break;
                k = k - list.size();
            }
        }
        ListNode pre = new ListNode(-1);
        ListNode cur = pre;
        for (int i = list.size() - k; i < list.size(); i++) {
            ListNode node = new ListNode(list.get(i));
            cur.next = node;
            cur = cur.next;
        }
        for (int i = 0; i < list.size() - k; i++) {
            ListNode node = new ListNode(list.get(i));
            cur.next = node;
            cur = cur.next;
        }
        return pre.next;

    }
}
