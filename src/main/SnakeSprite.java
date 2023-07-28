package main;

import java.awt.*;
import java.awt.geom.*;

public class SnakeSprite {
    private double x, y, size;
    private Color color;

    public SnakeSprite(double a, double b, double s, Color c) {
        x = a;
        y = b;
        size = s;
        color = c;
    }

    public void drawSnake(Graphics2D G) {
        Rectangle2D.Double square = new Rectangle2D.Double(x, y, size, size);
        G.setColor(color);
        G.fill(square);
    }

    // change the values of x and y
    public void snakeMoveHorizontal(double n) {
        x += n;
    }

    public void snakeMoveVertical(double n) {
        y += n;
    }

    // set the values of x and y
    public void setX(double n) {
        x = n;
    }

    public void setY(double n) {
        y = n;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}