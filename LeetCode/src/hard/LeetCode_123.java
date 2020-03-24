package hard;

/**
 * 2次购买股票的最大利润
 * 将数据分段,分别求两段的最大利润
 */
public class LeetCode_123 {


    public int solution(int[] nums) {

        int max = 0;
        for (int i = 0; i < nums.length; i++) {
            int leftmax = getMax(nums, 0, i);
            int rightmax = getMax(nums, i + 1, nums.length - 1);
            max = Math.max(max, leftmax + rightmax);
        }
        return max;

    }

    public int getMax(int[] nums, int start, int end) {
        if (start>=end) return 0;
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (int i = start; i <= end; i++) {
            if (nums[i] < min) min = nums[i];
            max = Math.max(max, nums[i] - min);
        }
        return max;
    }
}
