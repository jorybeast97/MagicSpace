package example;

import java.util.Stack;

/**
 * 用两个栈构建队列
 */
public class Problem_9 {

    Stack<Integer> stack1 = new Stack<>();
    Stack<Integer> stack2 = new Stack<>();

    public void push(Integer num) {
        stack1.push(num);
    }

    public Integer pop() {
        if (stack2.isEmpty()) {
            while (!stack1.isEmpty()) {
                stack2.push(stack1.pop());
            }
        }
        return stack2.pop();
    }
}
