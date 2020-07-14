package hard;

import java.util.ArrayList;
import java.util.List;

public class LeetCode51 {
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();

        String[][] mux = new String[n][n];
        init(mux);
        return result;
    }

    public boolean helper(String[][] arr, int x, int y) {
        if (x > arr.length-1 || y > arr.length-1) return false;
        for (int i = x; i < arr.length - 1; i++) {
            if (arr[i][y].equals("Q")) return false;
        }
        for (int i = y; y < arr.length - 1; i++) {
            if (arr[x][i].equals("Q")) return false;
        }
        int xIndex = x;
        int yIndex = y;
        while (xIndex < arr.length && yIndex < arr.length) {
            if (arr[xIndex][yIndex].equals("Q")) return false;
            xIndex++;
            yIndex++;
        }
        xIndex = x;
        yIndex = y;
        while (xIndex >= 0 && yIndex >= 0) {
            if (arr[xIndex][yIndex].equals("Q")) return false;
            xIndex--;
            yIndex--;
        }
        return true;
    }

    public List<String> convert(String[][] arr) {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            String s = "";
            for (int j = 0; j < arr.length; j++) s = s + arr[i][j];
            res.add(s);
        }
        return res;
    }

    public void init(String[][] arr) {
        for (String[] s : arr) {
            for (String string : s) {
                string = ".";
            }
        }
    }
}
