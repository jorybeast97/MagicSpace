package hard;

import structure.ListNode;

import java.util.ArrayList;
import java.util.List;

public class LeetCode_23 {

    public ListNode mergeKLists(ListNode[] lists) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < lists.length; i++) {
            ListNode head = lists[i];
            while (head != null) {
                list.add(head.val);
                head = head.next;
            }
        }
        list.sort((o1, o2) -> {
            return o1 - o2;
        });
        ListNode pre = new ListNode();
        ListNode cur = pre;
        for (Integer integer : list) {
            cur.next = new ListNode(integer);
            cur = cur.next;
        }
        return pre.next;
    }


}
