package easy;

import structure.Utils;

import java.util.ArrayList;
import java.util.List;

public class LeetCode_46 {

    public static void main(String[] args) {
        int[] arr = Utils.getArray(5, 4);
        LeetCode_46 leetCode_46 = new LeetCode_46();
        List<List<Integer>> res = leetCode_46.permute(arr);
        for (List t : res) {
            System.out.println(t);
        }
    }

    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        //创建标记数组
        boolean[] isUsed = new boolean[nums.length];
        List<Integer> path = new ArrayList<>();
        dfs(nums, nums.length, 0, isUsed, path, result);
        return result;
    }

    private void dfs(int[] nums, int length, int depth,boolean[] isUsed,
                     List<Integer> path, List<List<Integer>> res) {
        if (depth == length) {
            res.add(new ArrayList<>(path));
            return;
        }
        for (int i = 0; i < length; i++) {
            if (!isUsed[i]) {
                path.add(nums[i]);
                isUsed[i] = true;
                dfs(nums, length, depth+1, isUsed, path, res);
                isUsed[i] = false;
                path.remove(depth);
            }
        }
    }
}
