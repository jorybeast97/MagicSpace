package easy;

import structure.ListNode;

import java.util.List;
import java.util.Stack;

/**
 *
 */
public class LeetCode_2 {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode t1 = l1;
        ListNode t2 = l2;
        ListNode res = l1;
        //补零
        while (l1 != null || l2 != null) {
            if (l1 == null){
                ListNode n = new ListNode(0);
                l1.next = n;
                l1 = l1.next;
                l2 = l2.next;
            } else if (l2 == null) {
                ListNode node = new ListNode(0);
                l2.next = node;
                l2 = l2.next;
                l1 = l1.next;
            } else {
                l1 = l1.next;
                l2 = l2.next;
            }
        }
        while (t1 != null && t2 != null) {
            if ((t1.val+t2.val) >= 10){
                int k = t1.val + t2.val;
                t1.val = k % 10;
                t1.next.val = k / 10;
            }
        }
        return res;
    }


}
