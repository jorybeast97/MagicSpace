package example;

import structure.TreeNode;

/**
 * 二叉树堆成
 * 判断一颗二叉树是否对称
 */
public class Problem_28 {

    /**
     * 遍历
     * @param root
     * @return
     */
    public boolean isSymmetric(TreeNode root) {
        return judge(root, root);
    }

    /**
     *  判断二叉树是否镜像
     * @param t1
     * @param t2
     * @return
     */
    public boolean judge(TreeNode t1 , TreeNode t2) {
        if (t1 == null && t2 == null) {
            return true;
        }
        if (t1 == null || t2 == null) {
            return false;
        }
        if (t1.value != t2.value) {
            return false;
        }

        return judge(t1.rightNode, t2.leftNode) && judge(t1.leftNode, t2.rightNode);
    }
}
