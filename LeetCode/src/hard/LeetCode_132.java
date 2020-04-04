package hard;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class LeetCode_132 {

    /**
     * 动态规划方法参考LeetCode132第一个题解
     * @param args
     */
    public static void main(String[] args) {
        String s = "fifgbeajcacehiicccfecbfhhgfiiecdcjjffbghdidbhbdbfbfjccgbbdcjheccfbhafehieabbdfeigbiaggchaeghaijfbjhi";
        System.out.println(new LeetCode_132().minCut(s));

    }

    /***
     * 以下内容为暴力递归,求解所有的回文子串,然后比较其中list最少的那个就是需要分割次数最小的
     * @param s
     * @return
     */
    public int minCut(String s) {
        if (judge(s,0,s.length()-1)) return 0;
        List<List<String>> lists = getList(s);
        int result = Integer.MAX_VALUE;
        for (List<String> list : lists) {
            result = Math.min(result,list.size());
        }
        return result - 1;
    }

    public List<List<String>> getList(String s) {
        List<List<String>> list = new ArrayList<>();
        Deque<String> path = new ArrayDeque<>();
        helper(s, 0, s.length(), path, list);
        return list;
    }

    public void helper(String s, int start, int length,
                       Deque<String> path, List<List<String>> result) {
        if (start == length) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = start; i < length; i++) {
            if (!judge(s,start,i)) continue;
            path.addLast(s.substring(start, i + 1));
            helper(s, i + 1, length, path, result);
            path.removeLast();
        }
    }

    public boolean judge(String s,int left,int right) {
        while (left < right) {
            if (s.charAt(left) == s.charAt(right)) {
                left++;
                right--;
            }else {
                return false;
            }
        }
        return true;
    }
}
