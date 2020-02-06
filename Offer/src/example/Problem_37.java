package example;

import structure.TreeNode;

/**
 * 二叉树的序列化与反序列化
 */
public class Problem_37 {
    private String deserializeStr;

    public String Serialize(TreeNode root) {
        if (root == null)
            return "#";
        return root.value + " " + Serialize(root.leftNode) + " " + Serialize(root.rightNode);
    }

    public TreeNode Deserialize(String str) {
        deserializeStr = str;
        return Deserialize();
    }

    private TreeNode Deserialize() {
        if (deserializeStr.length() == 0)
            return null;
        int index = deserializeStr.indexOf(" ");
        String node = index == -1 ? deserializeStr : deserializeStr.substring(0, index);
        deserializeStr = index == -1 ? "" : deserializeStr.substring(index + 1);
        if (node.equals("#"))
            return null;
        int val = Integer.valueOf(node);
        TreeNode t = new TreeNode(val);
        t.leftNode = Deserialize();
        t.rightNode = Deserialize();
        return t;
    }

}
