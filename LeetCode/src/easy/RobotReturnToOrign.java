package easy;

/**
 * 657. 机器人能否返回原点
 * 在二维平面上，有一个机器人从原点 (0, 0) 开始。给出它的移动顺序，判断这个机器人在完成移动后是否在 (0, 0) 处结束。
 *
 * 移动顺序由字符串表示。字符 move[i] 表示其第 i 次移动。机器人的有效动作有 R（右），L（左），U（上）和 D（下）。如果机器人在完成所有动作后返回原点，则返回 true。否则，返回 false。
 *
 *
 * 思路 : 只需要将机器人起始点作为xy平面的0点，左右行动对应xy增减，y轴同理，最后判断xy是否都为0
 */
public class RobotReturnToOrign {

    public static void main(String[] args) {
        String move = "UDLL";
        System.out.println(solution(move));
    }

    public static boolean solution(String move) {
        int x = 0 ;
        int y = 0 ;

        char[] chars = move.toCharArray();
        for (int i = 0 ; i < chars.length; i++) {
            if (chars[i] == 'L') x--;
            if (chars[i] == 'R') x++;
            if (chars[i] == 'U') y++;
            if (chars[i] == 'D') y--;
        }

        return x== 0 && y==0;
    }
}
