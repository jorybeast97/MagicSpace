package easy;

public class LeetCode_633 {

    public static void main(String[] args) {
        System.out.println(judgeSquareSum(1000000));
    }

    public static boolean judgeSquareSum(int target) {
        if (target < 0) {
            return false;
        }
        int i = 0, j = (int)Math.sqrt(target);
        while (i <= j) {
            int powSum = i * i + j * j;
            if (j > 999995){
                System.out.println("i的值是"+i+" j的值是: "+j+" 乘积是 : "+powSum);
            }
            if (powSum == target) {
                return true;
            } else if (powSum > target) {
                j--;
            } else {
                i++;
            }
        }
        return false;
    }
}
