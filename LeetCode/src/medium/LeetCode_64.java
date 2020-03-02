package medium;

public class LeetCode_64 {
    //动态规划
    public int minPathSum(int[][] grid) {
        if (grid == null || grid.length<1 || grid[0] ==null) {
            return 0;
        }
        int[][] result = new int[grid.length][grid[0].length];
        result[0][0] = grid[0][0];
        for (int i = 1; i < grid.length; i++) {
            result[i][0] = result[i - 1][0] + grid[i][0];
        }
        for (int i = 1; i < grid[0].length; i++) {
            result[0][i] = result[0][i - 1] + grid[0][i];
        }
        for (int i = 1; i < grid.length; i++) {
            for (int j = 1; j < grid[0].length; j++) {
                int left = grid[i][j] + result[i - 1][j];
                int up = grid[i][j] + result[i][j - 1];
                result[i][j] = Math.min(up, left);
            }
        }
        return result[grid.length - 1][grid[0].length - 1];
    }

    public int minPathSum(int[][] grid , int row , int col) {
        if (row == grid.length || col == grid[0].length) {
            return Integer.MIN_VALUE;
        }
        if (row == grid.length-1 || col == grid[0].length-1) {
            return grid[row][col];
        }
        return grid[row][col] + Math.min(minPathSum(grid, row + 1, col), minPathSum(grid, row, col ));
    }

    public int solution(int[][] grid) {
        return minPathSum(grid, 0, 0);
    }

}
