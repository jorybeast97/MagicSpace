package medium;

/**
 * 338. 比特位计数
 * 给定一个非负整数 num。对于 0 ≤ i ≤ num 范围中的每个数字 i ，计算其二进制数中的 1 的数目并将它们作为数组返回。
 *
 * 示例 1:
 *
 * 输入: 2
 * 输出: [0,1,1]
 *
 * 示例 2:
 *
 * 输入: 5
 * 输出: [0,1,1,2,1,2]
 *
 */
public class CountBits {

    public static int[] solution(int num) {
        int[] arr = new int[num + 1];
        for (int i = 0; i < num; i++) {
            arr[i] = checkOne(i);
        }
        return arr;
    }

    //获取二进制代码
    public static int checkOne(int num) {
        String res = Integer.toBinaryString(num);
        int sum = 0;
        char[] chars = res.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '1') {
                sum ++ ;
            }
        }
        return sum;
    }
}
