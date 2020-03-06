package medium;

/**
 * 不同路径
 */
public class LeetCode_62 {

    public static void main(String[] args) {
        LeetCode_62 leetCode_62 = new LeetCode_62();
        System.out.println(leetCode_62.solution(51, 9));
    }

    public int uniquePaths(int m, int n) {
        if (m <= 1 && n <=1) return 1;
        int[][] path = new int[m][n];
        for (int i = 0; i < path.length; i++) {
            path[i][0] = 1;
        }
        for (int i = 0; i < path[0].length; i++) {
            path[0][i] = 1;
        }
        helper(path, 1, 1);
        return path[m - 1][n - 1];
    }
    //使用递归写法,容易超时
    public void helper(int[][] nums, int row, int column) {
        if (row > nums.length-1 || column > nums[0].length-1) return;
        nums[row][column] = nums[row - 1][column] + nums[row][column - 1];
        helper(nums, row + 1, column);
        helper(nums, row, column + 1);
    }

    //使用for循环
    public int solution(int m, int n) {
        int[][] dp = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 || j == 0) {
                    dp[i][j] = 1;
                }else {
                    dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
                }

            }
        }
        return dp[m - 1][n - 1];
    }


}
