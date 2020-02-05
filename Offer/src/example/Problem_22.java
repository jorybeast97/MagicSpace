package example;

import structure.ListNode;

/**
 * 链表中倒数第K个节点
 * 输入一个链表，输出该链表中倒数的第K个节点
 */
public class Problem_22 {

    /**
     * 最简单的解法，两次遍历，第一次找倒数第K个，第二次找结果
     * @param node
     * @param k
     * @return
     */
    public int solution_1(ListNode node , int k) {
        if (node == null) {
            return 0;
        }
        int length = 1;
        ListNode head = node;
        while (node != null) {
            node = node.getNext();
            length++;
        }
        int res = 0;
        for (int i = 0; i < length - k + 1; i++) {
            head = head.getNext();
            res = head.getValue();
        }
        return res;
    }

    /**
     * 双指针法，让二者之间相差
     * @param node
     * @param k
     * @return
     */
    public ListNode solution_2(ListNode node, int k) {
        if (node == null || k <= 0) {
            return null;
        }

        ListNode head = node;
        //先让前指针到达K处
        for (int i = 0; i < k - 1; i++) {
            node = node.getNext();
        }
        //当前指针到达时，则后指针正好在目标位置
        while (node != null) {
            head = head.getNext();
            node = node.getNext();
        }
        return head;
    }
}
