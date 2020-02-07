package example;

import structure.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 二叉查找树第k小的结点
 */
public class Problem_54 {

    int index = 0;

    /**
     * 直接中序遍历，中序遍历所产生的结果就是从小到大
     *
     * @param root
     * @param k
     * @return
     */
    public TreeNode hardSolution(TreeNode root, int k) {
        if (root == null) {
            return null;
        }
        TreeNode treeNode = hardSolution(root.leftNode, k);
        if (treeNode != null) {
            index++;
        }
        if (index == k) {
            return root;
        }
        treeNode = hardSolution(root.rightNode, k);
        if (treeNode != null) {
            return treeNode;
        }
        return null;
    }

    /**
     * 根据顺序遍历进入一个链表，然后读取k
     * 时间复杂度为O(n)，空间也为O(n)
     * @param root
     * @param k
     * @return
     */
    public TreeNode solution(TreeNode root , int k) {
        List<TreeNode> list = new ArrayList<>();
        help(root, list);
        return list.get(k - 1);
    }

    public void help(TreeNode node, List<TreeNode> list) {
        if (node == null) {
            return;
        }
        help(node.leftNode, list);
        list.add(node);
        help(node.rightNode, list);
    }
}
