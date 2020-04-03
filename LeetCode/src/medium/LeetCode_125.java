package medium;

public class LeetCode_125 {

    public static void main(String[] args) {
        String s = "A man, a plan, a canal: Panama";
        LeetCode_125 leetCode_125 = new LeetCode_125();
        System.out.println(leetCode_125.isPalindrome(s));
    }

    public boolean isPalindrome(String s) {
        if (s == null) return true;
        String str = s.toLowerCase();
        int start = 0;
        int end = str.length()-1;
        boolean result = true;
        while (start < end) {
            if (!Character.isLetterOrDigit(str.charAt(start))) {
                start++;
                continue;
            }
            if (!Character.isLetterOrDigit(str.charAt(end))) {
                end--;
                continue;
            }
            if (str.charAt(start) == str.charAt(end)) {
                start++;
                end--;
            }else {
                return false;
            }
        }
        return result;
    }
}
