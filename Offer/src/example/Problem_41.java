package example;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * 数据流中的中位数
 */
public class Problem_41 {

    /**
     * 链表排序,获取中位数
     * @param num
     * @return
     */
    public static Integer solution_1(int num) {
        LinkedList<Integer> container = new LinkedList<>();
        container.add(num);
        container.sort((o1, o2) -> {
            return o1 - o2;
        });
        //获取中位数
        Integer result = null;
        if (container.size() % 2 == 0) {
            result = (container.get(container.size()/2-1) + container.get(container.size()/2)) / 2;
        }
        else {
            result = container.get(container.size() / 2);
        }
        return result;
    }

}
