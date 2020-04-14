package easy;

public class LeetCode_905 {
    public int[] sortArrayByParity(int[] A) {
        if (A.length <= 1) return A;
        int left = 0;
        int right = A.length - 1;
        while (left < right) {
            while (left < right && help(A[left])) left++;
            while (left < right && !help(A[right])) right--;
            int temp = A[left];
            A[left] = A[right];
            A[right] = temp;
            left++;
            right--;
        }
        return A;
    }

    public boolean help(int i) {
        return i % 2 == 0;
    }
}
