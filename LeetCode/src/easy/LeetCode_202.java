package easy;

import java.util.HashSet;

public class LeetCode_202 {

    public static void main(String[] args) {
        System.out.println(new LeetCode_202().convert(19));

    }
    public boolean isHappy(int n) {
        HashSet<Integer> set = new HashSet<>();
        int temp = n;
        while (true) {
            temp = convert(temp);
            if (temp == 1) return true;
            if (set.contains(temp)) return false;
            set.add(temp);
        }
    }

    public int convert(int n) {
        String s = String.valueOf(n);
        String[] strings = s.split("");
        int result = 0;
        for (String c : strings) {
            int t = Integer.valueOf(c);
            result = result + (t*t);
        }
        return result;
    }
}
