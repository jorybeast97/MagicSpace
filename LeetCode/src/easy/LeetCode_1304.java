package easy;

public class LeetCode_1304 {

    public int[] solution(int n) {
        int[] result = new int[n];
        int add = 0;
        for (int i = 0; i < n - 1; i++) {
            add = add + i;
            result[i] = i;
        }
        result[n - 1] = -add;
        return result;
    }
}
