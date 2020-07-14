package easy;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import java.util.*;

public class LeetCode349 {
    public int[] intersection(int[] nums1, int[] nums2) {
        List<Integer> list = new ArrayList<>();
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums2.length; i++) {
            if (map.containsKey(nums2[i])) map.put(nums2[i], map.get(nums2[i]) + 1);
            else map.put(nums2[i], 1);
        }
        for (int i = 0; i < nums1.length; i++) {
            if (map.containsKey(nums1[i]) && map.get(nums1[i]) > 0) {
                list.add(nums1[i]);
                map.put(nums1[i], map.get(nums1[i]) - 1);
            }
        }
        int[] res = new int[list.size()];
        for (int i = 0; i < res.length; i++) res[i] = list.get(i);
        return res;
    }
}
