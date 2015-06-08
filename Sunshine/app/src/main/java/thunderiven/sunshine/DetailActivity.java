package thunderiven.sunshine;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.ShareActionProvider;


public class DetailActivity extends AppCompatActivity {
    public ShareActionProvider mShareActionProvider;
    private static final String FORECAST_SHARE_HASHTAG=" #SunshineApp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
    }

    @TargetApi(14)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        MenuItem shareItem=menu.findItem(R.id.my_menu_share);
        mShareActionProvider=(ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        if (mShareActionProvider!=null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d("Activity", "Share Action Provider is null?");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i=new Intent(this,SettingsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    protected Intent createShareForecastIntent() {
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        String mForecastStr=getIntent().getStringExtra(Intent.EXTRA_TEXT);
        String mSharedString=mForecastStr+FORECAST_SHARE_HASHTAG;
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,mSharedString);
        Log.d("Activity",mSharedString);
        return shareIntent;
    }

}
