package easy;

public class LeetCode1480 {
    public int[] runningSum(int[] nums) {
        if (nums == null) return nums;
        if (nums.length == 1) return nums;
        int[] res = new int[nums.length];
        res[0] = nums[0];
        for (int i = 1; i < nums.length; i++) {
            res[i] = res[i - 1] + nums[i];
        }
        return res;
    }
}
