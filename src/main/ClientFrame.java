package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import main.util.Constants;
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
    Random random= new Random();

    public ClientFrame() {
        width = constant.SCREEN_WIDTH;
        height = constant.SCREEN_HEIGHT;
        size = constant.UNIT_SIZE;
        up = false;
        down = false;
        right = false;
        left = false;
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
        if (playerID== 1) {
            spawnApple();
        }
    }

    public void spawnApple() {
        appleX = random.nextInt((int) (constant.SCREEN_WIDTH / constant.UNIT_SIZE))
                * constant.UNIT_SIZE;
        appleY = random.nextInt((int) (constant.SCREEN_HEIGHT /
                constant.UNIT_SIZE)) * constant.UNIT_SIZE;
    }

    public void checkApple() {
        if ((mySnake.getX() == appleX) && (mySnake.getY() == appleY)) {
            //sound.play("src/main/res/apple_eaten_sound.wav");
            mySnake.score();
            mySnake.addSegment();
            spawnApple();
        }
    }

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
        for (int i = 0; i < width; i += size) {
            G.drawLine(i, 0, i, height);
        }
        for (int i = 0; i < height; i += size) {
            G.drawLine(0, i, width, i);
        }
    }

    private void setUpAnimationTimer() {
        int interval = 100;
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (up) {
                    mySnake.snakeMoveVertical(-1);
                }
                if (down) {
                    mySnake.snakeMoveVertical(1);
                }
                if (right) {
                    mySnake.snakeMoveHorizontal(1);
                }
                if (left) {
                    mySnake.snakeMoveHorizontal(-1);
                }
                dc.repaint();
                checkApple();
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
            enemySnake.drawSnake(G2D);
            mySnake.drawSnake(G2D);
            G.setColor(Color.red);
            G.fillOval((int)appleX, (int)appleY, constant.UNIT_SIZE, constant.UNIT_SIZE);
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
                        //appleX= dataIn.readDouble();
                        //appleY= dataIn.readDouble();
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
                        //dataOut.writeDouble(appleX);
                        //dataOut.writeDouble(appleY);
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
