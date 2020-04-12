package medium;

import java.util.HashMap;

public class LeetCode_1347 {
    public int minSteps(String s, String t) {
        HashMap<Character,Integer> map = new HashMap<>();
        if (s.length() != t.length()) return -1;
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if (map.containsKey(c)) map.put(c, map.get(c) + 1);
            else map.put(c, 1);
        }
        char[] arr = t.toCharArray();
        int result = 0;
        for (char c : arr) {
            if (!map.containsKey(c)) {
                result++;
            }
            else {
                if (map.get(c) == 0) result++;
                if (map.get(c) > 0) map.put(c, map.get(c) - 1);
            }
        }
        return result;
    }
}
