package medium;

import structure.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ZPrintTree {

    public List<TreeNode> solution(TreeNode root) {
        if (root == null) {
            return null;
        }
        List<TreeNode> list = new ArrayList<>();
        Stack<TreeNode> leftStack = new Stack<>();
        Stack<TreeNode> rightStack = new Stack<>();
        int temp = 0;
        rightStack.push(root);
        while (leftStack != null || rightStack != null) {
            if (temp == 0) {
                TreeNode t1 = leftStack.pop();
                if (t1.left != null){
                    leftStack.push(t1.left);
                }
                if (t1.right != null) {
                    leftStack.push(t1.right);
                }
                if (leftStack.isEmpty()) {
                    list.add(rightStack.pop());
                    temp = 1;
                }
            } else {
                TreeNode t2 = rightStack.pop();
                if (t2.right != null) {
                    rightStack.push(t2.right);
                }
                if (t2.left != null) {
                    rightStack.push(t2.left);
                }
                if (rightStack.isEmpty()) {
                    list.add(leftStack.pop());
                    temp = 0;
                }
            }
        }
        return list;
    }
}
