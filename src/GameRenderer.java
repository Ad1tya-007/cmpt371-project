import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameRenderer extends JFrame {
    private GameBoard gameBoard;

    public GameRenderer(GameBoard gameBoard) {
        this.gameBoard = gameBoard;

        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);

        JPanel boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                renderGameBoard(g);
            }
        };

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameBoard.updateBoard();
                boardPanel.repaint();
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(boardPanel, BorderLayout.CENTER);
        getContentPane().add(updateButton, BorderLayout.SOUTH);
    }

    private void renderGameBoard(Graphics g) {
        int cellSize = Math.min(getWidth() / gameBoard.getSize(), getHeight() / gameBoard.getSize());

        for (int i = 0; i < gameBoard.getSize(); i++) {
            for (int j = 0; j < gameBoard.getSize(); j++) {
                int cellValue = gameBoard.getBoard()[i][j];
                Color cellColor;

                switch (cellValue) {
                    case 0: // Empty cell
                        cellColor = Color.WHITE;
                        break;
                    case 1: // Apple
                        cellColor = Color.RED;
                        break;
                    case 2: // Snake 1
                        cellColor = Color.GREEN;
                        break;
                    case 3: // Snake 2
                        cellColor = Color.BLUE;
                        break;
                    default:
                        cellColor = Color.BLACK;
                        break;
                }

                g.setColor(cellColor);
                g.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
            }
        }
    }

    public static void main(String[] args) {
        int size = 20;
        Snake snake1 = new Snake(10, 10);
        Snake snake2 = new Snake(15, 15);
        GameBoard gameBoard = new GameBoard(size, snake1, snake2);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GameRenderer gameRenderer = new GameRenderer(gameBoard);
                gameRenderer.setVisible(true);
            }
        });
    }
}
