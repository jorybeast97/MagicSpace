package array;

/**
 * 输入一个整数数组，实现一个函数来调整该数组中
 * 数组的顺序，要求所有的奇数位于数组前半部分，所有
 * 偶数位于数组后半部分
 */
public class Problem_21 {

    /**
     * 用空间换时间的解法，创建新数组
     * 两次扫描
     * @return
     */
    public static int[] solutionOne(int[] arr) {
        int[] res = new int[arr.length];
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            if (judgeJi(arr[i])) {
                res[index] = arr[i];
                index++;
            }
        }
        for (int i = 0; i < arr.length; i++) {
            if (judgeO(arr[i])) {
                res[index] = arr[i];
                index++;
            }
        }
        arr = res;
        return arr;
    }

    /**
     * 双指针解法 , pre和last向中间扫描当pre为偶数last为奇数交换
     * 否则继续扫描
     * @param arr
     * @return
     */
    public static int[] solutionTwo(int[] arr) {
        int preIndex = 0;
        int lastIndex = arr.length - 1;
        while (preIndex < lastIndex) {
            if (judgeO(arr[preIndex]) && judgeJi(arr[lastIndex])) {
                //交换两个数
                int temp = arr[preIndex];
                arr[preIndex] = arr[lastIndex];
                arr[lastIndex] = temp;
                preIndex++;
                lastIndex--;
            }
            if (judgeJi(arr[preIndex])) {
                preIndex++;
            }
            if (judgeO(arr[lastIndex])) {
                lastIndex--;
            }
        }
        return arr;
    }

    public static boolean judgeJi(int num) {
        return num % 2 == 1;
    }

    public static boolean judgeO(int num) {
        return num % 2 == 0;
    }
}
