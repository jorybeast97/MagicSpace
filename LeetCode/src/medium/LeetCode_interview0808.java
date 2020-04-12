package medium;

import java.util.Set;

public class LeetCode_interview0808 {
    public String[] permutation(String S) {
        if (S.length() == 0) return new String[0];
        return new String[0];
    }

    public void helper(Set<String> set, boolean[] used, String cur,char[] chars) {
        if (cur.length() == chars.length) {
            set.add(cur);
        }
        for (int i = 0; i < used.length; i++) {
            if (!used[i]) {
                used[i] = true;
                helper(set, used, cur + chars[i], chars);
                used[i] = false;
            }
        }
    }
}
