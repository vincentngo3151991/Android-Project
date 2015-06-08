package thunderiven.hellomoon;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Vincent Ngo on 5/28/2015.
 */
public class HelloMoonActivity extends Activity {
    private Button mVideoButton;
    private FragmentManager fm;
    public static final String TAG="Activity";
    private String mType; // set to type ("Video" or "Audio")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_moon);

        Log.d(TAG, "HelloMoonActivity created");
        fm = getFragmentManager();
        Fragment  fragment=fm.findFragmentById(R.id.fragment_container);

        if (fragment==null) {
            fragment=new HelloMoonFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit(); }
        mType="Video";

        mVideoButton=(Button)findViewById(R.id.video_button);
        mVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mType=="Video") {
                    changeFragment(new VideoFragment());
                    mType="Audio";
                    mVideoButton.setText(mType);
                } else if (mType=="Audio") {
                    changeFragment(new HelloMoonFragment());
                    mType="Video";
                    mVideoButton.setText(mType);
                }

            }
        });

    }


    private void changeFragment(Fragment new_fragment) {
        fm=getFragmentManager();
        fm.beginTransaction().replace(R.id.fragment_container,new_fragment).commit();
    }
}
