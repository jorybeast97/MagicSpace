package medium;

import structure.TreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeetCode_1305 {
    public List<Integer> getAllElements(TreeNode root1, TreeNode root2) {
        List<Integer> result = new ArrayList<>();
        addList(root1, result);
        addList(root2, result);
        Collections.sort(result,(o1, o2) -> {
            return o1-o2;
        });
        return result;
    }

    public void addList(TreeNode root, List<Integer> list) {
        if (root == null) return;
        addList(root.right, list);
        addList(root.left, list);
        list.add(root.val);
    }
}
