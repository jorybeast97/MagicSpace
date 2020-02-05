package example;

import structure.ListNode;

import java.util.HashSet;
import java.util.Set;

/**
 * 链表中环的入口节点
 * 如果一个链表中包含环，如何找出环的入口节点
 */
public class Problem_23 {

    /**
     * 借助HashSet来去重
     * @param list
     * @return
     */
    public ListNode solution_1(ListNode list) {
        Set<ListNode> set = new HashSet<>();
        set.add(list);
        while (list != null) {
            list = list.getNext();
            if (set.contains(list)) {
                return list;
            }
        }
        return null;
    }

    /**
     * 双指针快慢法，快的先走，如果又追上了慢的，则出现了重复，否则无环
     * @param list
     * @return
     */
    public ListNode solution_2(ListNode list) {
        ListNode fastNode = list;
        ListNode slowNode = list;
        while (fastNode != null && slowNode != slowNode) {
            if (fastNode == slowNode) {
                return fastNode;
            }
            slowNode = slowNode.getNext();
            fastNode = fastNode.getNext();
            if (fastNode != null) {
                fastNode = fastNode.getNext();
            }
        }
        return null;
    }
}
