package example;

/**
 * 二维数组查找
 * 都是递增
 */
public class Problem_4 {

    /**
     * 先通过检查缩小检查范围，然后再进行查找
     * @param arr
     * @param key
     * @return
     */
    public boolean solution(int[][] arr, int key) {
        if (arr.length == 0) {
            return false;
        }
        int column = 0;
        for (int i = 1; i < arr.length; i++) {
            if (key < arr[i][0]) {
                column = i - 1;
                break;
            }
        }
        int row = 0;
        for (int t = 1; t < arr[0].length; t++) {
            if (key < arr[0][t]) {
                row = t - 1;
                break;
            }
        }

        for (int i = 0; i < column; i++) {
            for (int j = i; j < row; j++) {
                if (arr[i][j] == key) {
                    return true;
                }
            }
        }
        return false;
    }
}
