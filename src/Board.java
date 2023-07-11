import java.util.Random;

public class GameBoard {
    private int size;
    private Snake snake1;
    private Snake snake2;
    private int[][] board;
    private Apple apple;

    public GameBoard(int size, Snake snake1, Snake snake2) {
        this.size = size;
        this.snake1 = snake1;
        this.snake2 = snake2;
        this.board = new int[size][size];
        this.apple = null;

        spawnApple();
    }

    private void spawnApple() {
        Random random = new Random();
        int x, y;

        do {
            x = random.nextInt(size);
            y = random.nextInt(size);
        } while (board[x][y] != 0); // Retry if the cell is not empty

        apple = new Apple(x, y);
        board[x][y] = 1; // Mark the cell as occupied by the apple
    }

    public void updateBoard() {
        // Reset the board
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = 0;
            }
        }

        // Add the snakes and apple to the board
        // Let's assume snake1 is marked as 2, snake2 as 3, and apple as 1 on the board
        for (Position position : snake1.getBodyPosition()) {
            board[position.getX()][position.getY()] = 2;
        }

        for (Position position : snake2.getBodyPosition()) {
            board[position.getX()][position.getY()] = 3;
        }

        board[apple.getX()][apple.getY()] = 1;
    }
}
