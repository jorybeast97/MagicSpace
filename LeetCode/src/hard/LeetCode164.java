package hard;

public class LeetCode164 {

    public int maximumGap(int[] nums) {
        if (nums.length <= 1) return 0;
        sort(nums, 0, nums.length - 1);
        int res = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            res = Math.max(res, nums[i + 1] - nums[i]);
        }
        return res;
    }

    public void sort(int[] nums, int left, int right) {
        if (left > right) return;
        int temp = nums[left];
        int i = left;
        int j = right;
        while (left < right) {
            while (left < right && nums[i] <= temp) i++;
            while (left < right && nums[j] >= temp) j--;
            if (left < right) {
                int k = nums[i];
                nums[i] = nums[j];
                nums[j] = k;
            }
        }
        int t = nums[i];
        nums[i] = temp;
        nums[left] = t;
        sort(nums, left, i);
        sort(nums, i + 1, right);
    }
}
