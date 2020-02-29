package structure;

import java.util.Random;

public class Utils {

    public static int[] getArray(int bound, int length) {
        if (length <= 0) {
            return null;
        }
        int[] result = new int[length];
        Random random = new Random();
        for (int i = 0; i < result.length; i++) {
            result[i] = random.nextInt(bound);
        }
        return result;
    }

    public static void ergodic(int[] arr) {
        for (int t : arr) {
            System.out.println(t);
        }
    }
}
