package thunderiven.game2048;

import android.util.Log;

import java.util.Arrays;
import java.util.Random;

/**
 *                              Created by Vincent Ngo on 6/4/2015.
 * This Game.java file implements Game.java file in game2048 package provided as a project 0
 * in Fall 2014 CS61B -Data Structures at UC Berkeley. I implements some of the methods
 * from the package in order to help with the game engine. It is used as a personal project
 * only and will not be available for commercial usage.
 *
 */
public class Game {
    // --------------------------------Variables -----------------------
    // This is the probability of get random tile value of 2
    private static final double TWO_TILE_PROBABILITY=0.9;
    private Random random;
    private static final int mNumRow=4; // set the number of row and column to be 4

    // Score of current game
    private int mScore;
    // Max score of all the game
    private int mMaxScore;

    // The Tiles currently on the board
    private Tile[][] mCurrentTile;
    // The Tiles that are to be merged with tiles already at the indicated squares
    private Tile[][] mMergedTile;
    // The tile that will be displayed after displayMoved call
    private Tile[][] mNextTile;
    // Number of pending moves to be made
    private int mMove;

    //===============================End of Variable Declaration===============


    // This is the constructor // input seed
    public Game(long seed) {

        //Create random number generator. The random number will generate same number if seed != 0
        if (seed==0) {
            random=new Random();
        } else {
            random=new Random(seed);
        }


    }
    //===============================End of Constructor=========================

    //------------------------------Method Declaration------------------------

    // Clear and reset the current state to an empty board;
    public void clear() {
        mCurrentTile=new Tile[mNumRow][mNumRow];
        mMergedTile=new Tile[mNumRow][mNumRow];
        mNextTile=new Tile[mNumRow][mNumRow];
        mMove=0;
    }

    // Create new Tile with Value at Row and Column
    public void addTile(int value,int row, int col) {
        if (mMove!=0) {
            throw  badArg("Must finish pending moves before addTile");
        } else if (mCurrentTile[row][col]!=null) {
            throw  badArg("Square at (%d,%d) is already occupied",row,col);
        }
        mCurrentTile[row][col]=new Tile(value);
        mCurrentTile[row][col].setPosition(row,col);

        //reset Next Tile and Merged Tile
        mNextTile=new Tile[mNumRow][mNumRow];
        mMergedTile=new Tile[mNumRow][mNumRow];

    }

    // Move a tile from (row,col) to (newRow,newCol)
    public void moveTile(int value,int row,int col,int newRow,int newCol) {
        Tile tile=mCurrentTile[row][col];
        if (tile==null) {
            throw badArg("No tile at (%d,%d)",row,col);
        }
        if (row==newRow&&col==newCol) {
            return;
        }
        if (mMergedTile[row][col]!=null) {
            throw  badArg("Tile is (%d,%d) is already merged",row,col);
        } else if (tile.getValue()!=value) {
            throw  badArg("wrong value (%d) for tile at (%d,%d)",value,row,col);
        } else if (mCurrentTile[newRow][newCol]!=null) {
            throw  badArg("Square at *%d,%d) is occupied",newRow,newCol);
        }

        mMove+=1;
        mCurrentTile[row][col]=null;
        mNextTile[newRow][newCol]=mCurrentTile[newRow][newCol]=tile;

    }

    // Move a tile whose value is Value from (Row,Col) to (newRow,newCol) merging
    // it with the tile of the same value
    public void mergeTile(int value,int newValue,int row,int col,int newRow,int newCol) {
        Tile tile=mCurrentTile[row][col];
        if (tile==null) {
            throw badArg("No tile at (%d,%d)",row,col);
        } else if (tile.getValue()!=value) {
            throw badArg("wrong value (%d) for tile at (%d, %d)",
                    value, row, col);
        } else if (mCurrentTile[newRow][newCol] == null) {
            throw badArg("no tile to merge with at (%d, %d)", row, col);
        } else if (mMergedTile[newRow][newCol] != null) {
            throw badArg("tile at (%d, %d) is already merged", newRow, newCol);
        } else if (mCurrentTile[newRow][newCol].getValue() != tile.getValue()) {
            throw badArg("merging mismatched tiles at (%d, %d)",
                    newRow, newCol);
        }
        mMove+=1;
        mCurrentTile[row][col]=null;
        mMergedTile[newRow][newCol]=tile;
        mNextTile[newRow][newCol]=new Tile(newValue);
    }

    // Complete all pending moves
    public void displayMoves() {
        if (mMove==0) {
            return;
        }
        for (int r=0;r<mNumRow;r++) {
            for (int c=0;c<mNumRow;c++) {
                if (mNextTile[r][c]==null) {
                    mNextTile[r][c]=mCurrentTile[r][c];
                }
            }
        }

        mMove=0;
        mCurrentTile=mNextTile;
        mMergedTile=new Tile[mNumRow][mNumRow];
        mNextTile=new Tile[mNumRow][mNumRow];
    }

    public void setScore(int score,int maxScore) {
        mScore=score;
        mMaxScore=maxScore;
    }

    public int[] getScore() {
        return new int[]{mScore,mMaxScore};
    }


    // This method is used to get random tile at the beginning of the move
    // The return value is the triple {value,row,column}
    // Where value is either 2 or 4
    public int[] getRandomTile() {
        int[] tile;
        int value=2*(1+(int) (random.nextDouble()/TWO_TILE_PROBABILITY ));
        tile=new int[] {value,random.nextInt(mNumRow),random.nextInt(mNumRow)};
        return tile;
    }

    static IllegalArgumentException badArg(String msg,Object... args) {
        return new IllegalArgumentException(String.format(msg,args));
    }

    // ==============================End of method==================================

    // --------------- Log/Debug ----------------
    private void checkBoardLog() {
        Log.d("Game", " First row is "+ Arrays.toString(mCurrentTile[0]));
        Log.d("Game", " Second row is "+ Arrays.toString(mCurrentTile[1]));
        Log.d("Game", " Third row is "+ Arrays.toString(mCurrentTile[2]));
        Log.d("Game", " Forth row is "+ Arrays.toString(mCurrentTile[3]));
    }
}
