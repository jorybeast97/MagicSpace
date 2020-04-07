package medium;

import structure.TreeNode;

import java.util.HashMap;

public class LeetCode_1008 {
    //寻找索引
    HashMap<Integer,Integer> indexMap = new HashMap<>();
    public TreeNode bstFromPreorder(int[] preorder) {

        return buildTree(preorder, 0, preorder.length-1);
    }

    public TreeNode buildTree(int[] preorder, int left, int right) {
        if (left >= right) return null;
        TreeNode root = new TreeNode(preorder[left]);
        int index = left;
        while (index <= right && preorder[index] <= preorder[left]) {
            index++;
        }
        root.left = buildTree(preorder, left + 1, index-1);
        root.right = buildTree(preorder, index, right);
        return root;
    }


}
