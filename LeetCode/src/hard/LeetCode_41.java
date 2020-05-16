package hard;

import java.util.Arrays;
import java.util.HashSet;

public class LeetCode_41 {
    public int firstMissingPositive(int[] nums) {
        HashSet<Integer> set = new HashSet<>();
        for (int c : nums) set.add(c);
        for (int i = 1; i < nums.length + 1; i++) {
            if (!set.contains(i)) return i;
        }
        return nums.length + 1;
    }
}
