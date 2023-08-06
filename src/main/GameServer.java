package main;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.geom.*;
import main.util.Constants;

public class GameServer {
    private ServerSocket ss;
    private int numPlayers;
    private int maxPlayers;

    private Socket p1Socket;
    private Socket p2Socket;
    private ReadFromClient p1RFC;
    private ReadFromClient p2RFC;
    private WriteToClient p1WTC;
    private WriteToClient p2WTC;
    private Point2D.Double apple = new Point2D.Double(); // initialized;
    private volatile int appleEatenBy = 0;  // 0 means no one, 1 means Player 1, and 2 means Player 2


    private ArrayList<double[]> p1Positions = new ArrayList<>();
    private ArrayList<double[]> p2Positions = new ArrayList<>();

    private Constants constant = new Constants();
    Random random= new Random();

    public GameServer() {
        System.out.println("Game Server started");
        numPlayers = 0;
        maxPlayers = 2;
        spawnApple();

        // intial coordiantes of snakes
        double[] p1InitialPos = {100, 400}; // x and y coordinates for player 1
        double[] p2InitialPos = {500, 400}; // x and y coordinates for player 2
        p1Positions.add(p1InitialPos);
        p2Positions.add(p2InitialPos);

        try {
            ss = new ServerSocket(2321);
        } catch (IOException ex) {
            System.out.println("IOexception from GameServer Constructor");
        }
    }

    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections...");
            while (numPlayers <= maxPlayers) {
                Socket s = ss.accept();
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                // once player has connected
                numPlayers++;
                out.writeInt(numPlayers);
                System.out.println("Player " + numPlayers + " has connected");

                ReadFromClient rfc = new ReadFromClient(numPlayers, in);
                WriteToClient wtc = new WriteToClient(numPlayers, out);

                if (numPlayers == 1) {
                    p1Socket = s;
                    p1RFC = rfc;
                    p1WTC = wtc;
                }
                if (numPlayers == 2) {
                    p2Socket = s;
                    p2RFC = rfc;
                    p2WTC = wtc;
                    p1WTC.sendStartMessage();
                    p2WTC.sendStartMessage();

                    // now both players have joined the game
                    // start the thread to read and write to client
                    Thread readThreadP1 = new Thread(p1RFC);
                    Thread readThreadP2 = new Thread(p2RFC);

                    readThreadP1.start();
                    readThreadP2.start();

                    Thread writeThreadP1 = new Thread(p1WTC);
                    Thread writeThreadP2 = new Thread(p2WTC);

                    writeThreadP1.start();
                    writeThreadP2.start();
                }
            }
            System.out.println("No longer accepting connections");

        } catch (IOException ex) {
            System.out.println("IOexception from acceptConnections");
        }
    }

    private class ReadFromClient implements Runnable {
        private int playerID;
        private DataInputStream dataIn;

        public ReadFromClient(int pid, DataInputStream in) {
            playerID = pid;
            dataIn = in;
            System.out.println("RFC " + playerID + " Runnable created");
        }

        public void run() {
            try {
                while (true) {
                    int numSegments = dataIn.readInt(); // Reading the number of segments
                    ArrayList<double[]> positions = playerID == 1 ? p1Positions : p2Positions;
                    synchronized (positions) {
                        positions.clear(); // Clear previous positions
                        double[] position = new double[2];
                        position[0] = dataIn.readDouble(); // x coordinate
                        position[1] = dataIn.readDouble(); // y coordinate
                        positions.add(position);
                        
                        checkApple(position[0], position[1], playerID); // Check the first position

                        for (int i = 1; i < numSegments; i++) {
                            position = new double[2];
                            position[0] = dataIn.readDouble(); // x coordinate
                            position[1] = dataIn.readDouble(); // y coordinate
                            positions.add(position);
                        }
                    }
                }
            } catch (IOException ex) {
                System.out.println("IOexception from ReadFromClient run()");
            }
        }
    }

    private class WriteToClient implements Runnable {
        private int playerID;
        private DataOutputStream dataOut;

        public WriteToClient(int pid, DataOutputStream out) {
            playerID = pid;
            dataOut = out;
            System.out.println("WTC " + playerID + " Runnable created");
        }

        public void run() {
            try {
                while (true) {
                    ArrayList<double[]> positions = playerID == 1 ? p2Positions : p1Positions;
                    synchronized (positions) {
                        if (!positions.isEmpty()) {
                            dataOut.writeInt(positions.size()); // Sending the number of positions
                            for (double[] position : positions) {
                                dataOut.writeDouble(position[0]);
                                dataOut.writeDouble(position[1]);
                            }
                            dataOut.flush();
                        }
                    }
                    // Read appleX and appleY from shared location
                    dataOut.writeDouble(apple.x);
                    dataOut.writeDouble(apple.y);
                    if (appleEatenBy == playerID) {
                        notifyAppleEaten();
                        appleEatenBy = 0;  // Reset the flag
                    }else{
                        dataOut.writeUTF("No Apple Eaten");
                    }
                    
                    dataOut.flush();
                    Thread.sleep(25); // prevent overwhelming the network
                }
            } catch (IOException | InterruptedException ex) {
                System.out.println("Exception from WriteToClient run()");
            }
        }
        
        public void sendStartMessage() {
            try {
                dataOut.writeUTF("We now have two players. Start the match!");
            } catch (IOException ex) {
                System.out.println("IOexception from WriteToClient sendStartMessage()");
            }
        }
        
        public void notifyAppleEaten() {
            try {
                dataOut.writeUTF("AppleEaten"); // a simple message to denote apple is eaten
            } catch (IOException ex) {
                System.out.println("IOexception from WriteToClient notifyAppleEaten()");
            }
        }
    }

    //Apple related code
    public void spawnApple() {
        apple.x = (double)random.nextInt((int) (constant.SCREEN_WIDTH / constant.UNIT_SIZE))
                * constant.UNIT_SIZE;
        apple.y= (double)random.nextInt((int) (constant.SCREEN_HEIGHT /
                constant.UNIT_SIZE)) * constant.UNIT_SIZE;
    }

    public void checkApple(double x, double y, int playerID) {
        if ((x == apple.x) && (y == apple.y)) {
            //sound.play("src/main/res/apple_eaten_sound.wav");
            // mySnake.score();
            // mySnake.addSegment();
            spawnApple();
            appleEatenBy = playerID;
        }
    }
    

    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.acceptConnections();
    }
}
