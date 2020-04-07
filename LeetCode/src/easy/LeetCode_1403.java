package easy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LeetCode_1403 {
    /**
     * 排序+去重
     * @param nums
     * @return
     */
    public List<Integer> minSubsequence(int[] nums) {
        Integer[] n = new Integer[nums.length];
        int sum = 0;
        for(int i = 0;i < nums.length;i++){
            n[i] = nums[i];
            sum += nums[i];
        }
        Arrays.sort(n, Collections.reverseOrder());

        int i = 0;
        int count = 0;
        List<Integer> list = new ArrayList<>();
        while(i<nums.length && count <= sum - count){
            count += n[i];
            list.add(n[i++]);
        }

        return list;

    }
}
