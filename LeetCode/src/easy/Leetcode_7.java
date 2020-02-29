package easy;

import java.util.HashMap;
import java.util.Map;

public class Leetcode_7 {

    public static void main(String[] args) {
        System.out.println(reverse(21473647));
    }
    public static int reverse(int x) {
        boolean isNegative = false;
        if (x < 0) {
            isNegative = true;
        }
        String s = "" + x;
        StringBuilder sb = new StringBuilder();
        int last = 0;
        if (isNegative) {
            last = 1;
        }
        for (int i = s.length()-1; i>=last; i--) {
            sb.append(s.charAt(i));
        }
        if (isNegative){
            s = "-" + sb.toString();
        }else {
            s = sb.toString();
        }
        Long res = Long.parseLong(s);
        if (res > 2147483647 || res < -2147483648){
            return 0;
        }else {
            return Integer.parseInt(s);
        }

    }
}
