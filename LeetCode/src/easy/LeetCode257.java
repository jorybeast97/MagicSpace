package easy;

import structure.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class LeetCode257 {
    List<String> list = new ArrayList<>();
    public List<String> binaryTreePaths(TreeNode root) {
        String path = "";
        helper(root,path);
        return list;
    }

    public void helper(TreeNode root,String path) {
        if (root == null) return;
        path = path + root.val;
        if (root.left == null && root.right == null) {
            list.add(path);
        }else {
            path = path + "->";
            helper(root.left, path);
            helper(root.right, path);
        }
    }
}
