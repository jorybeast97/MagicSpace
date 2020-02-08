package example;

import structure.ListNode;

import java.util.Stack;

/**
 * 反向打印链表
 * 有很多实现方法
 */
public class Problem_6 {

    /**
     * 递归法
     * @param listNode
     */
    public void solution(ListNode listNode) {
        if (listNode == null) {
            return;
        }
        solution(listNode.next);
        System.out.println(listNode);
    }

    /**
     * 栈方法
     * @param listNode
     */
    public void solution_2(ListNode listNode) {
        Stack<ListNode> stack = new Stack<>();
        while (listNode != null) {
            stack.push(listNode);
            listNode = listNode.next;
        }
        while (!stack.empty()) {
            System.out.println(stack.pop());
        }
    }
}
