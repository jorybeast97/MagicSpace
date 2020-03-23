package explore;

public class ReverseString {

    public static void main(String[] args) {
        char[] chars = {'h', 'e', 'l', 'l', 'o'};
        new ReverseString().solution(chars);
        for (char c : chars) {
            System.out.println(c);

        }
    }

    public void solution(char[] c) {
        if (c==null || c.length<=1) return;
        int pre = 0;
        int last = c.length - 1;
        while (pre < last) {
            char temp = c[pre];
            c[pre] = c[last];
            c[last] = temp;
            pre++;
            last--;
        }
    }
}
