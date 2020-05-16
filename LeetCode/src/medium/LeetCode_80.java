package medium;

import java.util.*;

public class LeetCode_80 {
    public int solution(int[] nums) {
        if (nums.length <= 2) return 2;
        int index = 2;
        for (int i = 2; i < nums.length; i++) {
            if (nums[i] != nums[i - 2]) {
                nums[index++] = nums[i];
            }
        }
        return index;
    }


    public int removeDuplicates(int[] nums) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int c : nums) {
            if (map.containsKey(c) && map.get(c)<2) map.put(c, map.get(c) + 1);
            if (!map.containsKey(c)) map.put(c, 1);
        }
        List<Integer> list = new ArrayList<>();
        Set<Map.Entry<Integer, Integer>> entries = map.entrySet();
        Iterator<Map.Entry<Integer, Integer>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            int index = entry.getValue();
            for (int i = 0; i < index; i++) list.add(entry.getKey());
        }
        list.sort((o1, o2) -> {
            return o1 - o2;
        });
        for (int i = 0; i < list.size(); i++) {
            nums[i] = list.get(i);
        }
        return list.size();
    }
}
