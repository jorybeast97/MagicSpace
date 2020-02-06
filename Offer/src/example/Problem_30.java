package example;

import java.util.Stack;

/**
 * 包含main函数的栈
 */
public class Problem_30 {
    //主栈
    Stack<Integer> mainStack = new Stack<>();
    //辅助栈
    Stack<Integer> auxiliaryStack = new Stack<>();

    public void push(Integer num) {
        //压入主栈
        mainStack.push(num);
        if (auxiliaryStack.empty()) {
            auxiliaryStack.push(num);
        }else {
            Integer temp = auxiliaryStack.peek();
            if (num >= temp) {
                auxiliaryStack.push(temp);
            }else {
                auxiliaryStack.push(num);
            }
        }
    }

    public Integer pop() {
        Integer res = mainStack.pop();
        auxiliaryStack.pop();
        return res;
    }

    public Integer min() {
        return auxiliaryStack.peek();
    }


}
