package medium;

public class LeetCode_5 {
    public boolean isPalindromic(String s) {
        int len = s.length();
        for (int i = 0; i < len / 2; i++) {
            if (s.charAt(i) != s.charAt(len - i - 1)) {
                return false;
            }
        }
        return true;
    }

    // 暴力解法
    public String longestPalindrome(String s) {
        String ans = "";
        int max = 0;
        int len = s.length();
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j <= len; j++) {
                String test = s.substring(i, j);
                if (isPalindromic(test) && test.length() > max) {
                    ans = s.substring(i, j);
                    max = Math.max(max, ans.length());
                }
            }
        }
        return ans;
    }

    /**
     * 滑动窗口
     * @param s
     * @return
     */
    public String longestPalindrome2(String s) {
        int maxSize=0;
        String res="";
        for (int i = 0,j=i; i < s.length(); i++) {
            while (j<s.length()){
                if (isPalindrome(s,i,j)&&j-i+1>maxSize){
                    maxSize=j-i+1;
                    res=s.substring(i,j+1);
                }
                j++;
            }
            if (i+maxSize-1>=s.length()){
                return res;
            }else {
                j=i+maxSize+1;
            }
        }
        return res;
    }
    private boolean isPalindrome(String s, int i, int j) {
        while (i<j){
            if (s.charAt(i++)!=s.charAt(j--))return false;
        }
        return true;
    }
}
