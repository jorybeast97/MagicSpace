package example;

import structure.TreeNode;

/**
 * 二叉树的深度
 */
public class Problem_55 {

    /**
     * 二叉树深度遍历
     * @param root
     * @return
     */
    public int solution(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int leftDeepth = solution(root.leftNode);
        int rightDeepth = solution(root.rightNode);
        int result = 1 + Math.max(leftDeepth, rightDeepth);
        return result;
    }

    /**
     * 平衡二叉树
     * @param root
     */
    public boolean binaryTree(TreeNode root) {
        isBalance(root);
        return isBalance;
    }

    public boolean isBalance = true;

    public int isBalance(TreeNode root) {
        if (root == null || !isBalance) {
            return 0;
        }
        int left = isBalance(root.leftNode);
        int right = isBalance(root.rightNode);
        if (Math.abs(left - right) > 1) {
            isBalance = false;
        }
        return 1 + Math.max(left, right);
    }
}
