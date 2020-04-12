package medium;

import structure.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 层序遍历,然后取出最后一层的最左边的值
 */
public class LeetCode_513 {

    public int findBottomLeftValue(TreeNode root) {
        List<List<Integer>> bfs = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode head = queue.poll();
                if (head.left != null) {
                    queue.add(head.left);
                }
                if (head.right != null) {
                    queue.add(head.right);
                }
                list.add(head.val);
            }
            bfs.add(list);
        }
        List<Integer> last = bfs.get(bfs.size() - 1);
        return last.get(0);
    }


}
