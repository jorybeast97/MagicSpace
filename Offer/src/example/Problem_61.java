package example;

import java.util.Arrays;

/**
 * 扑克牌顺子
 */
public class Problem_61 {


    /**
     * 时间复杂度为O（n）
     * @param arr
     * @return
     */
    public boolean isContiuous(int[] arr) {
        Arrays.sort(arr);
        int laizi = 0;
        //获取癞子的数量
        for (int i : arr) {
            if (i==0) {
                laizi++;
                continue;
            }
            break;
        }
        //查找两数差值，当差值大于癞子数量时则不符合
        for (int i = laizi - 1; i < arr.length - 1; i++) {
            if (laizi < 0) {
                return false;
            }
            int temp = arr[i + 1] - arr[i] - 1;
            if (temp <= laizi) {
                laizi = laizi - temp;
            }
        }
        return true;
    }
}
