package thunderiven.spotifystreamer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Vincent Ngo on 7/1/2015.
 */
public class TrackPlayerFragment extends Fragment {
    public static final String TRACK_LOCATION="track_location";
    private static final String TAG="TrackPlayerFragment";
    private TracksSingleton mTracksSingleton;
    private ArrayList<Track> mTracks;
    private int mTrackLocaiton;
    private MediaPlayer mMediaPlayer;

    private TextView mArtistNameTextView, mAlbumNameTextView,mTrackNameTextView;
    private TextView mTimeDuration,mTimePosition;
    private ImageButton mBackButton, mPlayButton, mNextButton;
    private ImageView mAlbumArtWork;
    private SeekBar mTrackDuration;
    private Handler mHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTracksSingleton=TracksSingleton.get(getActivity());
        mTracks=mTracksSingleton.getTracks();

        // Get the intent
        Intent i=getActivity().getIntent();
        mTrackLocaiton=i.getIntExtra(TRACK_LOCATION,0);
        mMediaPlayer=new MediaPlayer();
        mHandler=new Handler();
        setRetainInstance(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_track_player, container, false);

        mArtistNameTextView=(TextView)v.findViewById(R.id.trackPlayer_artist_name);
        mAlbumNameTextView=(TextView)v.findViewById(R.id.trackPlayer_album_name);
        mTrackNameTextView=(TextView)v.findViewById(R.id.trackPlayer_track_name);
        mTimeDuration=(TextView)v.findViewById(R.id.trackPlayer_durationTextView);
        mTimePosition=(TextView)v.findViewById(R.id.trackPlayer_positionTextView);


        mAlbumArtWork=(ImageView)v.findViewById(R.id.trackPlayer_album_artwork);


        mBackButton=(ImageButton)v.findViewById(R.id.trackPlayer_back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseMedia();
                // If the track is 0 then it will go to the last song
                // Otherwise it will go to previous song
                if (mTrackLocaiton==0) {
                    mTrackLocaiton=mTracks.size()-1;
                } else {
                    mTrackLocaiton=mTrackLocaiton-1;
                }
                updateView();
            }
        });

        mPlayButton=(ImageButton)v.findViewById(R.id.trackPlayer_play_button);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mPlayButton.setImageResource(R.drawable.play);
                } else {
                    mMediaPlayer.start();
                    mPlayButton.setImageResource(R.drawable.pause);
                }
                mUpdateTimeTask.run();

            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayButton.setImageResource(R.drawable.play);
            }
        });

        mNextButton=(ImageButton)v.findViewById(R.id.trackPlayer_next_button);
        mNextButton
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // If the track is the last song then it will go to the first song
                        // Otherwise it will go to next song song
                        releaseMedia();
                        if (mTrackLocaiton == mTracks.size() - 1) {
                            mTrackLocaiton = 0;
                        } else {
                            mTrackLocaiton = mTrackLocaiton + 1;
                        }
                        updateView();
                    }
                });
        mTrackDuration=(SeekBar)v.findViewById(R.id.trackPlayer_duration);
        mTrackDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int currentPosition=seekBar.getProgress()*mMediaPlayer.getDuration()/100;
                mMediaPlayer.seekTo(currentPosition);
                mUpdateTimeTask.run();
            }
        });

        updateView();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMediaPlayer==null) {
            updateView();
        }
    }

    public void updateView() {
        Track track=mTracks.get(mTrackLocaiton);
        TrackParser parser=new TrackParser(track);
        if (mMediaPlayer==null) {
            mMediaPlayer=new MediaPlayer();
        }
        mArtistNameTextView.setText(parser.getArtistName());
        mAlbumNameTextView.setText(parser.getAlbumName());
        mTrackNameTextView.setText(parser.getName());

        //Uri preview_url=Uri.parse(parser.getPreviewUrl());
        String preview_url=parser.getPreviewUrl();

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(preview_url);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Error streaming music",e );
        }

        mTimeDuration.setText(milliSecondsToTimer(mMediaPlayer.getDuration()));
        mTimePosition.setText(milliSecondsToTimer(mMediaPlayer.getCurrentPosition()));

        if (mMediaPlayer.isPlaying()) {
            mPlayButton.setImageResource(R.drawable.pause);
        } else {
            mPlayButton.setImageResource(R.drawable.play);
        }

        // Set the seek bar progrees
        updateSeekBar();


        // Update the thumbnail
        Picasso.with(getActivity()).load(parser.getThumbnailLarge()).into(mAlbumArtWork);
        Log.d(TAG, "updateView called");
    }

    public void releaseMedia() {
        if (mMediaPlayer!=null) {
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMedia();
    }

    // Set the progress bar
    public void updateSeekBar (){
        long totalDuration=mMediaPlayer.getDuration();
        long currentPosition=mMediaPlayer.getCurrentPosition();
        int progress=getProgressPercentage(currentPosition,totalDuration);
        mTrackDuration.setProgress(progress);
    }


    // Convert milliseconds to timer
    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public int getProgressPercentage(long position,long duration) {
        Double percentage=(double) 0;
        percentage=(double)position/duration*100;

        return percentage.intValue();
    }
    // Create runnable to update the progress bar timer
    private Runnable mUpdateTimeTask=new Runnable() {
        @Override
        public void run() {
            long totalDuration=mMediaPlayer.getDuration();
            long currentPosition=mMediaPlayer.getCurrentPosition();

            mTimeDuration.setText(milliSecondsToTimer(totalDuration));
            mTimePosition.setText(milliSecondsToTimer(currentPosition));

            int progress=getProgressPercentage(currentPosition,totalDuration);
            mTrackDuration.setProgress(progress);

            mHandler.postDelayed(this, 100);

        }
    };
}
