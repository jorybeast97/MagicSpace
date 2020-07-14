package medium;

public class TranslateNum {
    public int translateNum(int num) {
        //利用跳台阶的思维，每次选取一个或者两个
        String str = String.valueOf(num);
        int[] res = new int[str.length() + 1];
        res[0] = 1;
        res[1] = 1;
        for (int i = 2; i < res.length; i++) {
            String part = str.substring(i - 2, i);
            if (Integer.valueOf(part) >= 10 && Integer.valueOf(part) <= 25) {
                res[i] = res[i - 1] + res[i - 2];
            } else {
                res[i] = res[i - 1];
            }
        }
        return res[res.length - 1];
    }
}
