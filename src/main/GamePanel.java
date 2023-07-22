import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import util.Constants;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener{

    Constants constants = new Constants();

    // these two hold x and y coordinates of the snakes and apple
    final int x[] = new int[constants.GAME_UNITS];
    final int y[] = new int[constants.GAME_UNITS];

    int bodyParts = 3;
    int applesEaten = 0;
    int appleX, appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(constants.SCREEN_WIDTH, constants.SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame(){
        createApple();
        running = true;
        timer = new Timer(constants.DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics G) {
        super.paintComponent(G);
        draw(G);
    }

    public void draw(Graphics G){
        // draw the board lines
        for(int i=0; i<(int)(constants.SCREEN_HEIGHT/constants.UNIT_SIZE);i++){
            G.setColor(Color.white);
            G.drawLine(i*constants.UNIT_SIZE, 0, i*constants.UNIT_SIZE, constants.SCREEN_HEIGHT);
            G.drawLine(0, i*constants.UNIT_SIZE, constants.SCREEN_WIDTH, i*constants.UNIT_SIZE);
        }

        // draw the apple
        G.setColor(Color.red);
        G.fillOval(appleX, appleY, constants.UNIT_SIZE, constants.UNIT_SIZE);
        
        // draw the snake
        for(int i=0; i<bodyParts; i++){
            G.setColor(Color.green);
            G.fillRect(x[i], y[i], constants.UNIT_SIZE, constants.UNIT_SIZE);    
        }
    }

    public void createApple(){
        appleX = random.nextInt((int)(constants.SCREEN_WIDTH/constants.UNIT_SIZE))*constants.UNIT_SIZE;
        appleY = random.nextInt((int)(constants.SCREEN_HEIGHT/constants.UNIT_SIZE))*constants.UNIT_SIZE;
    }

    public void move(){
        for(int i = bodyParts;i>0;i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch(direction) {
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

    public void checkApple(){

    }

    public void checkCollisions(){
        // check if head collides with body
        for(int i=bodyParts; i>0; i--){
            if((x[0] == x[i])&&(y[0] == y[i])) {
                running = false;
            }
        }
    }

    public void gameOver(Graphics G){

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){

        }
    }
    
}
