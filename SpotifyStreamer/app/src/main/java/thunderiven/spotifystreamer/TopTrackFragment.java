package thunderiven.spotifystreamer;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by Vincent Ngo on 6/2/2015.
 */
public class TopTrackFragment extends Fragment {
    public static final String ARTIST_ID_EXTRA="Id extra";
    public static final String ARTIST_NAME_EXTRA="name extra";
    private String mArtistId;
    private String mArtistName;
    private ListView mListView;
    private TrackAdapter mTrackAdapter;
    private TracksSingleton mTracksSingleton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        // Create an instance of TracksSingleton
        mTracksSingleton= TracksSingleton.get(getActivity());

        Intent i=getActivity().getIntent();
        mArtistId=i.getStringExtra(ARTIST_ID_EXTRA);
        mArtistName=i.getStringExtra(ARTIST_NAME_EXTRA);
        Log.d("TopTrackFragment",mArtistId);
        FetchTrackData mTrackData=new FetchTrackData();
        mTrackAdapter=new TrackAdapter(new ArrayList<Track>());
        setHasOptionsMenu(true);
        mTrackData.execute(mArtistId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_toptentracks, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(mArtistName);
        View emptyView=getActivity().getLayoutInflater().inflate(R.layout.empty_list_view,null);
        mListView=(ListView)v.findViewById(R.id.top_ten_tracks_listview);
        mListView.setEmptyView(emptyView);
        mListView.setAdapter(mTrackAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(getActivity(),TrackPlayerActivity.class);
                i.putExtra(TrackPlayerFragment.TRACK_LOCATION,position);
                startActivity(i);
            }
        });
        return v;
    }

    private class FetchTrackData extends AsyncTask<String,Void,Tracks> {
        /* subclass of AsyncTask that is used to fetch the top track by artist
        * */
        @Override
        protected Tracks doInBackground(String... strings) {
            SpotifyApi api=new SpotifyApi();
            SpotifyService spotify=api.getService();
            // create querymap of the country desired
            Map<String,Object> optionMap=new HashMap<String,Object>();
            // put the query parameter to the query map, here the query parameter key is country
            // and the query value is US
            optionMap.put("country","US");

            Tracks results=spotify.getArtistTopTrack(strings[0],optionMap);
            return results;
        }

        @Override
        protected void onPostExecute(Tracks tracks) {
            updateAdapter((ArrayList<Track>)tracks.tracks);
        }
    }

    private class TrackAdapter extends ArrayAdapter<Track> {
        public TrackAdapter(ArrayList<Track> tracks) {super(getActivity(),0,tracks);}
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null) {
                convertView=getActivity().getLayoutInflater().inflate(R.layout.individual_track,null);
            }
            Track track=getItem(position);
            TrackParser parser=new TrackParser(track);

            TextView mName=(TextView)convertView.findViewById(R.id.track_name_textview);
            mName.setText(parser.getName());

            TextView mAlbumName=(TextView)convertView.findViewById(R.id.track_album_textview);
            mAlbumName.setText(parser.getAlbumName());

            ImageView mThumbnail=(ImageView)convertView.findViewById(R.id.track_imageview);
            Picasso.with(getActivity()).load(parser.getThumbnailSmall()).into(mThumbnail);
            return convertView;
        }
    }
    private void updateAdapter(ArrayList<Track> tracks) {
        if (tracks!=null) {
            mTrackAdapter.clear();
            for (Track track:tracks) {
                mTrackAdapter.add(track);
            }

            // Update the TracksSingleton to contain the new tracks
            mTracksSingleton.updateTracks(tracks);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity())!=null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
