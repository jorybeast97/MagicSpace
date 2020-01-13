package hard;


/**
 * 42.接雨水
 * 给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，下雨之后能接多少雨水。
 *
 * 输入: [0,1,0,2,1,0,1,3,2,1,2,1]
 * 输出: 6
 */
public class Trap {

    public static void main(String[] args) {

    }

    /**
     * 列求法，分别向左，向右求两边最高的墙，然后根据木桶效应选择矮墙减去当前的墙
     * 所得就是当前列在这两个墙之间存储的水。通过循环就能够得到所有的水量。
     * @param height
     * @return
     */
    public static int solution(int[] height) {
        int res = 0;
        int length = height.length;
        for (int i = 1; i < length - 1; i++) {
            int left_max = 0;
            int right_max = 0;
            for (int j = i; j >= 0; j--) {
                left_max = Math.max(height[j], left_max);
            }
            for (int j = i; j < length; j++) {
                right_max = Math.max(height[j], right_max);
            }
            res =res + Math.min(left_max, right_max) - height[i];
        }
        return res;
    }


}
