package medium;

import java.math.BigDecimal;
import java.util.Arrays;

public class LeetCode179 {
    public String largestNumber(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i; j < nums.length; j++) {
                if (helper(nums[i], nums[j])) {
                    int k = nums[i];
                    nums[i] = nums[j];
                    nums[j] = k;
                }
            }
        }
        String s = "";
        for (int c : nums) {
            s = s + c;
        }
        if (s.charAt(0) == '0') return "0";
        return s;
    }

    public boolean helper(int pre, int last) {
        String k1 = pre + "" + last;
        String k2 = last + "" + pre;
        if (Long.valueOf(k1) > Long.valueOf(k2)) return false;
        return true;
    }
}
