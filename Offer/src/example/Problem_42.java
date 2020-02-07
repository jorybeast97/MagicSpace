package example;

/**
 * 连续子数组的最大和
 * 输入一个整形数组，数组里有正数也有负数
 */
public class Problem_42 {

    /**
     * 累加法
     * @param arr
     * @return
     */
    public int solution(int[] arr) {
        if (arr.length == 0 || arr == null) {
            return 0;
        }
        int max = Integer.MIN_VALUE;
        int sum = 0;
        for (int value : arr) {
            if (sum <= 0) {
                sum = value;
            }else {
                sum = sum + value;
            }
            max = Math.max(sum, max);
        }
        return max;
    }
}
