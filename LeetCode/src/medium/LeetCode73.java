package medium;

public class LeetCode73 {
    public void setZeroes(int[][] matrix) {
        int[][] arr = matrix;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (arr[i][j] == 0) setZero(matrix, i, j);
            }
        }
    }

    public void setZero(int[][] matrix, int x, int y) {
        int xsize = matrix.length;
        int ysize = matrix[0].length;
        for (int i = 0; i < xsize; i++) {
            matrix[i][y] = 0;
        }
        for (int i = 0; i < ysize; i++) {
            matrix[x][i] = 0;
        }
    }
}
