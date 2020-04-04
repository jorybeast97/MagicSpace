package medium;

import com.sun.deploy.util.StringUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class LeetCode_131 {
    public List<List<String>> partition(String s) {
        List<List<String>> res = new ArrayList<>();
        if (s.length() == 0 || s==null) return res;
        Deque<String> strings = new ArrayDeque<>();
        help(s, 0, s.length(), strings, res);
        return res;
    }

    public void help(String s, int start, int length,
                     Deque<String> path, List<List<String>> result) {
        if (start == length - 1) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = start; i < length; i++) {
            if (!judge(s,start,i)) continue;
            path.addLast(s.substring(start, i + 1));
            help(s, i + 1, length, path, result);
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
