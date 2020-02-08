package example;

/**
 * 剪绳子
 */
public class Problem_14 {

    /**
     * 动态规划
     */
    public int integerBreak(int n) {
        int[] dp = new int[n + 1];
        dp[1] = 1;
        for (int i = 2; i <= n; i++) {
            for (int j = 1; j < i; j++) {
                dp[i] = Math.max(dp[i], Math.max(j * (i - j), dp[j] * (i - j)));
            }
        }
        return dp[n];
    }

    /**
     * 暴力解法
     * 比较i*(n-i) 和 i * integerBreak的原因是绳子如果只分割为两节
     * 则长度为 I *(N-I)，但是不知道剪成两截和多截的大小，所以需要比较
     * @param n
     * @return
     */
    public int solution(int n) {
        if (n == 2) {
            return 1;
        }
        int res = -1;
        for (int i = 1; i <= n - 1; i++) {
            res = Math.max(res, Math.max(i * (n - i), i * integerBreak(n - i)));
        }
        return res;
    }


}
