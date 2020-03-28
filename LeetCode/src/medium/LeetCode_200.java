package medium;

import java.util.LinkedList;
import java.util.Queue;

public class LeetCode_200 {
    public int numIslands(char[][] grid) {
        int[][] directions = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
        int row = grid.length;
        int column = grid[0].length;
        //访问节点
        boolean[][] path = new boolean[row][column];
        int result = 0;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                //岛屿未被访问并且
                if (path[i][j] != true && grid[i][j] == '1') {
                    result++;
                    //存储坐标用数组
                    Queue<int[]> characterQueue = new LinkedList<>();
                    int[] xy = {i, j};
                    characterQueue.add(xy);
                    path[i][j] = true;
                    //开始BFS
                    while (!characterQueue.isEmpty()) {
                        //弹出当前岛屿,并获取岛屿的四个方向坐标值
                        int[] t = characterQueue.poll();
                        int x = t[0];
                        int y = t[1];
                        for (int k = 0; k < 4; k++) {
                            int newX = x + directions[k][0];
                            int newY = x + directions[k][1];
                            if (judge(newX, newY, row, column) && !path[newX][newY] && grid[newX][newY] == '1') {
                                characterQueue.add(new int[]{newX, newY});
                                path[newX][newY] = true;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    //判断坐标是否越界
    public boolean judge(int x,int y, int row, int column) {
        return x >= 0 && x < row && y >= 0 && y < row;
    }
}
