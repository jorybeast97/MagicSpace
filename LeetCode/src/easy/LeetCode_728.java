package easy;

import java.util.ArrayList;
import java.util.List;

public class LeetCode_728 {

    public static void main(String[] args) {
        System.out.println(new LeetCode_728().judge(128));

    }
    public List<Integer> selfDividingNumbers(int left, int right) {
        List<Integer> result = new ArrayList<>();
        if (left > right) return result;
        for (int i = left; i <= right; i++) {
            if (judge(i)) result.add(i);
        }
        return result;
    }

    public boolean judge(int val) {
        String s = String.valueOf(val);
        for (char c : s.toCharArray()) {
            System.out.println(c);
            if (c == '0') {
                return false;
            }
            if (val % Integer.valueOf(String.valueOf(c)) != 0) {
                return false;
            }
        }
        return true;
    }
}
