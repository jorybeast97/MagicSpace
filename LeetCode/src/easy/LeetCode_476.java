package easy;

import java.util.concurrent.locks.ReentrantLock;

public class LeetCode_476 {

    public static void main(String[] args) {
        System.out.println(new LeetCode_476().findComplement(5));
    }
    public int findComplement(int num) {
        String binary = Integer.toBinaryString(num);
        System.out.println(binary);
        char[] arr = binary.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == '1') arr[i] = '0';
            else arr[i] = '1';
        }
        String res = new String(arr);
        System.out.println(res);
        int result = Integer.valueOf(res, 2);
        System.out.println(result);
        return result;
    }
}
