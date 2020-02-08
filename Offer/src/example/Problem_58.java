package example;

import java.util.Stack;

/**
 * 翻转单词顺序列
 */
public class Problem_58 {

    /**
     * 借助栈来完成
     */
    public String solution(String words) {
        Stack<String> stack = new Stack<>();
        StringBuilder sb = new StringBuilder();

        String[] strings = words.split("");
        for (String s : strings) {
            stack.push(s);
        }
        while (!stack.empty()) {
            sb.append(stack.pop());
        }
        return sb.toString();
    }
}
