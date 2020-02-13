package easy;

public class LeetCode_680 {

    public boolean validPalindrome(String s) {
        char[] chars = s.toCharArray();
        int i = 0 ;
        int j = chars.length - 1;
        while (i <= j) {
            if (chars[i] != chars[j]) {
                return judge(s, i++, j) || judge(s, i, j--);
            }
        }
        return true;
    }

    public boolean judge(String s1, int start, int end) {
        while (start < end) {
            if (s1.toCharArray()[start++] == s1.toCharArray()[end--]) {
                return true;
            }
        }
        return false;
    }
}
