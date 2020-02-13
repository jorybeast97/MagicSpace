package structure;


public class  TreeNode {

    public TreeNode leftNode;

    public TreeNode rightNode;

    public Integer value;

    public TreeNode() {
    }

    public TreeNode(Integer value) {
        this.value = value;
    }

    public TreeNode(TreeNode leftNode, TreeNode rightNode, Integer value) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.value = value;
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

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
