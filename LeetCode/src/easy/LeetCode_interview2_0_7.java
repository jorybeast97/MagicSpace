package easy;

import structure.ListNode;

import java.util.HashSet;
import java.util.Set;

public class LeetCode_interview2_0_7 {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode result = null;
        Set<ListNode> set = new HashSet<>();
        ListNode curA = headA;
        while (curA != null) {
            set.add(curA);
            curA = curA.next;
        }
        ListNode curB = headB;
        while (curB != null) {
            if (set.contains(curB)) {
                result = curB;
                break;
            }
            curB = curB.next;
        }
        return result;
    }
}
