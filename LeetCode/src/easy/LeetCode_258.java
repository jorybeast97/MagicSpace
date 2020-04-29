package easy;

public class LeetCode_258 {

    public static void main(String[] args) {
        System.out.println(new LeetCode_258().help(38));

    }

    public int addDigits(int num) {
        if (num < 10) return num;
        int result = num;
        while (true) {
            result = help(result);
            if (result < 10) break;
        }
        return result;
    }

    public int help(int num) {
        String s = String.valueOf(num);
        String[] strings = s.split("");
        int result = 0;
        for (int i = 0; i < strings.length; i++) {
            result = result + Integer.valueOf(strings[i]);
        }
        return result;
    }

}
