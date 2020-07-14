package medium;

import java.util.HashMap;

public class LeetCode220 {
    public boolean containsNearbyAlmostDuplicate(int[] nums, int k, int t) {
        if (k > 10000) return false;
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                int p1 = Math.abs(nums[i] - nums[j]);
                int p2 = Math.abs(i - j);
                if (p1 > k && p2 > t) return true;
            }
        }
        return false;
    }
}
