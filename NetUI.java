import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import javax.imageio.ImageIO;

public class NetUI {

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cards;
    private Board board;
    private NetPanel netPanel;

    public NetUI() {
        frame = new JFrame("Net Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 900);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        // --- Front Page ---
        JPanel frontPage = new JPanel(new GridBagLayout());
        frontPage.setBackground(new Color(10, 25, 50));

        JButton playButton = new JButton("NET GAME");
        playButton.setFont(new Font("Segoe UI", Font.BOLD, 48));
        playButton.setForeground(Color.WHITE);
        playButton.setBackground(new Color(0, 0, 128)); // navy blue
        playButton.setFocusPainted(false);
        playButton.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        // Octopus icon
        try {
            BufferedImage octopus = ImageIO.read(new File("octopus_play.png"));
            ImageIcon icon = new ImageIcon(octopus.getScaledInstance(120, 120, Image.SCALE_SMOOTH));
            playButton.setIcon(icon);
            playButton.setHorizontalTextPosition(SwingConstants.CENTER);
            playButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Hover effect
        playButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { playButton.setBackground(new Color(0, 0, 180)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { playButton.setBackground(new Color(0, 0, 128)); }
        });

        frontPage.add(playButton);
        cards.add(frontPage, "FRONT");

        // --- Mode Selection Page ---
        JPanel modePage = new JPanel(new GridBagLayout());
        modePage.setBackground(new Color(10, 25, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton single = createStyledButton("Single Player", 26, new Color(0, 120, 180));
        JButton greedy = createStyledButton("Greedy Co-op", 26, new Color(0, 120, 180));
        JButton random = createStyledButton("Random Co-op", 26, new Color(0, 120, 180));

        gbc.gridx = 0; gbc.gridy = 0; modePage.add(single, gbc);
        gbc.gridy = 1; modePage.add(greedy, gbc);
        gbc.gridy = 2; modePage.add(random, gbc);

        cards.add(modePage, "MODE");

        // --- Game setup ---
        board = new Board(5, 5);
        netPanel = new NetPanel(board);
        netPanel.setOpaque(false);

        JPanel gameContainer = new JPanel(null);
        gameContainer.setBackground(new Color(188, 184, 138)); // sage green

        // Board placement
        netPanel.setBounds(50, 50, board.cols * 100, board.rows * 100);
        gameContainer.add(netPanel);

        // Controls panel at bottom
        JPanel controls = new JPanel();
        controls.setBackground(new Color(0, 0, 0, 80)); // semi-transparent
        JButton restart = createStyledButton("Restart", 18, new Color(0, 120, 180));
        JButton hint = createStyledButton("Hint", 18, new Color(0, 120, 180));
        JButton solve = createStyledButton("Solve", 18, new Color(0, 120, 180));
        JButton newGame = createStyledButton("New Game", 18, new Color(0, 120, 180));

        controls.add(restart);
        controls.add(hint);
        controls.add(solve);
        controls.add(newGame);
        controls.setBounds(50, board.rows * 100 + 80, 700, 60);
        gameContainer.add(controls);

        // Exit button top-right
        JButton exit = createStyledButton("Exit", 18, new Color(200, 60, 60));
        exit.setBounds(760, 10, 100, 50);
        gameContainer.add(exit);
        exit.addActionListener(e -> {
            cardLayout.show(cards, "FRONT");
            board.restart();
            netPanel.repaint();
        });

        // Button actions
        playButton.addActionListener(e -> cardLayout.show(cards, "MODE"));
        single.addActionListener(e -> startGame(GameMode.SINGLE_PLAYER));
        greedy.addActionListener(e -> startGame(GameMode.GREEDY_COOP));
        random.addActionListener(e -> startGame(GameMode.RANDOM_COOP));

        restart.addActionListener(e -> { board.restart(); netPanel.repaint(); });
        hint.addActionListener(e -> { if(board.hint()) netPanel.repaint(); });
        solve.addActionListener(e -> { board.solve(); netPanel.repaint(); });
        newGame.addActionListener(e -> { board.generateSolvable(); netPanel.repaint(); });

        cards.add(gameContainer, "GAME");

        frame.add(cards);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        cardLayout.show(cards, "FRONT");
    }

    private JButton createStyledButton(String text, int fontSize, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(bg.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(bg); }
        });
        return btn;
    }

    private void startGame(GameMode mode) {
        board.setMode(mode);
        board.generateSolvable();
        netPanel.repaint();
        cardLayout.show(cards, "GAME");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NetUI::new);
    }
}
