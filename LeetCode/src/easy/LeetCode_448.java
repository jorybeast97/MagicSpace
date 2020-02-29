package easy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeetCode_448 {



    public List<Integer> findDisappearedNumbers(int[] nums) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            int index = Math.abs(nums[i])-1;
            if (nums[index] > 0) {
                nums[index] = nums[index] * -1;
            }
        }
        for (int t : nums) {
            if (t > 0){
                list.add(t);
            }
        }
        return list;
    }
}
