package medium;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class LeetCode_442 {
    public List<Integer> findDuplicates(int[] nums) {
        Set<Integer> set = new HashSet<>();
        List<Integer> list = new LinkedList<>();
        for (int t : nums) {
            if (set.contains(t)) list.add(t);
            else set.add(t);
        }
        return list;
    }

    /**
     * 每次将对应index上的数改为负数,出现两次的数对应的索引上的数必然事正数
     * @param nums
     * @return
     */
    public List<Integer> solution(int[] nums) {
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < nums.length; i++) {
            int index = nums[i]-1;
            nums[index] = -nums[index];
        }
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > 0) list.add(i + 1);
        }
        return list;
    }

}
