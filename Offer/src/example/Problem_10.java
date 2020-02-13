package example;

/**
 * 斐波那契数列
 */
public class Problem_10 {

    public static void main(String[] args) {
        System.out.println(solution(10));

    }

    //跳台阶
    public static int solution(int n) {
        if (n==1) {
            return 1;
        }
        if (n == 2) {
            return 2;
        }
        return solution(n - 1) + solution(n - 2);
    }
}
