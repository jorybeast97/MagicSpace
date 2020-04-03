package medium;

import structure.ListNode;
import structure.TreeNode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LeetCode_106 {
    Map<Integer,Integer> map = new HashMap<>();
    public TreeNode buildTree(int[] inorder, int[] postorder) {
        for (int i = 0; i < inorder.length; i++) map.put(inorder[i], i);
        return buildHelper(inorder, postorder, 0,
                inorder.length - 1, postorder.length - 1, 0);
    }

    public TreeNode buildHelper(int[] inorder, int[] postorder,
                             int iLeft, int iRight,int pRight,int pLeft) {
        //越界情况
        if (iLeft > iRight || pLeft > pRight) return null;
        int rootVal = postorder[pRight];
        //找到后续遍历根节点在中序中的位置
        int rootIndex = map.get(rootVal);
        TreeNode root = new TreeNode(rootVal);
        root.left = buildHelper(inorder, postorder, iLeft, rootIndex - 1,
                pLeft + rootIndex - iLeft - 1, pLeft);
        root.right = buildHelper(inorder, postorder, rootIndex + 1, iRight,
                pRight - 1, pLeft + rootIndex - iLeft);
        return root;
    }

}
