package easy;

public class LeetCode_122 {

    /**
     * 贪心算法
     * @param prices
     * @return
     */
    public int maxProfit(int[] prices) {
        int result = 0;
        for (int i = 0; i < prices.length - 1; i++) {
            if (prices[i + 1] > prices[i]) {
                result = result + (prices[i + 1] - prices[i]);
            }
        }
        return result;
    }

}
