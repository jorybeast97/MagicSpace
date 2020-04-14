package easy;

import java.util.*;

public class LeetCode_1207 {

    public boolean uniqueOccurrences(int[] arr) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int c : arr) {
            if (map.containsKey(c)) map.put(c, map.get(c) + 1);
            else map.put(c, 1);
        }
        Set<Map.Entry<Integer,Integer>> entry = map.entrySet();
        Iterator<Map.Entry<Integer, Integer>> iterator = entry.iterator();
        int[] temp = new int[entry.size()];
        int index = 0;
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> a = iterator.next();
            temp[index] = a.getValue();
            index++;
        }
        Arrays.sort(temp);
        for (int i = 0; i < temp.length - 1; i++) {
            if (temp[i] == temp[i+1]) return false;
        }
        return true;
    }
}
