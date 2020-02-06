package example;

import java.util.ArrayList;

/**
 * 顺时针打印矩阵，暂缓
 * 没有弄懂
 */
public class Problem_29 {
    public ArrayList<Integer> printMatrix(int [][] matrix) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        if(matrix==null || matrix.length==0 || (matrix.length==1 && matrix[0].length==0)) {
            return null;
        }
        int row = matrix.length-1;
        int col = matrix[0].length-1;
        int rowstart = 0,colstart=0;
        while(rowstart<=row && colstart<=col){
            // 只剩一行
            if(rowstart==row){
                for(int q=colstart;q<=col;q++) {
                    result.add(matrix[row][q]);
                }
                return result;
            }
            //只剩一列
            if(colstart==col){
                for(int t=rowstart;t<=row;t++) {
                    result.add(matrix[t][col]);
                }
                return result;
            }
            for(int i=colstart;i<=col;i++) {
                result.add(matrix[rowstart][i]);
            }
            rowstart++;
            for(int j=rowstart;j<=row;j++) {
                result.add(matrix[j][col]);
            }
            col--;
            for(int k=col;k>=colstart;k--) {
                result.add(matrix[row][k]);
            }
            row--;
            for(int y=row;y>=rowstart;y--) {
                result.add(matrix[y][colstart]);
            }
            colstart++;
        }
        return result;
    }
}
