package structure;


/**
 * 单向链表节点
 */
public class ListNode {

    private ListNode next;

    private int value;

    public ListNode getNext() {
        return next;
    }

    public void setNext(ListNode next) {
        this.next = next;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
