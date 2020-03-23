package easy;

import structure.ListNode;

public class LeetCode_876 {

    /**
     * 现计算链表长度,然后让一个指针走length/2,然后两个指针同时走
     * 后面的指针就是中点
     * @param head
     * @return
     */
    public ListNode solution(ListNode head) {
        if (head.next == null || head == null) return head;
        if (head.next.next == null) return head.next;
        int length = 1;
        ListNode numCount = head;
        while (numCount != null) {
            numCount = numCount.next;
            length++;
        }
        numCount = head;
        for (int i = 0; i < length / 2; i++) {
            numCount = numCount.next;
        }
        ListNode pre = head;
        while (numCount != null) {
            pre = pre.next;
            numCount = numCount.next;
        }
        return pre;
    }

    public ListNode solutionTwo(ListNode head) {
        if (head.next == null || head == null) return head;
        if (head.next.next == null) return head.next;
        ListNode slowPoint = head;
        ListNode fastPoint = head;
        while (fastPoint.next != null) {
            fastPoint = fastPoint.next.next;
            slowPoint = slowPoint.next;
        }
        return slowPoint;
    }
}
