package medium;

import structure.TreeNode;

public class LeetCode_1038 {
    int sum = 0;
    public TreeNode bstToGst(TreeNode root) {
        if(root == null)
            return null;
        TreeNode node = root;
        bstToGstCore(node);
        return root;
    }
    public void bstToGstCore(TreeNode root){
        if(root.right != null){
            bstToGstCore(root.right);
        }
        root.val += sum;
        sum = root.val;
        if(root.left != null){
            bstToGstCore(root.left);
        }
    }
}
