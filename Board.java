import java.util.*;

public class Board {

    public int rows, cols;
    public Tile[][] grid;

    private final int[] dr = {-1, 0, 1, 0};
    private final int[] dc = {0, 1, 0, -1};

    private GameMode mode = GameMode.SINGLE_PLAYER;
    private Greedy greedy;

    private boolean[][] lastCycle = null;
    private boolean allowCycleDetection = false;

    public int moves = 0;

    public Board(int r, int c) {
        rows = r;
        cols = c;
        grid = new Tile[r][c];
        generateSolvable();
        greedy = new Greedy(this);
    }

    // ================= MODE =================
    public void setMode(GameMode m) {
        mode = m;
    }

    public GameMode getMode() {
        return mode;
    }

    // ================= PLAYER + AI TURN =================
    public void afterPlayerMove() {

        moves++;                    // count ONLY player move
        allowCycleDetection = true; // cycles only after player

        if (mode == GameMode.GREEDY_COOP) {
            allowCycleDetection = false;
            greedy.makeOneMove();
        }
        else if (mode == GameMode.RANDOM_COOP) {
            allowCycleDetection = false;

            // SAFE RANDOM: rotate only incorrect tiles
            List<Tile> wrong = new ArrayList<>();
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    if (grid[r][c].rotation != grid[r][c].solutionRotation)
                        wrong.add(grid[r][c]);

            if (!wrong.isEmpty())
                wrong.get((int)(Math.random() * wrong.size())).rotate();
        }

        updateWaterFlow();
        detectCycles();
    }

    // ================= GENERATION =================
    public void generateSolvable() {

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                grid[i][j] = new Tile();

        boolean[][] vis = new boolean[rows][cols];
        buildTree(rows / 2, cols / 2, vis);
        addExtraConnections(4);

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                grid[r][c].solutionRotation = grid[r][c].rotation;

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                int correct = grid[r][c].rotation;
                int rot;
                do rot = (int)(Math.random() * 4);
                while (rot == correct);
                grid[r][c].setRotation(rot);
                grid[r][c].startRotation = rot;
            }

        moves = 0;
        allowCycleDetection = false;
        lastCycle = null;
        updateWaterFlow();
    }

    private void buildTree(int r, int c, boolean[][] vis) {
        vis[r][c] = true;
        List<Integer> dirs = Arrays.asList(0,1,2,3);
        Collections.shuffle(dirs);

        for (int d : dirs) {
            int nr = r + dr[d], nc = c + dc[d];
            if (nr<0||nc<0||nr>=rows||nc>=cols||vis[nr][nc]) continue;
            grid[r][c].addConn(d);
            grid[nr][nc].addConn((d+2)%4);
            buildTree(nr,nc,vis);
        }
    }

    private void addExtraConnections(int maxExtra) {
        Random rand = new Random();
        int added = 0;
        while (added < maxExtra) {
            int r = rand.nextInt(rows);
            int c = rand.nextInt(cols);
            int d = rand.nextInt(4);
            int nr = r + dr[d], nc = c + dc[d];
            if (nr<0||nc<0||nr>=rows||nc>=cols) continue;
            if (grid[r][c].hasConn(d)) continue;
            grid[r][c].addConn(d);
            grid[nr][nc].addConn((d+2)%4);
            added++;
        }
    }

    // ================= GAME CONTROLS =================
    public void restart() {
        moves = 0;
        allowCycleDetection = false;
        lastCycle = null;

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                grid[r][c].setRotation(grid[r][c].startRotation);

        updateWaterFlow();
    }

   public void hint() {
    allowCycleDetection = false;

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            Tile t = grid[r][c];

            // Fix EXACTLY one wrong tile
            if (t.getRotation() != t.solutionRotation) {

                // 🔥 DIRECT correction (no rotate loop)
                t.setRotation(t.solutionRotation);

                moves++;
                updateWaterFlow();
                return;   // IMPORTANT: only one tile per hint
            }
        }
    }
}



    public void solve() {
        allowCycleDetection = false;

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                grid[r][c].setRotation(grid[r][c].solutionRotation);

        lastCycle = null;
        updateWaterFlow();
    }

    // ================= UTIL =================
    public void updateWaterFlow() {
        boolean[][] connected = computeConnectedToSource();
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                grid[r][c].hasWater = connected[r][c];
    }

    public int countWaterTiles() {
        int cnt = 0;
        for (Tile[] row : grid)
            for (Tile t : row)
                if (t.hasWater) cnt++;
        return cnt;
    }

    public boolean isSolved() {
        for (Tile[] row : grid)
            for (Tile t : row)
                if (t.rotation != t.solutionRotation)
                    return false;
        return true;
    }

    public boolean[][] computeConnectedToSource() {
        boolean[][] connected = new boolean[rows][cols];
        int sr = rows/2, sc = cols/2;

        Deque<int[]> st = new ArrayDeque<>();
        st.push(new int[]{sr,sc});
        connected[sr][sc] = true;

        while (!st.isEmpty()) {
            int[] p = st.pop();
            int r = p[0], c = p[1];

            for (int d = 0; d < 4; d++) {
                if (!grid[r][c].hasConn(d)) continue;
                int nr = r + dr[d], nc = c + dc[d];
                if (nr<0||nc<0||nr>=rows||nc>=cols) continue;
                if (connected[nr][nc]) continue;
                if (grid[nr][nc].hasConn((d+2)%4)) {
                    connected[nr][nc] = true;
                    st.push(new int[]{nr,nc});
                }
            }
        }
        return connected;
    }

    // ================= CYCLES =================
    public boolean[][] getLastCycle() {
        return lastCycle;
    }

    public void detectCycles() {

        if (!allowCycleDetection || isSolved()) {
            lastCycle = null;
            return;
        }

        lastCycle = new boolean[rows][cols];
        boolean[][] visited = new boolean[rows][cols];
        boolean[][] stack = new boolean[rows][cols];

        dfsCycle(rows/2, cols/2, -1, -1, visited, stack);
    }

    private boolean dfsCycle(int r, int c, int pr, int pc,
                             boolean[][] visited, boolean[][] stack) {

        visited[r][c] = true;
        stack[r][c] = true;

        for (int d = 0; d < 4; d++) {
            if (!grid[r][c].hasConn(d)) continue;
            int nr = r + dr[d], nc = c + dc[d];
            if (nr<0||nc<0||nr>=rows||nc>=cols) continue;
            if (!grid[nr][nc].hasConn((d+2)%4)) continue;

            if (!visited[nr][nc]) {
                if (dfsCycle(nr,nc,r,c,visited,stack)) {
                    lastCycle[r][c] = true;
                    return true;
                }
            }
            else if (stack[nr][nc] && !(nr==pr && nc==pc)) {
                lastCycle[r][c] = true;
                return true;
            }
        }

        stack[r][c] = false;
        return false;
    }
}
