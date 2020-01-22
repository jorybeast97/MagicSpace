package easy;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * 350. 两个数组的交集 II
 * 给定两个数组，编写一个函数来计算它们的交集。
 *
 * 示例 1:
 *
 * 输入: nums1 = [1,2,2,1], nums2 = [2,2]
 * 输出: [2,2]
 *
 * 示例 2:
 *
 * 输入: nums1 = [4,9,5], nums2 = [9,4,9,8,4]
 * 输出: [4,9]
 *
 * 说明：
 *
 *     输出结果中每个元素出现的次数，应与元素在两个数组中出现的次数一致。
 *     我们可以不考虑输出结果的顺序。
 *
 */
public class Intersect {

    public int[] solution(int[] nums1, int[] nums2) {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        //先遍历第一个数组，并初始化map
        for(int i = 0; i < nums1.length; i++){
            if(map.containsKey(nums1[i]))
                map.put(nums1[i], map.get(nums1[i]) + 1);
            else
                map.put(nums1[i], 1);
        }

        //再遍历第二个数组，将于map中找到的key放入list中
        LinkedList<Integer> list = new LinkedList<Integer>();
        for(int j = 0; j < nums2.length; j++){
            if(map.containsKey(nums2[j]) && map.get(nums2[j]) > 0){
                list.add(nums2[j]); //添加到list中
                map.put(nums2[j], map.get(nums2[j]) - 1);
            }
        }

        //最后，将list中的值放入数组中
        int count = list.size();
        int[] aux = new int[count];
        for(int i = 0; i < count; i++){
            aux[i] = list.poll();
        }
        return aux;

    }
}
