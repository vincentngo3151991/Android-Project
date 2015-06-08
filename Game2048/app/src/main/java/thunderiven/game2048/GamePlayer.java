package thunderiven.game2048;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by Vincent Ngo on 6/4/2015.
 */
public class GamePlayer {
    //----------------------Variables---------------------
    // Number of Rows and Columns
    private static final int SIZE=4;
    // Number of squares on the board.
    private static final int SQUARES=SIZE*SIZE;
    // Symbolic names for the four sizes of a board.
    public static enum Side {NORTH,EAST,SOUTH,WEST};
    // Represents board with tile value at board[r][c]
    private final int[][] board=new int[SIZE][SIZE];
    //Number of tiles on the board
    private int Count;
    //Score of the current game
    private int mScore,mMaxScore;
    // Open game
    private Game game;
    // This return a location of tile in 2D format to use in gridview adapter
    private int[] m1DBoard=new int[SQUARES];
    // Highest tile value
    private int mHighestValue=0;

    boolean mIsStarted=false;
    // Define ScoreDictionary
    private ScoreDictionary mDictionary=new ScoreDictionary();

    //==========================End of Variables=================

    // -----------------------Method---------------------------


    // Reset the score and clear the board
    public void clear() {
        Count=0;
        mScore=0;
        game.clear();
        game.setScore(mScore, mMaxScore);
        for (int r=0;r<SIZE;r++) {
            for (int c=0;c<SIZE;c++) {
                board[r][c]=0;
            }
        }
        mIsStarted=false;
    }
    public void newGame(long seed) {
        game=new Game(seed);
        clear();

        // Set two random tiles on the board
        setRandomPiece();
        setRandomPiece();
        mIsStarted=true;
    }

    //return if the game is started or not
    public boolean isStarted() {
        return mIsStarted;
    }

    public boolean nextMove(Side side) {

        // check if the game is over or we can tilt the board in the direction
        if (!gameOver() && tiltBoard(side)) {
            setRandomPiece();
            return true;
        }
        return false;
    }


    /** Return true iff the current game is over (no more moves
     *  possible). */
    boolean gameOver() {
        // game over when Highest Tile value reaches 2048
        if (mHighestValue == 2048) {
            return true;
            // test if there is no move
        } else if (Count==SQUARES) {
            return !checkBoard();

        } else {
            return false;
        }
    }
    // Check board for available move
    boolean checkBoard() {
        boolean move=false;
        for (int r=0;r<SIZE;r++) {
            for (int c=0;c<SIZE;c++) {
                if (r<SIZE-1 &&c<SIZE-1) {
                    if(board[r][c]==board[r+1][c] ||
                        board[r][c]==board[r][c+1]) {
                        move=true;
                        break;
                    }

                } else if (r==SIZE-1 && c!=SIZE -1) {
                    if(board[r][c]==board[r][c+1]) {
                        move=true;
                        break;
                    }
                } else if (r!=SIZE-1 && c==SIZE-1) {
                    if (board[r][c]==board[r+1][c]) {
                        move=true;
                        break;
                    }
                }
            }
        }
        return move;
    }

    /** Add a tile to a random, empty position, choosing a value (2 or
     *  4) at random.  Has no effect if the board is currently full. */
    void setRandomPiece() {
        if (Count == SQUARES) {
            return;
        }
        int[] newTile=game.getRandomTile();
        if (board[newTile[1]][newTile[2]]!=0) {
            setRandomPiece();
        } else {
            Count++;
            game.addTile(newTile[0], newTile[1], newTile[2]);
            board[newTile[1]][newTile[2]] = newTile[0];
            Log.d("Game Player", String.format("Tile value %d added to (%d,%d)",
                    newTile[0],newTile[1],newTile[2]));
        }
    }

