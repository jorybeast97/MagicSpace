package example;

import structure.ListNode;

import java.util.LinkedList;

/**
 * 合并两个排序链表
 */
public class Problem_25 {

    public LinkedList solution_1(ListNode listNode1, ListNode listNode2) {
        LinkedList linkedList = new LinkedList();
        while (listNode1 != null && listNode2 != null) {
            if (listNode1.getValue() <= listNode2.getValue()) {
                linkedList.add(listNode1.getValue());
                listNode1 = listNode1.next;
            }else {
                linkedList.add(listNode2.value);
                listNode2 = listNode2.next;
            }
        }
        return linkedList;
    }

}
