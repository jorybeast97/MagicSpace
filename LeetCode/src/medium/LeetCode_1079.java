package medium;

import java.util.HashSet;
import java.util.Set;

public class LeetCode_1079 {

    public int numTilePossibilities(String tiles) {

        boolean[] visited = new boolean[tiles.length()];
        Set<String> set = new HashSet<>();
        dfs(visited, set, tiles, new StringBuilder());
        return set.size() - 1;
    }

    public void dfs(boolean[] visited, Set<String> set, String tiles, StringBuilder sb) {

        set.add(sb.toString());

        for (int i = 0; i < tiles.length(); i++) {

            if (!visited[i]) {
                visited[i] = true;
                sb.append(tiles.charAt(i));
                dfs(visited, set, tiles, sb);
                sb.deleteCharAt(sb.length() - 1);
                visited[i] = false;
            }

        }
    }
}
