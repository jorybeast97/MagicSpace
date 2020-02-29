package easy;

import java.util.Stack;

public class LeetCode_155 {

        Stack<Integer> mainStack = new Stack<>();
        Stack<Integer> helper = new Stack<>();

        public void push(int x) {
            if (helper.isEmpty() && mainStack.isEmpty()){
                mainStack.push(x);
                helper.push(x);
                return;
            }
            mainStack.push(x);
            if (x < helper.peek()) {
                helper.push(x);
            }else {
                helper.push(helper.peek());
            }
        }

        public void pop() {
            mainStack.pop();
            helper.pop();
        }

        public int top() {
            helper.pop();
            return mainStack.pop();
        }

        public int getMin() {
            return helper.peek();
        }
}
