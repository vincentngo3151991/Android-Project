package thunderiven.spotifystreamer;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SearchView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by Vincent Ngo on 6/2/2015.
 */
public class SearchFragment extends Fragment {
    private SearchView mSearchView;
    private ListView mListView;
    private ArrayList<Artist> mArtistList=new ArrayList<Artist>();
    private ArtistAdapter adapter;
    private static final String TAG="SearchFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        adapter=new ArtistAdapter(mArtistList);
        adapter.setNotifyOnChange(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_musicsearch,container,false);
        mSearchView=(SearchView) v.findViewById(R.id.music_search_searchview);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                FetchArtistData fetching = new FetchArtistData();
                fetching.execute(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        mListView=(ListView)v.findViewById(R.id.music_search_listview);
        View mEmptyView=getActivity().getLayoutInflater().inflate(R.layout.empty_list_view,null);
        ((ViewGroup)mListView.getParent()).addView(mEmptyView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist=adapter.getItem(i);
                ArtistParser parser=new ArtistParser(artist);
                String mId=parser.getId();
                String mName=parser.getName();

                Intent intent=new Intent(getActivity(),TopTrackActivity.class);
                intent.putExtra(TopTrackFragment.ARTIST_ID_EXTRA,mId);
                intent.putExtra(TopTrackFragment.ARTIST_NAME_EXTRA,mName);
                startActivity(intent);
            }
        });
        mListView.setAdapter(adapter);
        mListView.setEmptyView(mEmptyView);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private class ArtistAdapter extends ArrayAdapter<Artist> {
        public ArtistAdapter(ArrayList<Artist> artists) {super(getActivity(),0,artists);}
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null) {
                convertView=getActivity().getLayoutInflater().inflate(R.layout.search_result,null);
            }
            Artist artist=getItem(position);
            ArtistParser parsed=new ArtistParser(artist);

            TextView mArtistName=(TextView)convertView.findViewById(R.id.search_result_textview);
            mArtistName.setText(parsed.getName());

            ImageView mThumbnail=(ImageView)convertView.findViewById(R.id.search_result_image_view);
            Picasso.with(getActivity()).load(parsed.getThumbnailId()).into(mThumbnail);
            return convertView;
        }

    }
    private void updateAdapter(ArrayList<Artist> artists) {
        if (artists!=null) {
            if (artists.isEmpty()) {
                Toast.makeText(getActivity(),R.string.no_artist_toast,Toast.LENGTH_SHORT).show();
            } else {
                adapter.clear();
                for (Artist artist:artists) {
                adapter.add(artist);
                }
            }
        }


    }

    public class FetchArtistData extends AsyncTask<String,Void, List<Artist>> {
        private static final String TAG="FetchArtistData";
        @Override
        protected List<Artist> doInBackground(String... params) {
            SpotifyApi api=new SpotifyApi();
            SpotifyService spotify=api.getService();
            ArtistsPager results=spotify.searchArtists(params[0]);
            List<Artist> list=results.artists.items;
            Log.d(TAG, list.toString());
            return list;
        }

        @Override
        protected void onPostExecute(List<Artist> artists) {
            mArtistList=(ArrayList<Artist>)artists;
            Log.d(TAG,Integer.toString(mArtistList.size()));
            updateAdapter(mArtistList);
        }
    }
}


