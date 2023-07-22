import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import util.Constants;
import util.Drawer;

public class GamePanel extends JPanel implements ActionListener {

    Constants constants = new Constants();
    Drawer drawer = new Drawer();
    // Apple apple = new Apple();

    // these two hold x and y coordinates of the snakes and apple
    final int x[] = new int[constants.GAME_UNITS];
    final int y[] = new int[constants.GAME_UNITS];

    int bodyParts = 3;
    int applesEaten = 0;
    int appleX, appleY;
    int direction = 'R';
    int score = 0;
    boolean running = false;
    Timer timer;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(constants.SCREEN_WIDTH, constants.SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        createApple();
        running = true;
        timer = new Timer(constants.DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics G) {
        super.paintComponent(G);
        draw(G);
    }

    public void draw(Graphics G) {
        if (running) {
            // drawer.drawLines(G);
            drawer.drawApple(G, appleX, appleY);
            drawer.drawSnake(G, x, y, bodyParts);
            displayScore(G);
        } else {
            displayScore(G);
            displayGameOver(G);
        }
    }

    public void createApple() {
        appleX = random.nextInt((int) (constants.SCREEN_WIDTH / constants.UNIT_SIZE))
                * constants.UNIT_SIZE;
        appleY = random.nextInt((int) (constants.SCREEN_HEIGHT /
                constants.UNIT_SIZE)) * constants.UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - constants.UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + constants.UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - constants.UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + constants.UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            score++;
            createApple();
        }
    }

    public void checkCollisions() {
        // check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        // check if head collides with border
        // left border
        if (x[0] < 0) {
            running = false;
        }
        // right border
        if (x[0] > constants.SCREEN_WIDTH - 1) {
            running = false;
        }
        // top border
        if (y[0] < 0) {
            running = false;
        }
        // bottom border
        if (y[0] > constants.SCREEN_HEIGHT - 1) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void displayScore(Graphics G) {
        G.setColor(Color.white);
        G.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics = getFontMetrics(G.getFont());
        G.drawString("User: " + "test", (constants.SCREEN_WIDTH / 4 - metrics.stringWidth("User: " + "test") / 2),
                G.getFont().getSize());
        G.drawString("Score: " + score, ((3 * constants.SCREEN_WIDTH) / 4 - metrics.stringWidth("Score: " + score) / 2),
                G.getFont().getSize());
    }

    public void displayGameOver(Graphics G) {
        displayScore(G);
        // game over text
        G.setColor(Color.white);
        G.setFont(new Font("Arial", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(G.getFont());
        G.drawString("Game Over", (constants.SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2,
                constants.SCREEN_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }

}
