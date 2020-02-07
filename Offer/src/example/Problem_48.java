package example;

/**
 * 最长不含重复字符的字符子串
 */
public class Problem_48 {

    /**
     * 经典双指针法
     * @param s
     * @return
     */
    public int solution(String s) {
        String[] arr = s.split("");
        int pre = 1;
        int last = 0;
        int max = 0;
        while (pre < arr.length - 1) {
            if (arr[pre].equals(arr[last])) {
                last++;
            }
            max = Math.max(max, pre - last);
            pre++;
        }
        return max;
    }

}
