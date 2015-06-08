package thunderiven.game2048;

/**
 * Created by Vincent Ngo on 6/4/2015.
 */
public class Tile {
    // set the value of the tile
    private int mValue;
    // X and Y coordinates of the tile
    private double mX,mY;

    public Tile (int value) {
        mValue=value;
    }
    public int getValue() {
        return mValue;
    }
    public void setPosition(int row, int col) {
        mX=col;
        mY=row;
    }
}
