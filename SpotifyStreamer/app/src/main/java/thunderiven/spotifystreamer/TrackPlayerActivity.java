package thunderiven.spotifystreamer;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Vincent Ngo on 7/1/2015.
 */
public class TrackPlayerActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_player);

        FragmentManager fm=getSupportFragmentManager();
        Fragment fragment=fm.findFragmentById(R.id.track_player_fragment_container);

        if (fragment==null) {
            fragment=new TrackPlayerFragment();
            fm.beginTransaction()
                    .add(R.id.track_player_fragment_container,fragment)
                    .commit();
        }
    }
}
