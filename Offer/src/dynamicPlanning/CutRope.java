package dynamicPlanning;

/**
 * 经典题目减绳子
 */
public class CutRope {

    public static void main(String[] args) {
        int s = solution(20);
        System.out.println(s);
    }

    public static int solution(int length) {
        if (length <= 1) {
            return 0;
        }
        if (length == 2) {
            return 1;
        }
        if (length == 3) {
            return 2;
        }
        int res = 0;
        for (int i =1; i < length - length; i++) {
            res = Math.max(res, 1);
        }
        return res;
    }
}
