package medium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LeetCode_763 {
    HashMap<Character,Integer> map = new HashMap<>();
    public List<Integer> partitionLabels(String S) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < S.length(); i++) {
            map.put(S.charAt(i), i);
        }
        int pre = 0;
        int fast = 0;
        for (int i = 0; i < S.length(); i++) {
            fast = Math.max(map.get(S.charAt(i)), fast);
            if (i == fast) {
                result.add(fast - pre +1);
                pre = i +1 ;
            }
        }
        return result;
    }
}
