package example;

import com.sun.deploy.util.StringUtils;
import structure.TreeNode;

/**
 * 树的子结构
 * 输入两棵二叉树A和B，判断B是否是A的子结构
 */
public class Problem_26 {

    /**
     * 遍历判断是否为子结构
     * @param root
     * @param t2
     * @return
     */
    public boolean solution(TreeNode root, TreeNode t2) {
        boolean result = false;
        if (root != null && t2 != null) {
            if (root.value == t2.value) {
                result = equals(root, t2);
            }
            if (!result) {
                solution(root.leftNode, t2);
            }
            if (!result) {
                solution(root.rightNode, t2);
            }
        }
        return result;
    }

    /**
     * 递归判断两个树形结构是否相同
     * @param treeNode
     * @param t2
     * @return
     */
    public boolean equals(TreeNode treeNode, TreeNode t2) {
        if (t2 == null) {
            return true;
        }
        if (treeNode == null) {
            return false;
        }
        if (treeNode.value != t2.value) {
            return false;
        }
        return equals(treeNode.leftNode, t2.leftNode) && equals(treeNode.rightNode, t2.rightNode);
    }
}
