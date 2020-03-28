package hard;

import java.util.Arrays;

public class LeetCode_128 {
    public int longestConsecutive(int[] nums) {
        if (nums.length == 0) return 0;
        Arrays.sort(nums);
        int max = 1;
        int result = 1;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] - nums[i - 1] == 1 || nums[i] == nums[i - 1]) {
                max++;
                result = Math.max(max, result);
            }else {
                max = 1;
            }
        }
        return result;
    }
}
