package easy;

public class LeetCode1295 {
    public int findNumbers(int[] nums) {
        int res = 0;
        for (int c : nums) {
            if (helper(c)) res++;
        }
        return res;
    }

    boolean helper(int t) {
        String s = String.valueOf(t);
        if (s.length() % 2 == 0) return true;
        return false;
    }
}


