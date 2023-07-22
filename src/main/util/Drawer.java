package util;

import java.awt.*;

public class Drawer {
    Constants constants = new Constants();

    public void drawLines(Graphics G) {
        // draw the board lines
        for (int i = 0; i < (int) (constants.SCREEN_HEIGHT / constants.UNIT_SIZE); i++) {
            G.setColor(Color.gray);
            G.drawLine(i * constants.UNIT_SIZE, 0, i * constants.UNIT_SIZE, constants.SCREEN_HEIGHT);
            G.drawLine(0, i * constants.UNIT_SIZE, constants.SCREEN_WIDTH, i * constants.UNIT_SIZE);
        }
    }

    public void drawApple(Graphics G, int x, int y) {
        // draw the apple
        G.setColor(Color.red);
        G.fillOval(x, y, constants.UNIT_SIZE, constants.UNIT_SIZE);
    }

    public void drawSnake(Graphics G, int x[], int y[], int lenghtOfSnake) {
        // draw the snake
        for (int i = 0; i < lenghtOfSnake; i++) {
            G.setColor(Color.green);
            G.fillRect(x[i], y[i], constants.UNIT_SIZE, constants.UNIT_SIZE);
        }
    }
}
