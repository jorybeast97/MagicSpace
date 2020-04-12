package hard;

public class LeetCode_980 {
    public int uniquePathsIII(int[][] grid) {
        int sum = 1;//空白格+起始点的个数
        int r = 0;
        int c = 0;
        for(int i = 0; i < grid.length; i++) {
            for(int j = 0; j < grid[0].length; j++) {
                if(grid[i][j] == 0) {
                    sum++;
                } else if(grid[i][j] == 1) {
                    r = i;
                    c = j;
                }
            }
        }
        return dfs(grid, r, c, sum);
    }


    public int dfs(int[][] g, int i, int j, int sum) {
        if(i < 0 || i >= g.length || j < 0 || j >= g[0].length || g[i][j] == -1) {
            return 0;
        }

        if(g[i][j] == 2) return 0 == sum ? 1 : 0;
        int fix = 0;
        g[i][j] = -1;
        fix += dfs(g, i + 1, j, sum - 1);
        fix += dfs(g, i - 1, j, sum - 1);
        fix += dfs(g, i, j + 1, sum - 1);
        fix += dfs(g, i, j - 1, sum - 1);
        g[i][j] = 0;
        return fix;
    }

}
