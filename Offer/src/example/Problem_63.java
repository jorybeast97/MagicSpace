package example;

/**
 * 股票的最大利润
 */
public class Problem_63 {

    /**
     * 最简单的解法，时间复杂度最高为n²
     * @param arr
     * @return
     */
    public int solution(int[] arr) {
        int maxValue = Integer.MIN_VALUE;
        for (int i = 0; i < arr.length ; i++) {
            for (int j = i; j < arr.length ; j++) {
                maxValue = Math.max(maxValue, arr[j] - arr[i]);
            }
        }
        return maxValue;
    }

    /**
     * 遍历
     * @param arr
     * @return
     */
    public int solution_2(int[] arr) {
        if (arr.length == 0 || arr == null) {
            return 0;
        }
        int min = arr[0];
        int max = Integer.MIN_VALUE;
        for (int i = 1; i < arr.length; i++) {
            min = Math.min(min, arr[i]);
            max = Math.max(max, arr[i - min]);
        }
        return max;
    }

}
