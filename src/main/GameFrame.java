import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;

public class GameFrame extends JFrame{

    private GamePanel gamePanel;
    
    GameFrame(){
        gamePanel = new GamePanel();
        this.add(gamePanel);
        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> gamePanel.restartGame()); // Using a lambda expression
        this.add(restartButton, BorderLayout.SOUTH); // Adds button below the game panel
        this.setTitle("Snake Fight");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}
