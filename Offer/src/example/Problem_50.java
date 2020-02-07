package example;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 第一次只出现一次的字符
 */
public class Problem_50 {

    /**
     * 时间为O(n)解法
     *利用hashmap求解
     * @return
     */
    public String solution_1(String string) {
        String[] strings = string.split("");
        HashMap<String,Integer> hashMap = new HashMap<>();
        for (int i = 0; i < strings.length - 1; i++) {
            if (hashMap.containsKey(strings[i])) {
                Integer count = hashMap.get(strings[i]);
                hashMap.put(strings[i], count++);
            }else {
                hashMap.put(strings[i], 1);
            }
        }

        for (int i = 0; i < strings.length - 1; i++) {
            if (hashMap.get(strings[i]) == 1) {
                return strings[i];
            }
        }
        return null;
    }

    /**
     * 最常见的解法，双重遍历
     * @param string
     * @return
     */
    public String solution_2(String string) {
        String[] strs = string.split("");
        for (int i = 0; i < strs.length - 1; i++) {
            for (int j = i; j < strs.length - 1; j++) {
                if (strs[i].equals(strs[j])) {
                    break;
                }
                if (j == strs.length - 1) {
                    return strs[i];
                }
            }
        }
        return null;
    }

    
}