    boolean tiltBoard(Side side) {
        /* As a suggestion (see the project text), you might try copying
         * the board to a local array, turning it so that edge SIDE faces
         * north.  That way, you can re-use the same logic for all
         * directions.  (As usual, you don't have to). */
        int[][] tempBoard = new int[SIZE][SIZE];
        // This is to check if the player can move anything in the direction
        boolean canMove=false;
        // new tile value
        int newValue;
        Side transposeSide=transpose(side);

        for (int r = 0; r < SIZE; r += 1) {
            for (int c = 0; c < SIZE; c += 1) {
                tempBoard[r][c] =
                        board[tiltRow(side, r, c)][tiltCol(side, r, c)];
            }
        }

        /* Since we are only deal with the Up direction, I divided the board into 4 1D array
         * Do search for each of the tile in Y direction.
         * If the current tile is none (value of 0), then it will look for the next tile
         * (if available). Else if the current tile contains value V, then it will look for
         * the next tile with the same value.
         * Continues looping until all the column has been search. If there is no move available,
         * it return false
         */

        for (int c=0;c<SIZE;c++) {
            for (int r=0;r<SIZE-1;r++) { // Check the first 3 rows
                int nextRow=r+1;
                while (nextRow<SIZE-1 && tempBoard[nextRow][c]==0) {
                    nextRow++; // Find the next tile with value !=0;
                }
                if (tempBoard[r][c]==0&&tempBoard[nextRow][c]!=0) {//Check if we can move the column
                    canMove=true;
                    game.moveTile(tempBoard[nextRow][c],
                            tiltRow(side,nextRow,c), tiltCol(side,nextRow,c),
                            tiltRow(side,r,c),tiltCol(side,r,c));

                        // update board
                    tempBoard[r][c]=tempBoard[nextRow][c];
                    tempBoard[nextRow][c]=0;
                }
                int nextMergedRow=r+1;
                while (nextMergedRow<SIZE-1 && tempBoard[nextMergedRow][c]==0) {
                    nextMergedRow++;
                }
                if (tempBoard[r][c]!=0&&tempBoard[r][c]==tempBoard[nextMergedRow][c]) {
                        // Check if we can merge the column
                    Count--; // the total tile reduce by 1
                    canMove=true;
                    game.mergeTile(tempBoard[r][c],tempBoard[r][c]*2,
                            tiltRow(side,nextMergedRow,c), tiltCol(side,nextMergedRow,c),
                            tiltRow(side,r,c),tiltCol(side,r,c));
                        // Update board
                    tempBoard[r][c]=tempBoard[nextMergedRow][c]*2;
                    tempBoard[nextMergedRow][c]=0;
                        // Update highest value
                    if (mHighestValue<tempBoard[r][c]) {
                        mHighestValue=tempBoard[r][c];
                    }

                        // Update score and max score
                    mScore+=mDictionary.getScore(tempBoard[r][c]);
                    if (mScore> mMaxScore) {
                        mMaxScore=mScore;
                    }
                }

            }
        }
        // Return false if there is no move available
        if (canMove==false) {
            return false;
        }

        for (int r = 0; r < SIZE; r += 1) {
            for (int c = 0; c < SIZE; c += 1) {
                board[tiltRow(side, r, c)][tiltCol(side, r, c)]
                        = tempBoard[r][c];
            }
        }
        game.setScore(mScore,mMaxScore);
        game.displayMoves();
        return true;
    }

    // This is used to transposed the direction back after tilt
    private Side transpose(Side side) {
        switch (side) {
            case EAST:
                return Side.WEST;
            case WEST:
                return Side.EAST;
            default:
                return side;
        }
    }

    /** Return the column number on a playing board that corresponds to row
     *  R and column C of a board turned so that row 0 is in direction SIDE
     *  (as specified by the definitions of NORTH, EAST, etc.). So, if SIDE
     *  is NORTH, then tiltCol simply returns C (since in that case, the
     *  board is not turned).  If SIDE is WEST, then row 0 of the tilted
     *  board corresponds to column 0 of the untilted board, and tiltCol
     *  returns R. */
    int tiltCol(Side side, int r, int c) {
        switch (side) {
            case NORTH:
                return c;
            case EAST:
                return SIZE - 1 - r;
            case SOUTH:
                return SIZE - 1 - c;
            case WEST:
                return r;
            default:
                throw new IllegalArgumentException("Unknown direction");
        }
    }
    /** Return the row number on a playing board that corresponds to row R
     *  and column C of a board turned so that row 0 is in direction SIDE (as
     *  specified by the definitions of NORTH, EAST, etc.).  So, if SIDE
     *  is NORTH, then tiltRow simply returns R (since in that case, the
     *  board is not turned).  If SIDE is WEST, then column 0 of the tilted
     *  board corresponds to row SIZE - 1 of the untilted board, and
     *  tiltRow returns SIZE - 1 - C. */
    int tiltRow(Side side, int r, int c) {
        switch (side) {
            case NORTH:
                return r;
            case EAST:
                return c;
            case SOUTH:
                return SIZE - 1 - r;
            case WEST:
                return SIZE - 1 - c;
            default:
                throw new IllegalArgumentException("Unknown direction");
        }
    }

    //========================End of Methods=====================
    //--------------------- Getter Method------------------------

    // Return tile location as a 1D array
    public int[] getBoard() {
        int i=0;
        for (int r=0;r<SIZE;r++) {
            for (int c = 0; c < SIZE; c++) {
                m1DBoard[i] = board[r][c];
                i++;
            }
        }
        return m1DBoard;
    }

    public int getScore() {
        return mScore;
    }

    public int getMaxScore() {
        return mMaxScore;
    }

    public void setMaxScore(int maxScore) {
        mMaxScore = maxScore;
    }

    //=====================End of Getter Method==============
    // ------------------For testing purpose only-------------------
    protected void setBoard(int[][] mboard) {

        for (int r=0;r<SIZE;r++) {
            for (int c=0;c<SIZE;c++) {
                board[r][c]=mboard[r][c];
            }
        }
    }
    protected  void setCount(int i) {
        Count=i;
    }

    //----------------- For Debug only-----------------
    private void checkBoardLog() {
        Log.d("GamePlayer", " First row is "+ Arrays.toString(board[0]));
        Log.d("GamePlayer", " Second row is "+ Arrays.toString(board[1]));
        Log.d("GamePlayer", " Third row is "+ Arrays.toString(board[2]));
        Log.d("GamePlayer", " Forth row is "+ Arrays.toString(board[3]));
    }

}