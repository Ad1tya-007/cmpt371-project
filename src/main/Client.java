package main;

import javax.swing.*;
import java.awt.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import main.util.Constants;

public class Client extends JFrame {
    private int id;
    private int score;
    private ClientConnect cc;

    private Constants constants = new Constants();

    public Client() {
        score = 0;
    }

    // Set up gameboard interface
    public void setUpBoard() {
        this.setPreferredSize(new Dimension(constants.SCREEN_WIDTH, constants.SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.setTitle("Snake Fight Player " + id);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    // Create new player connection
    private void connect_Sever() {
        cc = new ClientConnect();
    }

    // Set up client connection
    private class ClientConnect {
        private Socket s;
        private DataInputStream in;
        private DataOutputStream out;

        // Get inputStream and outputStream from Server to read from server.
        public ClientConnect() {
            try {
                s = new Socket("localhost", 2321);
                in = new DataInputStream(s.getInputStream());
                out = new DataOutputStream(s.getOutputStream());
                id = in.readInt();
                System.out.println("Player ID is: " + id);
            } catch (IOException ex) {
                System.out.println("ClientConnect");
            }
        }
    }

    // Main function
    public static void main(String[] args) {
        Client c = new Client();
        c.connect_Sever();
        c.setUpBoard();
    }

}
