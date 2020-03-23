package medium;

import java.util.Arrays;

public class LeetCode_16 {
    public int threeSumClosest(int[] nums, int target) {
        if (nums == null || nums.length <3) return Integer.MAX_VALUE;
        int result = nums[0] + nums[1] + nums[2];
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 2; i++) {
            int pre = i + 1;
            int last = nums.length - 1;
            while (pre < last) {
                int t = nums[i] + nums[pre] + nums[last];
                if (Math.abs(target - t) < Math.abs(target - result)) {
                    result = t;
                }
                if (t > target) last--;
                else if (t < target) pre++;
                else return result;
            }
        }
        return result;
    }
}
