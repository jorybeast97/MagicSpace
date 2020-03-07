package medium;

import java.util.*;

public class LeetCode_49 {
    public List<List<String>> groupAnagrams(String[] strs) {
        if (strs.length == 0) return new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();
        for (String s : strs) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String key = String.valueOf(chars);
            if (!map.containsKey(key)) {
                ArrayList<String> list = new ArrayList<>();
                list.add(key);
                map.put(key, list);
            }
            map.get(key).add(key);
        }
        return new ArrayList<>(map.values());
    }
}
