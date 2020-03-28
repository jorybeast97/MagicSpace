package medium;

import structure.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class LeetCode_894 {
    public List<TreeNode> allPossibleFBT(int N) {
        List<TreeNode> res = new ArrayList<TreeNode>();
        if(N % 2 == 0){
            return res;
        }
        if(N == 1){
            res.add(new TreeNode(0));
            return res;
        }
        //对每个奇数
        //一个奇数拆成两个奇数+1
        for(int i = 1; i < N; i += 2){
            List<TreeNode> lt = allPossibleFBT(i);
            List<TreeNode> rt = allPossibleFBT(N - 1 - i);
            //每种可能性都加进来
            for(TreeNode l : lt){
                for(TreeNode r : rt){
                    TreeNode root = new TreeNode(0);
                    root.left = l;
                    root.right = r;
                    res.add(root);
                }
            }
        }
        return res;
    }


}
