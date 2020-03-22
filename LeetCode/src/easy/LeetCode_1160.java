package easy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LeetCode_1160 {

    public int countCharacters(String[] words, String chars) {
        int result = 0;
        for (String s : words) {
            if (hasWord(s.toCharArray(), chars)) {
                result = result + s.length();
            }
        }
        return result;
    }

    public boolean hasWord(char[] arr, String string) {
        char[] chars = string.toCharArray();
        HashMap<Character, Integer> map = new HashMap<>();
        for (char c : chars) {
            if (map.containsKey(c)) {
                map.put(c, map.get(c) + 1);
            } else {
                map.put(c, 1);
            }
        }
        for (char k : arr) {
            if (map.containsKey(k) && map.get(k) > 0) {
                map.put(k, map.get(k) - 1);
            } else {
                return false;
            }
        }
        return true;
    }
}
