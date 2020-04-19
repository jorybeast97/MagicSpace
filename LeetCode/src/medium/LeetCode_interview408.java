package medium;

import structure.TreeNode;
/**
 * 代码思路为：
 * 递归查询两个节点p q，如果某个节点等于节点p或节点q，则返回该节点的值给父节点。
 * 如果当前节点的左右子树分别包括p和q节点，那么这个节点必然是所求的解。
 * 如果当前节点有一个子树的返回值为p或q节点，则返回该值。（告诉父节点有一个节点存在其子树中）
 * 如果当前节点的两个子树返回值都为空，则返回空指针。
 *
 * 作者：WalkerR
 * 链接：https://leetcode-cn.com/problems/er-cha-shu-de-zui-jin-gong-gong-zu-xian-lcof/solution/zui-jin-gong-gong-zu-xian-wen-ti-by-walkerr/
 * 来源：力扣（LeetCode）
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */
public class LeetCode_interview408 {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if(root == null || root == p || root == q) return root;
        TreeNode left = lowestCommonAncestor(root.left,p,q);
        TreeNode right = lowestCommonAncestor(root.right,p,q);
        if(left != null && right != null) return root;
        else if(left == null) return right;
        else return left;
    }
}
