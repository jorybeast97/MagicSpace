package example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 把数组排成最小的数
 */
public class Problem_45 {

    public String solution(int[] arr) {
        List<Integer> list = new ArrayList<>();
        for (int t : arr) {
            list.add(t);
        }

        Collections.sort(list, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                String s1 = o1 + "" + o2;
                String s2 = o2 + "" + o1;
                return s1.compareTo(s2);
            }
        });
        String res = "";
        for (int i : list) {
            res = res + i;
        }
        return res;
    }
}
