package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import main.util.Constants;

import java.io.*;
import java.net.*;

public class ClientFrame extends JFrame {
    private int width, height, size;
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
    }

    private void createSprites() {
        if (playerID == 1) {
            mySnake = new SnakeSprite(100, 400, size, Color.BLUE);
            enemySnake = new SnakeSprite(490, 400, size, Color.GREEN);
        }
        if (playerID == 2) {
            enemySnake = new SnakeSprite(100, 400, size, Color.BLUE);
            mySnake = new SnakeSprite(490, 400, size, Color.GREEN);
        }

    }

    private void setUpAnimationTimer() {
        int interval = 10;
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                double speed = 5;
                if (up) {
                    // System.out.println(mySnake.getX() + "&" + mySnake.getY());
                    mySnake.snakeMoveVertical(-speed);
                }
                if (down) {
                    // System.out.println(mySnake.getX() + "&" + mySnake.getY());
                    mySnake.snakeMoveVertical(+speed);
                }
                if (right) {
                    // System.out.println(mySnake.getX() + "&" + mySnake.getY());
                    mySnake.snakeMoveHorizontal(+speed);
                }
                if (left) {
                    // System.out.println(mySnake.getX() + "&" + mySnake.getY());
                    mySnake.snakeMoveHorizontal(-speed);
                }
                dc.repaint();
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
                        enemySnake.setX(dataIn.readDouble());
                        enemySnake.setY(dataIn.readDouble());
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
                // keep updating server about your snakes location
                while (true) {
                    if (mySnake != null) {
                        dataOut.writeDouble(mySnake.getX());
                        dataOut.writeDouble(mySnake.getY());
                        dataOut.flush();
                    }
                    try {
                        Thread.sleep(25); // prevent overwhelming the network
                    } catch (InterruptedException ex) {
                        System.out.println("InterruptedException from WriteToServer run()");
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
        cf.setUpGUI();
    }

}
