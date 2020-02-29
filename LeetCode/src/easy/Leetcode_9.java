package easy;

public class Leetcode_9 {

    public static void main(String[] args) {
        System.out.println(isPalindrome(12321));
    }

    public static boolean isPalindrome(int x) {
        String s = String.valueOf(x);
        boolean result = true;
        int pre = 0;
        int last = s.length() - 1;
        while (pre < last) {
            if (s.charAt(pre) != s.charAt(last)) {
                return false;
            }
            pre++;
            last--;
        }
        return true;
    }
}
