package example;

import structure.ListNode;

import java.util.HashSet;

/**
 * 两个量表的第一个公共节点
 */
public class Problem_52 {

    /**
     * 空间换时间
     * @param n1
     * @param n2
     * @return
     */
    public ListNode solution(ListNode n1, ListNode n2) {
        HashSet<ListNode> set = new HashSet<>();
        while (n1 != null) {
            set.add(n1);
            n1 = n1.next;
        }

        while (n2 != null) {
            if (set.contains(n2)) {
                return n2;
            }
            n2 = n2.next;
        }
        return null;
    }
}
