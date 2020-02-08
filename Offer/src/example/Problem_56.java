package example;

import java.util.HashMap;

/**
 * 数组中只出现一次的数字
 */
public class Problem_56 {

    /**
     * 和字母字符串一个道理
     * @param array
     */
    public void solution(int[] array) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i : array) {
            if (map.containsKey(i)) {
                map.put(i, 2);
            }else{
                map.put(i, 1);
            }
        }
        for (int i : array) {
            if (map.get(i) == 1) {
                System.out.println(i);
            }
        }
    }
}
