package thunderiven.game2048;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import thunderiven.game2048.GamePlayer.Side;

/**
 * Created by Vincent Ngo on 6/5/2015.
 */
public class GameFragment extends Fragment {
    public static final String SEED_EXTRA="extra_seed";
    private static final String GAME_FRAGMENT="GameFragment";
    private int mScore;
    private int mMaxScore;
    private long mSeed;
    private Button mNewGameButton;
    private GamePlayer mGamePlayer;
    private GridView mGridView;
    private TextView mScoreTextView;
    private TextView mHighScoreTextView;
    private int[] mCurrentBoard;
    private ImageAdapter mAdapter;

    // List of reference to images
    private int[] mThumbIds=new int[16];


    // Check if the game is started
    private boolean mIsStarted;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mSeed=getActivity().getIntent().getLongExtra(SEED_EXTRA,0);
        mGamePlayer=new GamePlayer();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_game,container,false);

        // Load score
        try {
            loadScore();
            mGamePlayer.setMaxScore(mMaxScore);
        } catch (Exception e) {

        }

        // Set swipe listener to the view
        final GestureDetector detector=new GestureDetector(getActivity(),new OnSwipeListener());
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);

            }
        });
        mCurrentBoard=new int[16];
        mGridView=(GridView)v.findViewById(R.id.game_gridview);
        // Set on touch listener for the gridview
        mGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Link the grid view touch event to gesture detector event
                detector.onTouchEvent(event);
                return false;
            }
        });

        mAdapter=new ImageAdapter(getActivity());
        mGridView.setAdapter(mAdapter);
        updateAdapter();
        mIsStarted=false;

        mScoreTextView=(TextView)v.findViewById(R.id.score_fragment_textview);
        mHighScoreTextView=(TextView)v.findViewById(R.id.high_score_fragment_textview);
        updateScore(mScore,mMaxScore);


        // start Game
        mNewGameButton=(Button)v.findViewById(R.id.new_game_fragment_button);
        mNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsStarted) {
                    mGamePlayer.clear();
                    mSeed = 0;
                    updateScore(0, mMaxScore);
                    mCurrentBoard = new int[16];
                    Log.d(GAME_FRAGMENT, "Board clears, Score update");
                }
                mGamePlayer.newGame(mSeed);
                mCurrentBoard = mGamePlayer.getBoard();
                updateAdapter();
                mIsStarted = true;
                Log.d(GAME_FRAGMENT, "New game created");
            }
        });
        return v;
    }

    // Update Adapter
    private void updateAdapter() {
        updateThumbId();
        Log.d(GAME_FRAGMENT,"Adapter updated");
        mAdapter.notifyDataSetChanged();
    }

    // update thumbnail id
    private void updateThumbId() {
        for (int i=0;i<mThumbIds.length;i++) {
            mThumbIds[i]=getThumbId(mCurrentBoard[i]);
        }
    }

    // Get the drawable Id of the corresponding tile
    private int getThumbId(int i) {
        switch (i) {
            case 2:
                return R.drawable.tile_2;
            case 4:
                return R.drawable.tile_4;
            case 8:
                return R.drawable.tile_8;
            case 16:
                return R.drawable.tile_16;
            case 32:
                return R.drawable.tile_32;
            case 64:
                return R.drawable.tile_64;
            case 128:
                return R.drawable.tile_128;
            case 256:
                return R.drawable.tile_256;
            case 512:
                return R.drawable.tile_512;
            case 1024:
                return R.drawable.tile_1024;
            case 2048:
                return R.drawable.tile_2048;
            default:
                return R.drawable.tile_0;

        }
    }

    // This method is used to update the score
    public void updateScore (int score,int maxScore) {
        mScore=score;
        mMaxScore=maxScore;
        mScoreTextView.setText(Integer.toString(mScore));
        mHighScoreTextView.setText(Integer.toString(mMaxScore));
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public int getCount() {
            return mThumbIds.length;
        }

        public ImageAdapter(Context c) {
            mContext=c;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(250,250));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }
    }
    // This class call the next move on the game player
    private void nextMove(Side side) {
        if (!mIsStarted) {
            return;
        } // Only return true if the game has started, otherwise, it has no effect

        mGamePlayer.nextMove(side);
        mCurrentBoard=mGamePlayer.getBoard();
        updateScore(mGamePlayer.getScore(),mGamePlayer.getMaxScore());
        updateAdapter();

        // Check to see if game is over
        if (mGamePlayer.gameOver()) {
            Toast.makeText(getActivity(),R.string.game_over_toast,Toast.LENGTH_SHORT).show();
            mIsStarted=false;
        }
        Log.d(GAME_FRAGMENT,"Move registered");
    }

    // Override onTouchEvent of the fragment, capture swipe as necessary


    //------------------------OnSwipeListener declaration--------------------------------
    Side mSide;

    private class OnSwipeListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(GAME_FRAGMENT, "onFling: " + e1.toString()+e2.toString());

            float x1=e1.getX();
            float y1 = e1.getY();
            float x2=e2.getX();
            float y2=e2.getY();
            mSide=getSide(getAngle(x1,y1,x2,y2));
            nextMove(mSide);
            return false;
        }

    }
    //Get angle of the swipe
    private double getAngle(float x1,float y1,float x2,float y2) {
        double angle=Math.toDegrees(Math.atan2(y1-y2,x2-x1));
        if (angle<0) {
            angle+=360;
        }
        Log.d(GAME_FRAGMENT,Double.toString(angle));
        return angle;
    }
    // Get the Side of the swipe by calculating the coordinate of e1 and e2
    protected static Side getSide(double angle) {
        if (angle>=45&&angle<135) {
            Log.d(GAME_FRAGMENT,"Up swipe detected");
            return Side.NORTH; // Upward swipe
        } else if ((angle>=0&&angle<45)||(angle>=315&&angle<360)) {
            Log.d(GAME_FRAGMENT,"Right swipe detected");
            return Side.EAST; // Right swipe
        } else if (angle>=225&&angle<315) {
            Log.d(GAME_FRAGMENT,"Down swipe detected");
            return Side.SOUTH; // Downward swipe
        } else {
            Log.d(GAME_FRAGMENT,"Left swipe detected");
            return Side.WEST; // Left swipe
        }
    }
    //======================================================================
    private static final String JSON_SCORE="score";
    // ------------------- Save and load highest score-------------
    private void saveScore(int score) throws JSONException,IOException {
        JSONObject json=new JSONObject();
        json.put(JSON_SCORE,mMaxScore);
        Writer writer=null;
        try {
            OutputStream out=getActivity().openFileOutput("Score",Context.MODE_PRIVATE);
            writer=new OutputStreamWriter(out);
            writer.write(json.toString());
        } finally {
            if (writer!=null) {
                writer.close();
            }
        }
    }

    private void loadScore() throws JSONException, IOException{
        JSONObject json=new JSONObject();
        BufferedReader reader=null;
        try {
            InputStream in=getActivity().openFileInput("Score");
            reader= new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString=new StringBuilder();
            String line=null;
            while (( line =reader.readLine())!=null) {
                jsonString.append(line);
            }
            JSONObject jsonObject=(JSONObject) new JSONTokener(jsonString.toString()).nextValue();
            mMaxScore=jsonObject.getInt(JSON_SCORE);
        } catch  (FileNotFoundException e) {
            // Ignore at fresh start
        } finally {
            if (reader!=null) {
                reader.close();
            }
        }
    }
    // Save Score onPause()

    @Override
    public void onPause() {
        super.onPause();
        try {
            saveScore(mMaxScore);
        } catch (Exception e) {
            Log.d(GAME_FRAGMENT,"Unable to save file");
        }
    }


    //========================================================================

}
