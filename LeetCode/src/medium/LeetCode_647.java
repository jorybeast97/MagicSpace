package medium;

public class LeetCode_647 {

    public static void main(String[] args) {
        System.out.println(new LeetCode_647().solution("abc"));
    }

    public int solution(String s) {
        if (s == null || s.length()==0) return 0;
        if (s.length() == 1) return 1;
        int result = 0;
        char[] charArr = s.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            for (int j = i; j < charArr.length; j++) {
                if (judge(charArr,i,j)) result++;
            }
        }
        return result;

    }

    /**
     * 判断
     * @param chars
     * @param start
     * @param end
     * @return
     */
    public boolean judge(char[] chars, int start, int end) {
        boolean result = true;
        while (start < end) {
            if (chars[start] != chars[end]) {
                return false;
            }
            start++;
            end--;
        }
        return result;
    }
}
