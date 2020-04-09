package medium;

import java.util.HashMap;

public class LeetCode_260 {
    public int[] singleNumber(int[] nums) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i : nums) {
            if (map.containsKey(i)) {
                map.put(i, map.get(i) + 1);
            }else {
                map.put(i, 1);
            }
        }
        int index = 0;
        int[] result = new int[2];
        for (int i : nums) {
            if (map.get(i) == 1) {
                result[index] = i;
                index++;
            }
        }
        return result;
    }
}
