package medium;

import structure.TreeNode;

import java.util.ArrayList;

public class LeetCode_114 {

    ArrayList<TreeNode> arrayList = new ArrayList<>();
    public void flatten(TreeNode root) {
        //遍历
        for (int i = 0; i < arrayList.size() - 1; i++) {
            arrayList.get(i).right = arrayList.get(i+1);
        }
    }

    public void dfs(TreeNode root) {
        if (root == null) {
            return;
        }
        arrayList.add(root);
        dfs(root.left);
        dfs(root.right);
    }
}
