package example;

/**
 * 圆圈中最后剩下的数
 */
public class Problem_62 {
    public int solution(int n, int m) {
        if (n < 0) {
            return -1;
        }
        if (n == 0) {
            return 1;
        }
        return (solution(n - 1, m) + m) % n;
    }
}
