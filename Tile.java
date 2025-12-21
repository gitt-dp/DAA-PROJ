public class Tile {

    boolean[] conn = new boolean[4];
    int rotation = 0;
    int solutionRotation = 0;
    int startRotation = 0;
    public boolean hasWater = false;

    public boolean hasConn(int d) {
        return conn[(d - rotation + 4) % 4];
    }

    public void rotate() {
        rotation = (rotation + 1) % 4;
    }

    // ✅ ADD THIS METHOD
    public void rotateBack() {
        rotation = (rotation + 3) % 4; // reverse rotation
    }

    public void setRotation(int r) {
        rotation = r % 4;
    }

    public int getRotation() {
        return rotation;
    }

    public void addConn(int d) {
        conn[d] = true;
    }
}
