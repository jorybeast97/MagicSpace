package example;

/**
 * 构建乘积数组
 */
public class Problem_66 {

    public int[] solution(int[] arr) {
        int[] result = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = getVal(arr, i);
        }
        return result;
    }

    public int getVal(int[] arr, int i) {
        int res = 1;
        for (int t = 0; i < arr.length; t++) {
            if (t == i) {
                continue;
            }
            res  = res * arr[t];
        }
        return res;
    }
}
