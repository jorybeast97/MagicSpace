package example;

/**
 * 二进制中一的个数
 */
public class Problem_15 {

    /**
     * 逻辑与和位移操作
     * @param n
     * @return
     */
    public int solution(int n) {
        int res = 0;
        int flag = 1;
        while (n != 0) {
            if ((flag & n) == 1) {
                res++;
            }
            n = n >> 1;
        }
        return res;
    }

    public int solution_2(int n) {
        return Integer.bitCount(n);
    }
}
