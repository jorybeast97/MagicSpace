package bitOperation;

/**
 * 一的个数
 */
public class OneNumber {

    /**
     * 只针对正数的写法，思路简单
     * @param num
     * @return
     */
    public int easyFun(int num) {
        int res = 0;
        while (num != 0) {
            if ((num&1) == 1) {
                res++;
            }
            num = num >> 1;
        }
        return res;
    }

    /**
     * 能够输入负数的方法，更加普遍
     *
     * @param num
     * @return
     */
    public int hardFun(int num) {
        int res = 0;
        int temp = 1;
        while (temp != 0) {
            if ((num&temp) == 1){
                res++;
            }
            temp = temp << 1;
        }
        return res
    }
}
