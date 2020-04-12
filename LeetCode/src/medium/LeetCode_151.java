package medium;

public class LeetCode_151 {
    public String reverseWords(String s) {
        String[] strings = s.split(" ");
        for (int i = 0; i < strings.length; i++) {
            strings[i] = help(strings[i]);
        }
        String res = "";
        for (int i = strings.length-1; i >= 0; i--) {
            if (i == 0) {
                res = res + strings[i];
            }
            else {
                res = res + strings[i] + " ";
            }
        }
        return res;
    }

    public String help(String s) {
        String replace = s.replace(" ", "");
        return replace;
    }
}
