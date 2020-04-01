package medium;

public class LeetCode_1111 {

    public int[] maxDepthAfterSplit(String seq) {
        int len = seq.length();
        int[] ans = new int[len];
        int depth = 0;
        for (int i = 0; i < len; i++) {
            if (seq.charAt(i) == '(') {
                depth++;
                if ((depth & 1) == 0) ans[i] = 1;
            } else {
                if ((depth & 1) == 0) ans[i] = 1;
                depth--;
            }
        }
        return ans;
    }

}
