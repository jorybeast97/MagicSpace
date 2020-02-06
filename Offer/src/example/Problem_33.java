package example;

/**
 * 二叉搜索树的后序遍历序列
 */
public class Problem_33 {

    public boolean solution(int[] queue, int length) {
        if (queue == null || length <= 0) {
            return false;
        }
        int root = queue[length - 1];

        //在二叉搜索树中左子树节点的值小于根节点的值
        int index = 0;
        for (; index < length - 1; index++) {
            if (queue[index] > root) {
                break;
            }
        }

        //搜索右子树节点的值大于根节点的值
        int j = index;
        for (; j < length - 1; j++) {
            if (queue[j] < root) {
                break;
            }
        }

        //判断左子树
        boolean left = true;
        if (index > 0) {
            left = solution(queue, index);
        }
        //判断右子树
        boolean right = true;
        if (j < length - 1) {
            right = solution(queue, length - index - 1);
        }
        return left && right;
    }
}
