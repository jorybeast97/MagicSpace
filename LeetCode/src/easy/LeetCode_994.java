package easy;

public class LeetCode_994 {

    public int orangesRotting(int[][] grid) {
        if (grid == null || grid.length==0 || grid[0].length == 0) return 0;
        int freshOrange = 0;
        //统计新鲜橘子的个数
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) freshOrange++;
            }
        }
        int minute = 0;
        while (freshOrange > 0) {

        }
        return 0;
    }
}
