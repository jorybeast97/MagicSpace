package example;

import structure.ComplexNode;

import java.util.HashMap;

/**
 * 复杂链表的复制
 */
public class Problem_35 {

    /**
     * 通过HashMap空间换时间
     * @param head
     * @return
     */
    public ComplexNode solution(ComplexNode head) {
        if (head == null) {
            return null;
        }
        ComplexNode node = new ComplexNode();
        ComplexNode currentNode = node;
        ComplexNode t = head;
        HashMap<ComplexNode, ComplexNode> map = new HashMap<>();
        while (head != null) {
            ComplexNode newNode = new ComplexNode();
            node.next = newNode;
            newNode.value = head.value;
            map.put(head, newNode);
            head = head.next;
            node = node.next;
        }

        currentNode = currentNode.next;
        ComplexNode res = currentNode;
        while (t != null) {
            ComplexNode c = map.get(t.other);
            currentNode.other = c;
            t = t.next;
            currentNode = currentNode.next;
        }
        return res;
    }
}
