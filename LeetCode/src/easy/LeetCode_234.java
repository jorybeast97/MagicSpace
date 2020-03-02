package easy;

import structure.ListNode;

import java.util.ArrayList;
import java.util.HashMap;

public class LeetCode_234 {
    public boolean isPalindrome(ListNode head) {
        if (head == null) {
            return false;
        }
        ArrayList<Integer> list = new ArrayList<>();
        while (head != null) {
            list.add(head.val);
            head = head.next;
        }
        boolean result = true;
        int start = 0;
        int end = list.size()-1;
        while (start < end) {
            if (!list.get(start).equals(list.get(end))) {
                result = false;
                break;
            }
            start++;
            end--;
        }
        return result;
    }
}
