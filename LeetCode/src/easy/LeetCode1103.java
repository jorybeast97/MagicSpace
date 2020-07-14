package easy;

public class LeetCode1103 {
    public int[] distributeCandies(int candies, int num_people) {
        int[] nums = new int[num_people];
        int index = 0;
        int cost = 1;
        while (candies > 0) {
            if (index == nums.length-1 && candies > 0) index = 0;
            if (candies - cost <= 0) cost = candies;
            nums[index] = nums[index] + cost;
            cost++;
            index++;
            candies = candies - cost;
        }
        return nums;
    }
}
