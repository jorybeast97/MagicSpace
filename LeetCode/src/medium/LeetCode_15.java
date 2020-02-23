package medium;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LeetCode_15 {

    public static void main(String[] args) {
        int[] t = {-1, 0, 1, 2, -1, -4};
        List<List<Integer>> lists = threeSum(t);
        for (List a : lists) {
            System.out.println(a);
        }
    }

    public static List<List<Integer>> threeSum(int[] nums) {

        
        int start = 0;
        int end = nums.length -1;
        LinkedList<List<Integer>> res = new LinkedList();
        Arrays.sort(nums);
        for(int i = 0 ; i < nums.length /  2 ; i++){
            while(start < end){
                if(start == i) start++;
                if(end == i) start--;
                int temp = nums[start] + nums[end];
                if(temp == 0 - nums[i]){
                    LinkedList<Integer> l = new LinkedList();
                    l.add(nums[start]);
                    l.add(nums[end]);
                    l.add(nums[i]);
                    res.add(l);
                    end--;
                    start++;
                }
                if(temp > 0 - nums[i]) end--;
                if(temp < 0 - nums[i]) start++;
            }
            start = i;
            end = nums.length-1;
        }
        return res;
    }
}
