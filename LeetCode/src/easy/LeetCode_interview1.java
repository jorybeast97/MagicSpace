package easy;

import java.util.HashSet;
import java.util.Set;

/**
 * 面试题 01.01. 判定字符是否唯一
 * 实现一个算法，确定一个字符串 s 的所有字符是否全都不同。
 */
public class LeetCode_interview1 {
    public boolean isUnique(String astr) {
        Set<Character> characters = new HashSet<>();
        for (int i = 0; i < astr.length(); i++) {
            if (!characters.add(astr.charAt(i))) return false;
        }
        return true;
    }
}
