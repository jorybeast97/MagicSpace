package example;

/**
 * 1-n整数中1出现的次数
 *
 */
public class Problem_43 {

    /**
     * 最笨的方法，两次遍历，复杂度O(n²)
     * @param num
     * @return
     */
    public int solution_1(int num) {
        int res = 0;
        for (int i = 1; i < num - 1; i++) {
            String str = String.valueOf(i);
            char[] chars = str.toCharArray();
            for (char c : chars) {
                if (c == '1') {
                    res++;
                }
            }
        }
        return res;
    }

    /**
     * 利用取余解决
     * @param num
     * @return
     */
    public int solution_2(int num) {
        if (num <= 0) {
            return 0;
        }
        int res = 0;
        for (int i = 1; i < num; i++) {
            res = res + remainder(i);
        }
        return res;
    }

    public int remainder(int temp) {
        int res = 0;
        while (temp > 0) {
            if (temp % 10 == 1) {
                res++;
            }
            temp = temp / 10;
        }
        return res;
    }



}
