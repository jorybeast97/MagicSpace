package example;

import structure.ListNode;

/**
 * 反转链表
 * 定义一个函数，输入一个链表的头节点，反转该链表并输入反转后链表的头部。
 */
public class Problem_24 {

    /**
     * 迭代法
     * @param list
     * @return
     */
    public ListNode solution_1(ListNode list) {
        ListNode preNode = null;
        ListNode nextNode = null;
        ListNode currentNode = list;

        while (currentNode != null) {
            nextNode = currentNode.getNext();
            currentNode.setNext(preNode);
            preNode = currentNode;
            currentNode = nextNode;
        }
        return currentNode;

    }

    /**
     * 递归法
     * @param head
     * @return
     */
    public ListNode solution_2(ListNode head){
        if (head == null || head.getNext() == null) {
            return head;
        }
        ListNode temp = head.getNext();
        ListNode newHead = solution_2(head.getNext());
        temp.setNext(head);
        head.setNext(null);
        return newHead;
    }
}
