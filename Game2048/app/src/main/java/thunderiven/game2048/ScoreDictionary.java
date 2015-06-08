package thunderiven.game2048;

import java.util.HashMap;

/**
 * Created by Vincent Ngo on 6/7/2015.
 */
public final class ScoreDictionary {
    private HashMap<Integer,Integer> mScoreDictionary;
    protected ScoreDictionary() {
        mScoreDictionary=new HashMap<Integer,Integer>();
        mScoreDictionary.put(4,4);
        mScoreDictionary.put(8, 16);
        mScoreDictionary.put(16,48);
        mScoreDictionary.put(32,128);
        mScoreDictionary.put(64,320);
        mScoreDictionary.put(128,768);
        mScoreDictionary.put(256,1792);
        mScoreDictionary.put(512,4096);
        mScoreDictionary.put(1024,9216);
        mScoreDictionary.put(2048,20480);
    }

    public int getScore(int value) {
        return mScoreDictionary.get(value);
    }

}
