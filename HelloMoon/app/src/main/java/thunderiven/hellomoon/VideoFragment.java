package thunderiven.hellomoon;

import android.app.Fragment;
import android.media.session.MediaController;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

/**
 * Created by Vincent Ngo on 5/28/2015.
 */
public class VideoFragment extends Fragment {
    private Button mStartButton;
    private Button mStopButton;
    private Button mPauseButton;
    private VideoView mVideoView;
    private android.widget.MediaController mController;
    private boolean isStopped=false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.video_layout, container, false);

        mVideoView=(VideoView)v.findViewById(R.id.video_container);

        String uriPath="android.resource://" +getActivity().getPackageName()+"/"+R.raw.apollo_17_stroll;
        final Uri resourceUri=Uri.parse(uriPath);


        mController=new android.widget.MediaController(getActivity());
        mController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mController);
        mVideoView.requestFocus();
        mVideoView.setVideoURI(resourceUri);



        mStartButton=(Button)v.findViewById(R.id.hellomoon_playButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStopped) {
                    mVideoView.setVideoURI(resourceUri);
                    mVideoView.start();
                    isStopped=false;
                } else {
                    mVideoView.start();
                }
            }
        });

        mStopButton=(Button)v.findViewById(R.id.hellomoon_stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mVideoView.stopPlayback();
                isStopped=true;
            }
        });


        mPauseButton=(Button) v.findViewById(R.id.hellomoon_pauseButton);
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVideoView.isPlaying()&&mVideoView.canPause()) {
                    mVideoView.pause();

                }
            }
        });


        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }
}
