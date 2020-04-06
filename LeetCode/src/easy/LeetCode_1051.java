package easy;

import java.util.Arrays;

public class LeetCode_1051 {

    public int heightChecker(int[] heights) {
        int[] nums = heights.clone();
        int result = 0;
        Arrays.sort(heights);
        for (int i = 0; i < heights.length; i++) {
            if (nums[i] != heights[i]) result++;
        }
        return result;
    }
}
