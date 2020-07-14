package easy;

import java.util.HashMap;

public class LeetCode409 {
    public int longestPalindrome(String s) {
        HashMap<Character, Integer> map = new HashMap<>();
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if (map.containsKey(c)) map.put(c, map.get(c) + 1);
            else map.put(c, 1);
        }
        int sum = 0;
        boolean single = false;
        for (char c : chars) {
            if (map.containsKey(c) && map.get(c) % 2 == 0) {
                sum = sum + map.get(c);
                map.remove(c);
            } else if (map.containsKey(c) && map.get(c) % 2 == 1) {
                single = true;
            }
        }
        if (single) sum++;
        return sum;
    }
}
