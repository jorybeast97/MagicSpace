package easy;

import structure.ListNode;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class LeetCode_141 {
    public boolean hasCycle(ListNode head) {
        LinkedHashSet linkedHashSet = new LinkedHashSet();

        HashSet<ListNode> set = new HashSet<>();
        while (head != null) {
            if (set.contains(head)) {
                return true;
            }
            set.add(head);
            head = head.next;
        }
        return false;
    }
}
