package easy;

import java.util.ArrayList;
import java.util.List;

public class LeetCode_1380 {
    public List<Integer> luckyNumbers (int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                boolean row = isMin(matrix[i][j], matrix, i);
                boolean col = isMax(matrix[i][j], matrix, j);
                if (row && col){
                    result.add(matrix[i][j]);
                }
            }
        }
        return result;
    }

    public boolean isMin(int n, int[][] matrix, int row) {
        for (int i = 0; i < matrix[row].length; i++) {
            if (matrix[i][row] < n) return false;
        }
        return true;
    }

    public boolean isMax(int n, int[][] matrix, int column) {
        for (int i = 0; i < matrix[i].length; i++) {
            if (matrix[column][i] > n) return false;
        }
        return true;
    }
}
