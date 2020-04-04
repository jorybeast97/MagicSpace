package hard;

public class LeetCode_42 {
    /**
     * 暴力法,每一个格子能够存储的水量应该是两边最高的格子中小的那一个与其的插值
     * 如果当前格子高于两边的最大格子,则储水量为0
     * @param height
     * @return
     */
    public int trap(int[] height) {
        if (height.length <3 || height==null) return 0;
        int result = 0;
        for (int i = 1; i < height.length; i++) {
            int leftMax = 0, rightMax = 0;
            for (int j = 0; j < i; j++) {
                leftMax = Math.max(leftMax, height[j]);
            }
            for (int j = height.length - 1; j > i; j--) {
                rightMax = Math.max(rightMax, height[j]);
            }
            int small = Math.min(leftMax, rightMax);
            result = result + Math.max(small - height[i], 0);
        }
        return result;
    }


}
