package easy;

import java.util.HashMap;
import java.util.Map;

public class LeetCode_1394 {
    public int findLucky(int[] arr) {
        Map<Integer, Integer> map = new HashMap<>(32);
        int result = 0;
        for (int c : arr) {
            if (map.containsKey(c)) {
                map.put(c, map.get(c) + 1);
            }else {
                map.put(c, 1);
            }
        }
        for (int c : arr) {
            if (map.containsKey(c) && map.get(c) == c) {
                result = Math.max(result, c);
            }
        }
        return result;
    }
}
