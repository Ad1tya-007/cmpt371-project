

public class Snake {
    private int length;
    private int[] position;

    // initializing the snake
    public Snake(int start_x_position , int start_y_position){
        // this.position.append({start_x_position , start_y_position})
        this.length = 1; // 1 tile
    }

    public int getLength(){
        return this.length;
    }

    public void setLength(int new_length){
         this.length = new_length;
    }

    public void eatApple() {
        this.length++;
        // then increase length at end of snake...
    }

    public int[] getSnakePosition {
        return position;
    }

}