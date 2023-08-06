package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import main.util.Constants;
import main.util.Sounds;
import java.awt.geom.*;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.ArrayList; // Imported ArrayList class

public class ClientFrame extends JFrame {
    private int width, height, size;
    double appleX, appleY;
    private Container contentPane;

    private SnakeSprite mySnake;
    private SnakeSprite enemySnake;

    private DrawingComponent dc;
    private Timer animationTimer;
    private boolean up, down, right, left;

    private Socket socket;
    private int playerID;

    private ReadFromServer rfs;
    private WriteToServer wts;

    private Constants constant = new Constants();
    Random random = new Random();
    Sounds sfx = new Sounds();
    Sounds bg = new Sounds();

    public ClientFrame() {
        width = constant.SCREEN_WIDTH;
        height = constant.SCREEN_HEIGHT;
        size = constant.UNIT_SIZE;
        up = false;
        down = false;
        right = false;
        left = false;
        appleX = (double) constant.SCREEN_WIDTH / 2;
        appleY = (double) constant.SCREEN_WIDTH / 2;
    }

    public void setUpGUI() {
        contentPane = this.getContentPane();
        this.setTitle("Snake Fight Player " + playerID);
        contentPane.setPreferredSize(new Dimension(width, height));
        contentPane.setBackground(Color.BLACK);
        createSprites();
        dc = new DrawingComponent();
        contentPane.add(dc);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setResizable(false);
        setUpAnimationTimer();
        setUpKeyListener();
    }

    // public void spawnApple() {
    // appleX = (double)random.nextInt((int) (constant.SCREEN_WIDTH /
    // constant.UNIT_SIZE))
    // * constant.UNIT_SIZE;
    // appleY = (double)random.nextInt((int) (constant.SCREEN_HEIGHT /
    // constant.UNIT_SIZE)) * constant.UNIT_SIZE;
    // }

    // public void checkApple() {
    // if ((mySnake.getX() == appleX) && (mySnake.getY() == appleY)) {
    // //sound.play("src/main/res/apple_eaten_sound.wav");
    // // mySnake.score();
    // // mySnake.addSegment();
    // spawnApple();
    // }
    // }

    private void createSprites() {
        if (playerID == 1) {
            mySnake = new SnakeSprite(100, 400, size, Color.BLUE, 3, SnakeSprite.Direction.RIGHT);
            enemySnake = new SnakeSprite(500, 400, size, Color.GREEN, 3, SnakeSprite.Direction.LEFT);
        }
        if (playerID == 2) {
            enemySnake = new SnakeSprite(100, 400, size, Color.BLUE, 3, SnakeSprite.Direction.RIGHT);
            mySnake = new SnakeSprite(500, 400, size, Color.GREEN, 3, SnakeSprite.Direction.LEFT);
        }

    }

    private void drawGrid(Graphics2D G) {
        G.setColor(Color.GRAY);
        for (int i = 0; i < width; i += size) {
            G.drawLine(i, 0, i, height);
        }
        for (int i = 0; i < height; i += size) {
            G.drawLine(0, i, width, i);
        }
    }

    private boolean checkCollisions() {
        // Get the head of the player's snake
        Point2D.Double myHead = mySnake.getSegments().get(0);

        // Check for collision with the border
        if (myHead.x < 0 || myHead.x >= width || myHead.y < 0 || myHead.y >= height) {
            sfx.play("src/main/res/collision_wall.wav");
            return true; // Collision with border
        }

        // Check for collision with the opponent's snake
        ArrayList<Point2D.Double> enemySegments = enemySnake.getSegments();
        Point2D.Double enemyHead = enemySegments.get(0); // Get the head of the opponent's snake
        for (int i = 0; i < enemySegments.size(); i++) {
            Point2D.Double enemySegment = enemySegments.get(i);
            if (myHead.equals(enemySegment)) {
                sfx.play("src/main/res/collision_snake.wav");
                return true; // Collision with opponent's snake body
            }
        }
        // Check for collision with the opponent's snake head
        if (myHead.equals(enemyHead)) {
            sfx.play("src/main/res/collision_snake.wav");
            return true; // Collision with opponent's snake head
        }

        // Check for collision with own snake
        ArrayList<Point2D.Double> mySegments = mySnake.getSegments();
        // Start checking from index 1 to avoid checking with the head itself
        for (int i = 1; i < mySegments.size(); i++) {
            Point2D.Double mySegment = mySegments.get(i);
            if (myHead.equals(mySegment)) {
                sfx.play("src/main/res/collision_snake.wav");
                return true; // Collision with own snake body
            }
        }

        return false; // No collisions
    }

    private void gameOver() {
        // stop animation timer
        animationTimer.stop();

        // play game over music
        sfx.play("src/main/res/game_over.wav");

        JOptionPane.showMessageDialog(this, "Game Over", "Game Over", JOptionPane.INFORMATION_MESSAGE);

        // stop background music
        bg.stop();
        this.dispose();
    }

