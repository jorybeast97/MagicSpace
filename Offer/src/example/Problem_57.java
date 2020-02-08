package example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 和为S的两个数字，如果有多个和为S，取乘积最小
 */
public class Problem_57 {

    /**
     * 直接遍历法
     * @param arr
     * @return
     */
    public List<Integer> solution(int[] arr , int s) {
        ArrayList<Integer> list = new ArrayList<>();
        int min = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; i < arr.length; i++) {
                if ((arr[i] + arr[j]) == s && (arr[i] + arr[j]) < min) {
                    list.set(0, arr[i]);
                    list.set(1, arr[j]);
                }
            }
        }
        return list;
    }

    /**
     * 由于数组是递增，所以可以用双指针法
     * @param array
     * @param sum
     * @return
     */
    public ArrayList<Integer> FindNumbersWithSum(int[] array, int sum) {
        int i = 0, j = array.length - 1;
        while (i < j) {
            int cur = array[i] + array[j];
            if (cur == sum)
                return new ArrayList<>(Arrays.asList(array[i], array[j]));
            if (cur < sum)
                i++;
            else
                j--;
        }
        return new ArrayList<>();
    }

}
