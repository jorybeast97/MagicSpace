package example;

import java.util.Arrays;

/**
 * 数组中出现超过一半的数字
 */
public class Problem_39 {

    /**
     * 无脑解法
     * @param arr
     * @return
     */
    public int solution_1(int[] arr) {
        Arrays.sort(arr);
        return arr[arr.length - 1];
    }

    /**
     * 如果不让修改数组顺序，可以按照该方法
     * @param arr
     * @return
     */
    public int solution_2(int[] arr) {
        Integer res = null;
        Integer num = null;
        for (int t : arr) {
            if (res == null) {
                res = t;
                num = 1;
            }
            if (t == res) {
                num++;
            }else {
                num--;
            }

            if (num == 0) {
                res = t;
                num = 1;
            }
        }
        return res;
    }
}
