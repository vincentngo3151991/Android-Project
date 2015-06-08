package thunderiven.game2048;

import android.test.AndroidTestCase;

import java.sql.Array;
import java.util.Arrays;

/**
 * Created by Vincent Ngo on 6/4/2015.
 */
public class GamePlayerTest extends AndroidTestCase{
    private GamePlayer newgame;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        newgame=new GamePlayer();
        newgame.newGame(0);
        newgame.clear();
    }

    // Test converting 2D board to 1D board
    public void test1D() throws Throwable{

        int[] newboard=newgame.getBoard();
        assertTrue(Arrays.equals(newboard, new int[16]));

        int[][] testboard=new int[4][4];
        for (int i=0;i<4;i++) {
            testboard[1][i]=i;
        }
        int[] expected=new int[16];
        for (int i=4;i<8;i++) {
            expected[i]=i-4;
        }
        newgame.setBoard(testboard);
        int[] result = newgame.getBoard();
        assertTrue(Arrays.equals(result,expected));
    }

    // Test game over
    public void testGameOver() throws Throwable {
        newgame.clear();
        int i=1;
        int[][] testboard=new int[4][4];
        for (int r=0;r<4;r++) {
            for (int c=0;c<4;c++) {
                testboard[r][c]=i;
                i++;
            }
        }
        newgame.setCount(16);
        newgame.setBoard(testboard);
        assertTrue(newgame.gameOver()); // Game over

        testboard[1][0]=1;
        newgame.setBoard(testboard);
        assertFalse(newgame.gameOver());

        testboard[1][0]=5;
        testboard[3][3]=12;
        newgame.setBoard(testboard);
        assertFalse(newgame.gameOver());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
