package entities;

import java.util.Random;

import util.Constants;

public class Apple {
    int appleX, appleY;
    Random random;

    Constants constants = new Constants();

    public Apple() {
        appleX = 0;
        appleY = 0;
    }

    public int getAppleX() {
        return appleX;
    }

    public int getAppleY() {
        return appleY;
    }

    public void spawnApple() {
        appleX = random.nextInt((int) (constants.SCREEN_WIDTH / constants.UNIT_SIZE)) * constants.UNIT_SIZE;
        appleY = random.nextInt((int) (constants.SCREEN_HEIGHT / constants.UNIT_SIZE)) * constants.UNIT_SIZE;
    }

}
