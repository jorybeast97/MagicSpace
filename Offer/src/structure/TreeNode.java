package structure;


public class TreeNode {

    public TreeNode leftNode;

    public TreeNode rightNode;

    public Object value;

    public TreeNode() {
    }

    public TreeNode(TreeNode leftNode, TreeNode rightNode, Object value) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.value = value;
    }

    public TreeNode(int val) {
        value = val;
    }

    public TreeNode getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(TreeNode leftNode) {
        this.leftNode = leftNode;
    }

    public TreeNode getRightNode() {
        return rightNode;
    }

    public void setRightNode(TreeNode rightNode) {
        this.rightNode = rightNode;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
