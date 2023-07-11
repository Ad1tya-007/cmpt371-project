
public class Apple{
    private int position_x, position_y;
    
    
    public apple(int x , int y) {
        this.position_x = x;
        this.position_y = y;
    }

    int getApplePostion_X(){
        return this.position_x;
    }
    
    int getApplePostion_Y(){
        return this.position_y;
    }
    
	void setApplePostion( int x , int y){
        this.position_x = x;
        this.position_y = y;
    }

}

// public void checkApple(){
//     if((x[0]) == appleX) && if((y[0]) == appleY){
//         bodyParts++;
//         applesEaten++;
//         newApple();
//     }
// }