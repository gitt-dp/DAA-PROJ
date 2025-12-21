import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class NetPanel extends JPanel {

    private Board board;
    private JLabel info;
    private int size = 70;

    public NetPanel(Board b, JLabel label) {
        board = b;
        info = label;

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {

                if (board.isSolved()) return;

                int c = e.getX() / size;
                int r = e.getY() / size;

                if (r < 0 || c < 0 || r >= board.rows || c >= board.cols)
                    return;

                // PLAYER move
                board.grid[r][c].rotate();

                // AI + bookkeeping
                board.afterPlayerMove();

                updateUIStatus();
            }
        });
    }
private void updateUIStatus() {
    info.setText("Active: " + board.countWaterTiles() + " | Moves: " + board.moves);
    info.revalidate();   // 🔥 FIX
    info.repaint();      // 🔥 FIX
    repaint();
}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(10));

        for (int r = 0; r < board.rows; r++)
            for (int c = 0; c < board.cols; c++)
                drawTile(g2, r, c);

        if (board.isSolved()) {
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            g2.drawString(
                "YOU WIN in " + board.moves + " moves",
                40, getHeight() / 2
            );
        }
    }

    private void drawTile(Graphics2D g, int r, int c) {

        int x = c * size;
        int y = r * size;
        Tile t = board.grid[r][c];

        // tile background
        g.setColor(new Color(176, 190, 174));
        g.fillRect(x, y, size, size);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, size, size);

        int cx = x + size / 2;
        int cy = y + size / 2;

        int sr = board.rows / 2;
        int sc = board.cols / 2;

        // SOURCE TILE
        if (r == sr && c == sc) {
            g.setColor(Color.BLACK);
            g.fillRect(cx - 10, cy - 10, 20, 20);
        }

        // PIPE COLOR LOGIC (matches your friend's)
      boolean[][] cycle = board.getLastCycle();

if (cycle != null && cycle[r][c]) {
    g.setColor(Color.RED);
} else if (t.hasWater || board.isSolved()) {
    g.setColor(Color.CYAN);
} else {
    g.setColor(Color.DARK_GRAY);
}


        if (t.hasConn(0)) g.drawLine(cx, cy, cx, y);
        if (t.hasConn(1)) g.drawLine(cx, cy, x + size, cy);
        if (t.hasConn(2)) g.drawLine(cx, cy, cx, y + size);
        if (t.hasConn(3)) g.drawLine(cx, cy, x, cy);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(board.cols * size, board.rows * size);
    }
}
