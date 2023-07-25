package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class Server {
    private ServerSocket ss;
    private int player_num;
    private ServerConnect player1;
    private ServerConnect player2;

    public Server() {
        player_num = 0;
        try {
            ss = new ServerSocket(2321);
        } catch (IOException ex) {
            System.out.println("Error: Failed to create Server");
        }
    }

    /*
     * Function to connect clients
     * Displays in terminal which player has joined
     */
    public void connect_acc() {
        try {
            System.out.println("waiting...");
            while (player_num < 2) {
                Socket s = ss.accept();
                player_num++;
                System.out.println("Player " + player_num + " has joined");
                ServerConnect sc = new ServerConnect(s, player_num);
                if (player_num == 1)
                    player1 = sc;
                else
                    player2 = sc;

                Thread t = new Thread(sc);
                t.start();
            }
            System.out.println("Max players joined.");
        } catch (IOException ex) {
            System.out.println("connect_acc");
        }
    }

    private class ServerConnect implements Runnable {
        private Socket s;
        private DataInputStream in;
        private DataOutputStream out;
        private int playerID;

        public ServerConnect(Socket socket, int id) {
            s = socket;
            playerID = id;
            try {
                in = new DataInputStream(s.getInputStream());
                out = new DataOutputStream(s.getOutputStream());
            } catch (IOException ex) {
                System.out.println("ServerConnect");
            }
        }

        /*
         * Server class run function
         */
        public void run() {
            try {
                out.writeInt(playerID);
                out.flush();

                // Handle multiple players
                // while (true) {
                // if (playerID == 1) {
                // System.out.println("Player 1's snake has done something");
                // } else {
                // System.out.println("Player 2's snake has done something");
                // }
                // }
            } catch (IOException ex) {
                System.out.println("Player has left the server");
            }
        }

    }

    public static void main(String[] args) {
        Server s = new Server();
        s.connect_acc();
    }

}