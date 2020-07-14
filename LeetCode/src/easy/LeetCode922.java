package easy;

public class LeetCode922 {
    public int[] sortArrayByParityII(int[] A) {
        int[] res = new int[A.length];
        int jiIndex = 1;
        int ouIndex = 0;
        for (int i = 0; i < A.length; i++) {
            if (A[i] % 2 == 0){
                res[ouIndex] = A[i];
                ouIndex = ouIndex + 2;
            }else {
                res[jiIndex] = A[i];
                jiIndex = jiIndex + 2;
            }
        }
        return res;
    }
}
