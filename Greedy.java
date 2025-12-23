public class Greedy {
    private Board board;

    public Greedy(Board b) {
        this.board = b;
    }

    public void makeOneMove() {
        // AI logic here...
        int bestR = -1, bestC = -1, bestRot = 0, bestGain = Integer.MIN_VALUE;
        int currentWater = board.countWaterTiles();

        for (int r = 0; r < board.rows; r++) {
            for (int c = 0; c < board.cols; c++) {
                Tile t = board.grid[r][c];
                if (t.getRotation() == t.solutionRotation) continue;

                int originalRotation = t.getRotation();
                int rotCount = 0;

                for (int i = 0; i < 4; i++) {
                    t.rotate();
                    rotCount++;
                    board.updateWaterFlow();
                    int gain = board.countWaterTiles() - currentWater;
                    gain -= Math.abs(t.getRotation() - t.solutionRotation);

                    if (gain > bestGain) {
                        bestGain = gain;
                        bestR = r;
                        bestC = c;
                        bestRot = rotCount;
                    }
                }
                while (t.getRotation() != originalRotation) t.rotate();
            }
        }

        if (bestR != -1) {
            for (int i = 0; i < bestRot; i++)
                board.grid[bestR][bestC].rotate();
            board.updateWaterFlow();
        }
    }
}