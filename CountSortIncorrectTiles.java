import java.util.*;

public class CountSortIncorrectTiles {

    public static class TileInfo {
        public int row, col, degree;
        public TileInfo(int r, int c, int d) {
            row = r; col = c; degree = d;
        }
    }

    public static List<TileInfo> sort(Board board) {

        List<TileInfo>[] groups = new ArrayList[5];
        for (int i = 0; i < 5; i++) groups[i] = new ArrayList<>();

        for (int r = 0; r < board.rows; r++)
            for (int c = 0; c < board.cols; c++) {
                Tile t = board.grid[r][c];
                if (t.rotation != t.solutionRotation) {
                    int deg = 0;
                    for (boolean b : t.conn) if (b) deg++;
                    groups[deg].add(new TileInfo(r, c, deg));
                }
            }

        List<TileInfo> res = new ArrayList<>();
        for (int d = 4; d >= 0; d--) res.addAll(groups[d]);
        return res;
    }
}
