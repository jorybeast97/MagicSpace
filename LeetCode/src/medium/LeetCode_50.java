package medium;

public class LeetCode_50 {
    public double myPow(double x, int n) {
        return Math.pow(x, n);
    }

    public double solution(double x, int n) {
        if(x == 0) return 0;
        if (n == 0) return 1;
        if (n < 0) {
            x = 1 / x;
            n = -n;
        }
        double result = 1;
        for (int i = 0; i < n; i++) {
            result = result * x;
        }
        return result;
    }
}
