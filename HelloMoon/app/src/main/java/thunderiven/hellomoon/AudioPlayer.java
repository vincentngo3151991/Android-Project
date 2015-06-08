package thunderiven.hellomoon;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by Vincent Ngo on 5/28/2015.
 */
public class AudioPlayer {
    private MediaPlayer mPlayer;
    private boolean isPause;
    public void play(Context c) {
        if (isPause&&mPlayer!=null) {
            isPause=false;
            mPlayer.start();

        } else {
            stop();
            mPlayer=MediaPlayer.create(c,R.raw.one_small_step);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stop();
                }
            });
            mPlayer.start();
        }
    }

    public void stop() {
        if (mPlayer!=null) {
            mPlayer.release();
            mPlayer=null;
        }
    }
    public void pause() {
        if (mPlayer!=null&&mPlayer.isPlaying()) {
            isPause=true;
            mPlayer.pause();
        }
    }

}
