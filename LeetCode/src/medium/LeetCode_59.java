package medium;

import java.util.ArrayList;
import java.util.List;

public class LeetCode_59 {
    public int[][] generateMatrix(int n) {
        int r1 = 0, r2 = n - 1;
        int c1 = 0, c2 = n - 1;
        int[][] result = new int[n][n];
        int num = 1;
        while (r1 <= r2 && c1 <= c2) {
            for (int c = c1; c <= c2; c++) result[r1][c] = num++;
            for (int r = r1 + 1; r <= r2; r++) result[r][c2] = num++;
            if (r1 < r2 && c1 < c2) {
                for (int c = c2 - 1; c > c1; c--) result[r2][c] = num++;
                for (int r = r2; r > r1; r--) result[r][c1] = num++;
            }
            r1++;
            r2--;
            c1++;
            c2--;
        }
        return result;
    }
}
