package medium;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class LeetCode_77 {

    public static void main(String[] args) {

    }

    List<List<Integer>> result = new ArrayList<>();
    public List<List<Integer>> combine(int n, int k) {
        int[] nums = new int[n];
        for (int i = 0; i < nums.length; i++) nums[i] = i + 1;
        Deque<Integer> deque = new ArrayDeque<>();
        helper(deque, k, 0, nums.length, nums);
        return result;
    }

    public void helper(Deque<Integer> path, int k, int left, int right,int[] nums) {
        if (k == 0){
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = left; i < right; i++) {
            path.addLast(nums[i]);
            helper(path, k - 1, i + 1, right, nums);
            path.removeLast();
        }
    }
}
