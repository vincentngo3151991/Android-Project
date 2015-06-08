package thunderiven.game2048;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Vincent Ngo on 6/5/2015.
 */
public class GameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        FragmentManager fm=getFragmentManager();
        Fragment fragment=fm.findFragmentById(R.id.game_fragment_container);
        if (fragment == null) {
            fragment=new GameFragment();
            fm.beginTransaction().add(R.id.game_fragment_container,fragment)
                    .commit();
        }

    }
}
