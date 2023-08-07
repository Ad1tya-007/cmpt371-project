
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

// class for handling client
public class ClientFrame extends JFrame {
    private int width, height, size;
    // Apple coordinates.
    double appleX, appleY;
    private Container contentPane;

    // Snake objects for clients.
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

    // initialize variables of the client frame
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

    // function to setup the GUI of the client
    public void setUpGUI() {
        contentPane = this.getContentPane();
        this.setTitle("Snake Fight Player " + playerID);
        contentPane.setPreferredSize(new Dimension(width, height));
        contentPane.setBackground(Color.BLACK);
        // Initializes snakes and sets their location on the window.
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

    // function to initialize the snakes for the clients
    private void createSprites() {
        // if client is player1
        if (playerID == 1) {
            mySnake = new SnakeSprite(100, 400, size, Color.BLUE, 3, SnakeSprite.Direction.RIGHT);
            enemySnake = new SnakeSprite(500, 400, size, Color.GREEN, 3, SnakeSprite.Direction.LEFT);
        }
        // if client is player2
        if (playerID == 2) {
            enemySnake = new SnakeSprite(100, 400, size, Color.BLUE, 3, SnakeSprite.Direction.RIGHT);
            mySnake = new SnakeSprite(500, 400, size, Color.GREEN, 3, SnakeSprite.Direction.LEFT);
        }

    }

    // draw the grid of the board
    private void drawGrid(Graphics2D G) {
        G.setColor(Color.GRAY);
        for (int i = 0; i < width; i += size) {
            G.drawLine(i, 0, i, height);
        }
        for (int i = 0; i < height; i += size) {
            G.drawLine(0, i, width, i);
        }
    }

    // this function checks for collisions of the clients snake with the wall, enemy
    // snake and itself
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

    // display a dialog saying "GAME OVER" when the client snake dies
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

    // set up the animation timer of the game
    private void setUpAnimationTimer() {
        int interval = 100;
        // sees which key was pressed and performs actions of the snake
        // followed by checking for collisions
        // repainting the snake on the board
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // Controls snake's direction.
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
            }
        };
        animationTimer = new Timer(interval, al);
        animationTimer.start();
    }

    // function to see which key was pressed or released
    private void setUpKeyListener() {
        KeyListener kl = new KeyListener() {
            public void keyTyped(KeyEvent ke) {

            }

            // function to see which key was pressed
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

            // function to see which key was released
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

        // add keyListner to the game
        contentPane.addKeyListener(kl);
        contentPane.setFocusable(true);
    }

    // function to connect client to the server
    private void connectToServer() {
        try {
            // replace with your server ip address
            // localhost - 127.0.0.1
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 2321);
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

    // class to draw the elements in the board
    // grid, apple and snakes
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

    // class to read data from the server
    private class ReadFromServer implements Runnable {
        private DataInputStream dataIn;

        public ReadFromServer(DataInputStream in) {
            dataIn = in;
            System.out.println("RFS Runnable created");
        }

        // automatically read data from the server
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

        // wait for a start message from server before starting the game
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

    // class to write data to the server
    private class WriteToServer implements Runnable {
        private DataOutputStream dataOut;

        public WriteToServer(DataOutputStream out) {
            dataOut = out;
            System.out.println("WTS Runnable created");
        }

        // automaticaly write data to the client
        public void run() {
            try {
                while (true) {
                    if (mySnake != null) {
                        ArrayList<Point2D.Double> segments = mySnake.getSegments();
                        dataOut.writeInt(segments.size()); // Sending the number of segments
                        // Send snake coordinates to server.
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

    // main function to run the clientFrame
    public static void main(String[] args) {
        ClientFrame cf = new ClientFrame();
        cf.connectToServer();
        cf.createSprites();
        cf.setUpGUI();
    }

}
