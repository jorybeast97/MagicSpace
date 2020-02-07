package example;

/**
 * 数字序列中某一位的数字
 *
 */
public class Problem_44 {

    /**
     * 反向操作，先创建后查找
     * @param n
     * @return
     */
    public int solution_1(int n) {
        String str = "";
        for (int i = 0; i < n - 1; i++) {
            str = str + i;
            //当超过n时就停止
            if (str.length() > n) {
                break;
            }
        }
        String[] strings = str.split("");
        return Integer.parseInt(strings[n]);
    }

}
