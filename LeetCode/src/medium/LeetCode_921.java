package medium;

public class LeetCode_921 {

    public static void main(String[] args) {
        System.out.println(new LeetCode_921().minAddToMakeValid("()))(("));

    }
    public int minAddToMakeValid(String S) {
        char[] chars = S.toCharArray();
        int target = 0;
        int right = 0;
        for (char c : chars) {
            if (c == '(') target++;
            if (c == ')') target--;
            if (target < 0) {
                right++;
                target = 0;
            }
        }
        return target+right;
    }
}
