package easy;

public class LeetCode_557 {

    public String reverseWords(String s) {
        String[] strings = s.split(" ");
        for (int i = 0; i < strings.length; i++) {
            strings[i] = help(strings[i]);
        }
        String res = "";
        for (int i = 0; i < strings.length; i++) {
            if (i == strings.length - 1) {
                res = res + strings[i];
            }
            else {
                res = res + strings[i] + " ";
            }
        }
        return res;
    }

    public String help(String s) {
        char[] chars = s.toCharArray();
        int start = 0;
        int last = chars.length-1;
        while (start <= last) {
            char temp = chars[start];
            chars[start] = chars[last];
            chars[last] = temp;
            start++;
            last--;
        }
        return new String(chars);
    }
}
