package medium;

import structure.TreeNode;

/**
 * 给定一个不含重复元素的整数数组。一个以此数组构建的最大二叉树定义如下：
 *
 * 二叉树的根是数组中的最大元素。
 * 左子树是通过数组中最大值左边部分构造出的最大二叉树。
 * 右子树是通过数组中最大值右边部分构造出的最大二叉树。
 * 通过给定的数组构建最大二叉树，并且输出这个树的根节点。
 * 输入：[3,2,1,6,0,5]
 * 输出：返回下面这棵树的根节点：
 *
 *       6
 *     /   \
 *    3     5
 *     \    /
 *      2  0
 *        \
 *         1
 *
 */
public class LeetCode_654 {
    public TreeNode constructMaximumBinaryTree(int[] nums) {
        return buildTree(nums, 0, nums.length - 1);
    }

    public TreeNode buildTree(int[] nums, int left, int right) {
        if (left > right) return null;
        int index = maxIndex(nums, left, right);
        TreeNode root = new TreeNode(nums[index]);
        root.left = buildTree(nums, left, index - 1);
        root.right = buildTree(nums, index + 1, right);
        return root;
    }

    public int maxIndex(int[] nums, int left, int right) {
        int maxIndex = left;
        for (int i = left; i <= right; i++) {
            if (nums[i] > nums[maxIndex]) maxIndex = i;
        }
        return maxIndex;
    }
}
