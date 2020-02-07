package example;

/**
 * 数组中的逆序队
 */
public class Problem_51 {

    /**
     * 无脑解
     * @param arr
     * @return
     */
    public int solution_1(int[] arr) {
        int result = 0;
        int index = 0;
        for (int i = 1; i < arr.length - 1; i++) {
            if (arr[index] > arr[i]) {
                result++;
            }
        }
        return result;
    }
}
