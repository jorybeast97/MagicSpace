package InterViewCode;

import structure.ListNode;

public class InterViewOne {
    //3 4 5 6 1 2
    public static void main(String[] args) {
        int[] arr = {3, 4, 5, 6, 1, 2};
        int index = new InterViewOne().selectInArr(arr, 3);
        System.out.println(index);
    }

    public int select(int[] nums, int targer, int left, int right) {
        if (left > right || targer < nums[left] || targer > nums[right]) return -1;
        int mid = left + (right - left) / 2;
        if (nums[mid] > targer) return select(nums, targer, left, mid - 1);
        else if (nums[mid] < targer) return select(nums, targer, mid + 1, right);
        else return nums[mid];
    }

    public int selectInArr(int[] nums, int target) {
        int left = 0;
        int rigth = nums.length - 1;
        while (left <= rigth) {
            int mid = (left + rigth) / 2;
            if (nums[mid] == target) return mid;
            if (nums[mid] <= nums[rigth]){
                if (target >= nums[mid] && target <= nums[rigth]) left = mid + 1;
                else rigth = mid - 1;
            }
            else {
                if (target>=nums[left] && target <= nums[mid]) rigth = mid - 1;
                else left = mid + 1;
            }
        }
        return -1;
    }

    public ListNode kRe(ListNode head,int k) {
        if (head == null || head.next == null) return head;
        ListNode dummy = new ListNode(-1);
        ListNode next = null;
        ListNode cur = head;
        ListNode pre = dummy;
        int length = 0;
        while (head != null) {
            length++;
            head = head.next;
        }
        head = cur;
        for (int i = 0; i < length / k; i++) {
            for (int j = 0; i < k - 1; i++) {
                next = cur.next;
                cur.next = next.next;
                next.next = cur;
                pre.next = next;
            }
            pre = cur;
            cur = cur.next;
        }
        return dummy.next;
    }



}
