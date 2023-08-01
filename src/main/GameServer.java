package main;

import java.io.*;
import java.net.*;

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

    private double p1x, p1y, p2x, p2y;

    public GameServer() {
        System.out.println("Game Server started");
        numPlayers = 0;
        maxPlayers = 2;

        // intial coordiantes of snakes
        p1x = 100;
        p1y = 400;

        // intial coordiantes of snakes
        p2x = 490;
        p2y = 400;

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
                    if (playerID == 1) {
                        p1x = dataIn.readDouble();
                        p1y = dataIn.readDouble();
                    }
                    if (playerID == 2) {
                        p2x = dataIn.readDouble();
                        p2y = dataIn.readDouble();
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
                    if (playerID == 1) {
                        dataOut.writeDouble(p2x);
                        dataOut.writeDouble(p2y);
                        dataOut.flush();
                    }
                    if (playerID == 2) {
                        dataOut.writeDouble(p1x);
                        dataOut.writeDouble(p1y);
                        dataOut.flush();
                    }
                    try {
                        Thread.sleep(25); // prevent overwhelming the network
                    } catch (InterruptedException ex) {
                        System.out.println("InterruptedException from WriteToClient run()");
                    }
                }
            } catch (IOException ex) {
                System.out.println("IOexception from WriteToClient run()");
            }
        }

        public void sendStartMessage() {
            try {
                dataOut.writeUTF("We now have two players. Start the match!");
            } catch (IOException ex) {
                System.out.println("IOexception from WriteToClient sendStartMessage()");
            }
        }
    }

    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.acceptConnections();
    }
}
