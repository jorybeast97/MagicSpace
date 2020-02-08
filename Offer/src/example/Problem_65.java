package example;

/**
 * 不用加减乘除做加法
 */
public class Problem_65 {
    public int Add(int a, int b) {
        return b == 0 ? a : Add(a ^ b, (a & b) << 1);
    }
}
