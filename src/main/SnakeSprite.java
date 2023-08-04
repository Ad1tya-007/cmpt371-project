package main;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList; // Imported ArrayList class

import main.util.Constants;

public class SnakeSprite {
    private ArrayList<Point2D.Double> segments; // Changed to ArrayList
    private double size;
    private Color color;
    public int bodyParts = 3;
    private int score = 0;
    private Constants constant = new Constants();

    public void score() {
        score += 10;
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private Direction currentDirection;

    public SnakeSprite(double x, double y, double s, Color c, int length, Direction initialDirection) {
        size = s;
        color = c;
        segments = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            segments.add(new Point2D.Double(x - i * size, y));
        }
        currentDirection = initialDirection;
    }

    public void addSegment() {
        if (currentDirection== Direction.RIGHT) {
            segments.add(new Point2D.Double(segments.get(bodyParts-1).x - constant.UNIT_SIZE, segments.get(bodyParts-1).y));
        }
        else if (currentDirection== Direction.LEFT) {
            segments.add(new Point2D.Double(segments.get(bodyParts-1).x + constant.UNIT_SIZE, segments.get(bodyParts-1).y));
        }
        else if (currentDirection== Direction.UP) {
            segments.add(new Point2D.Double(segments.get(bodyParts-1).x, segments.get(bodyParts-1).y - constant.UNIT_SIZE));
        }
        else if (currentDirection== Direction.DOWN) {
            segments.add(new Point2D.Double(segments.get(bodyParts-1).x, segments.get(bodyParts-1).y + constant.UNIT_SIZE));
        }
        bodyParts++;
    }

    public void drawSnake(Graphics2D G) {
        G.setColor(color);
        ArrayList<Point2D.Double> copyOfSegments = getSegments(); // Get a copy
        for (Point2D.Double segment : copyOfSegments) {
            Rectangle2D.Double square = new Rectangle2D.Double(segment.x, segment.y, size, size);
            G.fill(square);
        }
    }

    // change the values of x and y
    public void snakeMoveHorizontal(int n) {
        // Prevent moving left if currently moving right and vice versa
        if ((currentDirection == Direction.RIGHT && n < 0) || 
            (currentDirection == Direction.LEFT && n > 0)) return;
            currentDirection = n > 0 ? Direction.RIGHT : Direction.LEFT;
            moveSnake(segments.get(0).x + n * size, segments.get(0).y);
        }
    
    public void snakeMoveVertical(int n) {
        // Prevent moving up if currently moving down and vice versa
        if ((currentDirection == Direction.UP && n > 0) || 
            (currentDirection == Direction.DOWN && n < 0)) return;
            currentDirection = n > 0 ? Direction.DOWN : Direction.UP;
            moveSnake(segments.get(0).x, segments.get(0).y + n * size);
        }
    

    private void moveSnake(double x, double y) {
        segments.add(0, new Point2D.Double(x, y)); // Changed to use index
        segments.remove(segments.size() - 1); // remove tail segment to maintain length
    }

    // set the values of x and y
    public void setX(double n) {
        double diff = n - segments.get(0).x;
        for (Point2D.Double segment : segments) {
            segment.x += diff;
        }
    }

    public void setY(double n) {
        double diff = n - segments.get(0).y;
        for (Point2D.Double segment : segments) {
            segment.y += diff;
        }
    }

    public double getX() {
        return segments.get(0).x;
    }

    public double getY() {
        return segments.get(0).y;
    }

    // Synchronized method to set the segments
    public synchronized void setSegments(ArrayList<Point2D.Double> newSegments) {
        segments.clear();
        segments.addAll(newSegments);
    }

    // Synchronized method to retrieve the segments
    public synchronized ArrayList<Point2D.Double> getSegments() {
        return new ArrayList<>(segments); // Return a copy of the segments
    }
}