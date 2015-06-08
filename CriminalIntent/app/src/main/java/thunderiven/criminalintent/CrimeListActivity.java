package thunderiven.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Vincent Ngo on 5/29/2015.
 */
public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
