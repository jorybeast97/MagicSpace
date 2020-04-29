package medium;

import java.util.HashMap;

public class LeetCode_791 {
    public String customSortString(String S, String T) {
        HashMap<Character, Integer> map = new HashMap<>();
        char[] chars = S.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            map.put(chars[i], 0);
        }
        for (int i = 0; i < T.length(); i++) {
            if (map.containsKey(T.charAt(i))) {
                map.put(T.charAt(i), map.get(T.charAt(i)) + 1);
            }
        }
        StringBuffer sb = new StringBuffer();
        for (char c : chars) {
            int size = map.get(c);
            for (int i = 0; i < size; i++) {
                sb.append(c);
            }
        }
        for (int i = 0; i < T.length(); i++) {
            if (!map.containsKey(T.charAt(i))) {
                sb.append(T.charAt(i));
            }
        }
        return sb.toString();
    }
}
