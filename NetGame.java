import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class NetGame {

    private static Board board; // make it static to be accessible by modeBox
    private static NetPanel panel;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Net Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            board = new Board(5, 5);
            JLabel activeLabel = new JLabel(
                "Active: " + board.countWaterTiles() + " | Moves: " + board.moves
            );

            panel = new NetPanel(board, activeLabel);

            JButton restart = new JButton("Restart");
            JButton hint = new JButton("Hint");
            JButton solve = new JButton("Solve");
            JButton newGame = new JButton("New Game");

            restart.addActionListener(e -> {
                board.restart();
                activeLabel.setText(
                    "Active: " + board.countWaterTiles() + " | Moves: " + board.moves
                );
                panel.repaint();
            });

            hint.addActionListener(e -> {
                board.hint();
                activeLabel.setText(
                    "Active: " + board.countWaterTiles() + " | Moves: " + board.moves
                );
                panel.repaint();
            });

            solve.addActionListener(e -> {
                board.solve();
                activeLabel.setText(
                    "Active: " + board.countWaterTiles() + " | Moves: " + board.moves
                );
                panel.repaint();
            });

            newGame.addActionListener(e -> {
                frame.getContentPane().remove(panel);
               Board newBoard = new Board(5, 5);
newBoard.setMode(board.getMode()); // copy current mode
panel = new NetPanel(newBoard, activeLabel);
board = newBoard;


                frame.add(panel, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            });

            String[] modes = {"Single Player", "Greedy Co-op", "Random Co-op"};
            JComboBox<String> modeBox = new JComboBox<>(modes);

            // Initial mode selection
            modeBox.setSelectedIndex(0);

            // Update board mode whenever dropdown changes
            modeBox.addActionListener(e -> {
                int i = modeBox.getSelectedIndex();
                if(i==0) board.setMode(GameMode.SINGLE_PLAYER);
                if(i==1) board.setMode(GameMode.GREEDY_COOP);
                if(i==2) board.setMode(GameMode.RANDOM_COOP);
            });

            JPanel controls = new JPanel();
            controls.add(restart);
            controls.add(hint);
            controls.add(solve);
            controls.add(newGame);
            controls.add(new JLabel("Mode:"));
            controls.add(modeBox);
            controls.add(activeLabel);

            frame.add(panel, BorderLayout.CENTER);
            frame.add(controls, BorderLayout.SOUTH);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
