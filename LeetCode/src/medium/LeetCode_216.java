package medium;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class LeetCode_216 {

    List<Integer> list;
    List<List<Integer>> res;

    public List<List<Integer>> combinationSum3(int k, int n) {
        list = new ArrayList<>();
        res = new ArrayList<>();
        dfs(1, n, k);
        return res;
    }

    private void dfs(int u, int n, int k) {
        if (k == 0 && n == 0) {
            res.add(new ArrayList<>(list));
            return;
        }
        if (n <= 0 || k <= 0) return;
        for (int i = u; i <= 9; i++) {
            list.add(i);
            dfs(i + 1, n - i, k - 1);
            list.remove(list.size() - 1);
        }
    }

}