    private void setUpAnimationTimer() {
        int interval = 100;
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (up) {
                    mySnake.snakeMoveVertical(-1);
                } else if (down) {
                    mySnake.snakeMoveVertical(1);
                } else if (right) {
                    mySnake.snakeMoveHorizontal(1);
                } else if (left) {
                    mySnake.snakeMoveHorizontal(-1);
                }

                // Check for collisions
                if (checkCollisions()) {
                    gameOver();
                }

                dc.repaint();
                // checkApple();
            }
        };
        animationTimer = new Timer(interval, al);
        animationTimer.start();
    }

    private void setUpKeyListener() {
        KeyListener kl = new KeyListener() {
            public void keyTyped(KeyEvent ke) {

            }

            public void keyPressed(KeyEvent ke) {
                int keyCode = ke.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_UP:
                        up = true;
                        break;
                    case KeyEvent.VK_DOWN:
                        down = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        right = true;
                        break;
                    case KeyEvent.VK_LEFT:
                        left = true;
                        break;
                }
            }

            public void keyReleased(KeyEvent ke) {
                int keyCode = ke.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_UP:
                        up = false;
                        break;
                    case KeyEvent.VK_DOWN:
                        down = false;
                        break;
                    case KeyEvent.VK_RIGHT:
                        right = false;
                        break;
                    case KeyEvent.VK_LEFT:
                        left = false;
                        break;
                }
            }
        };
        contentPane.addKeyListener(kl);
        contentPane.setFocusable(true);
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 2321);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            playerID = in.readInt();
            System.out.println("You are player " + playerID);
            if (playerID == 1) {
                System.out.println("Waiting for Player 2 to connect ...");
            }
            rfs = new ReadFromServer(in);
            wts = new WriteToServer(out);

            rfs.waitForStartMessage();
        } catch (IOException ex) {
            System.out.println("IOexception from connectToServer");
        }
    }

    private class DrawingComponent extends JComponent {
        protected void paintComponent(Graphics G) {
            Graphics2D G2D = (Graphics2D) G;
            drawGrid(G2D);
            G.setColor(Color.red);
            G.fillOval((int) appleX, (int) appleY, constant.UNIT_SIZE, constant.UNIT_SIZE);
            enemySnake.drawSnake(G2D);
            mySnake.drawSnake(G2D);
        }
    }

    private class ReadFromServer implements Runnable {
        private DataInputStream dataIn;

        public ReadFromServer(DataInputStream in) {
            dataIn = in;
            System.out.println("RFS Runnable created");
        }

        public void run() {
            try {
                while (true) {
                    if (enemySnake != null) {
                        int numSegments = dataIn.readInt(); // Reading the number of segments
                        ArrayList<Point2D.Double> segments = new ArrayList<>();
                        for (int i = 0; i < numSegments; i++) {
                            double x = dataIn.readDouble(); // x coordinate
                            double y = dataIn.readDouble(); // y coordinate
                            segments.add(new Point2D.Double(x, y));
                        }
                        // Update enemySnake with the new segments d
                        enemySnake.setSegments(segments);
                        appleX = dataIn.readDouble();
                        appleY = dataIn.readDouble();

                        // Read the message from the server (if any)
                        String messageFromServer = dataIn.readUTF();
                        if ("AppleEaten".equals(messageFromServer)) {
                            mySnake.addSegment();
                        }
                    }
                }
            } catch (IOException ex) {
                System.out.println("IOexception from ReadFromServer run()");
            }
        }

        public void waitForStartMessage() {
            try {
                String startMessage = dataIn.readUTF();
                System.out.println("Message from server: " + startMessage);

                // play game start sound
                sfx.play("src/main/res/game_start.wav");
                // play background music
                bg.loop("src/main/res/background.wav");

                // after receiving start message, start the thread

                Thread readThread = new Thread(rfs);
                Thread writeThread = new Thread(wts);
                readThread.start();
                writeThread.start();
            } catch (IOException ex) {
                System.out.println("IOexception from ReadFromServer waitForStartMessage()");
            }
        }
    }

    private class WriteToServer implements Runnable {
        private DataOutputStream dataOut;

        public WriteToServer(DataOutputStream out) {
            dataOut = out;
            System.out.println("WTS Runnable created");
        }

        public void run() {
            try {
                while (true) {
                    if (mySnake != null) {
                        ArrayList<Point2D.Double> segments = mySnake.getSegments();
                        dataOut.writeInt(segments.size()); // Sending the number of segments
                        for (Point2D.Double segment : segments) {
                            dataOut.writeDouble(segment.x);
                            dataOut.writeDouble(segment.y);
                        }
                        dataOut.flush();
                    }
                }
            } catch (IOException ex) {
                System.out.println("IOexception from WriteToServer run()");
            }
        }
    }

    public static void main(String[] args) {
        ClientFrame cf = new ClientFrame();
        cf.connectToServer();
        cf.createSprites();
        cf.setUpGUI();
    }

}
