package medium;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeetCode_40 {
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<Integer> path = new ArrayList<>();
        List<List<Integer>> result = new ArrayList<>();
        dp(result, path, candidates, 0, target);
        return result;
    }

    /**
     * 还有就是不能有重复的解， 可以考虑排序 candidates 遍历时和前一个兄弟节点比较， 相同的则排除。
     * 去除重复的解， 这种理论依据是排序过的数组， 如果非起始分支， 并且数值与前一个分支相同，
     * 则此分支可以剪枝， 因为前一个分支的数据遍历范围是大于此分支的， 如果还相同，
     * 那此份之属于与前一个分支肯定是相同的， 这也是回溯算法剪枝的常用套路之一
     * @param result
     * @param path
     * @param candidates
     * @param start
     * @param target
     */
    public void dp(List<List<Integer>> result,List<Integer> path, int[] candidates, int start, int target) {
        if (target == 0) {
            result.add(new ArrayList<>(path));
        }
        for (int i = start; i < candidates.length; i++) {
            int t = target - candidates[i];
            //第一次剪枝,剪除不符合规则的数据
            if (t<0) break;
            //第二次剪枝,取出重复的数据，例如两个[1,2,5]
            if (i>start && candidates[i] == candidates[i-1]) continue;
            path.add(candidates[i]);
            dp(result, path, candidates, start + 1, t);
            path.remove(path.size() - 1);
        }
    }
}
