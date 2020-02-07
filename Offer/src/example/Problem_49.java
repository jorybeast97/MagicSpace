package example;

/**
 * 丑数
 */
public class Problem_49 {

    /**
     * 基本解法
     * @param num
     * @return
     */
    public boolean solution(int num) {
        while (num % 2 == 0) {
            num = num / 2;
        }
        while (num % 3 == 0) {
            num = num / 3;
        }
        while (num % 5 == 0) {
            num = num / 5;
        }
        return num == 1;
    }
}
