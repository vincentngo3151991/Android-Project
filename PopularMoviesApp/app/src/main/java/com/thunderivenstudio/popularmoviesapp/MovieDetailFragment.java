package com.thunderivenstudio.popularmoviesapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Vincent Ngo on 6/26/2015.
 */
public class MovieDetailFragment extends Fragment {
    // Extra Movie Id
    public static final String MOVIE_ID_EXTRA="extra movie id";
    private Movies mMovie;
    public static final String TAG="MovieDetailFragment";
    // Favorited Movies List
    private MovieListSingleton mMovieListSingleton;

    // Check to see if the favorite movie button is clicked
    private boolean favoriteClicked=false;

    private Activity mActivity;

    private FavoriteCallbacks mFavoriteCallbacks;

    // Implement callback to remove the Favorite movie and update the adapter
    public interface FavoriteCallbacks {
        void onFavoriteRemoved();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFavoriteCallbacks =(FavoriteCallbacks)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFavoriteCallbacks=null;
    }

    // Layout
    private ImageView mThumbnail;
    private ToggleButton mFavoritedButton;
    private TextView mTitleTextView;
    private TextView mRatingTextView;
    private TextView mSynopsisTextView;
    private TextView mReleaseDateTextView;
    private ArrayList<Trailer> trailerList=new ArrayList<Trailer>();
    private String prefString;

    // Create new instance of the MovieDetailFragment and put argumentation to the fragment
    public static MovieDetailFragment newInstance(String id) {
        Bundle args=new Bundle();
        args.putSerializable(MOVIE_ID_EXTRA, id);
        MovieDetailFragment fragment=new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String mMovieId=(String)getArguments().getSerializable(MOVIE_ID_EXTRA);
        mMovieListSingleton=MovieListSingleton.get(getActivity());
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(getActivity());
        mActivity=getActivity();
        prefString=pref.getString("Sort", "popularity.desc");
        if (!prefString.equals("favorite")) {
             mMovie=mMovieListSingleton.getMovie(mMovieId);
        } else {
            mMovie=mMovieListSingleton.getFavoritedMovie(mMovieId);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTrailer();
    }


    private void updateTrailer() {
        if (mMovie.getTrailer().isEmpty()) {
            FetchTrailerTask task=new FetchTrailerTask();
            task.execute(mMovie.getMovieId());
        } else {
            trailerList=mMovie.getTrailer();
            updateTrailerListView();
        }

    }

    // Save favorite movies onPause only if the favorite Button is clicked
    @Override
    public void onPause() {
        super.onPause();
        if (favoriteClicked) {
            MovieListSingleton.get(getActivity()).saveMovies();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.movie_detail_fragment,container,false);


        mTitleTextView=(TextView)v.findViewById(R.id.movie_detail_title_textView);
        mTitleTextView.setText(mMovie.getTitle());

        // Set movies Rating
        mRatingTextView=(TextView)v.findViewById(R.id.movie_detail_rating_textView);
        mRatingTextView.setText(mMovie.getRating()+"/10");

        // Set Thumbnail of the movie
        mThumbnail=(ImageView)v.findViewById(R.id.movie_detail_thumbnail_imageView);
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/"+
                mMovie.getPosterPath()).into(mThumbnail);
        mReleaseDateTextView=(TextView)v.findViewById(R.id.movie_detail_release_date_textView);
        if (!mMovie.getReleasedDate().equals("null")) {
            mReleaseDateTextView.setText(mMovie.getReleasedDate());
        } else {
            mReleaseDateTextView.setText("Unknown");
        }

        if (!mMovie.getSynopsis().equals("null")) {
            mSynopsisTextView=(TextView)v.findViewById(R.id.movie_detail_plot_synopsis_textView);
            mSynopsisTextView.setText(mMovie.getSynopsis());
        }

        // Set the Favorite Button
        mFavoritedButton=(ToggleButton)v.findViewById(R.id.movie_detail_favorite_ToggleButton);
        // Check if the movie is in the favorite list. If yes, change the text to Favorited
        mFavoritedButton.setChecked(mMovieListSingleton.checkFavorite(mMovie.getMovieId()));
        // Set on Checked Change listener to add or remove the movie from the list
        mFavoritedButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                favoriteClicked=true; // Set the favorite button to click (to save file)
                if (isChecked) {
                    mMovieListSingleton.addFavorite(mMovie);
                } else {
                    mMovieListSingleton.deleteFavorite(mMovie);
                    if (prefString.equals("favorite")) {
                        // If the preference is in favorite list then update the adapter
                         mFavoriteCallbacks.onFavoriteRemoved();
                        // take user back to the previous activity
                        NavUtils.navigateUpFromSameTask(mActivity);
                    }
                }

            }
        });

        return v;
    }



    // Update the Trailer List View using the list
    private void updateTrailerListView() {
        LinearLayout trailerlayout=(LinearLayout)getView().findViewById(R.id.movie_detail_trailer);
        // empty the list view of trailers. This is used to prevent duplicating listview
        trailerlayout.removeAllViews();
        if (!trailerList.isEmpty()) {
            for (int i = 0; i < trailerList.size(); i++) {
                // Inflate the trailer layout and add the textview to the linear layout
                View v=getActivity().getLayoutInflater().inflate(R.layout.trailer_layout,null);
                TextView textView = (TextView)v.findViewById(R.id.trailer_layout_textView);
                final Trailer t = trailerList.get(i);
                // Set the name of the trailer
                textView.setText(t.getName());

                // Set onclick listener to the View to open the trailer in Youtube or other native app
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW,
                                // URI of the youtube link
                                Uri.parse("http://youtube.com/watch?v=" + t.getKey()));
                        startActivity(i);
                    }
                });
                trailerlayout.addView(v);
            }
            Log.d(TAG,"update trailer List View");
        }
    }


    private class FetchTrailerTask extends AsyncTask<String,Void,ArrayList<Trailer>> {
        @Override
        protected ArrayList<Trailer> doInBackground(String... params) {
            if (params.length==0) {
                return null;
            }
            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;
            String movieJsonString=null;
            try {

                // Build URL
                Uri.Builder builder=new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(params[0])
                        .appendPath("videos")
                        .appendQueryParameter("api_key", getString(R.string.api_key));
                String stringURL=builder.build().toString();
                Log.d(TAG, stringURL);
                URL url=new URL(stringURL);

                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream in= urlConnection.getInputStream();
                StringBuffer buffer=new StringBuffer();
                if (in==null) {
                    return null;
                }
                reader=new BufferedReader(new InputStreamReader(in));
                String line;

                while((line=reader.readLine())!=null) {
                    buffer.append(line+"\n");
                }

                if (buffer.length()==0) {
                    return null;
                }
                movieJsonString=buffer.toString();
                Log.d(TAG,movieJsonString);
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            } finally {
                // disconnect the urlConnection
                if (urlConnection!=null) {
                    urlConnection.disconnect();
                }
                if (reader!=null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG,"Error closing stream",e);
                    }
                }
            }
            try {
                return JSONToString(movieJsonString);
            } catch (JSONException e) {
            Log.e(TAG,e.getMessage(),e);
            e.printStackTrace();
            }
            return null;
        }

        //


        private ArrayList<Trailer> JSONToString(String s) throws JSONException{
            final String RESULT="results";
            final String KEY="key";
            final String NAME="name";
            ArrayList<Trailer> list=new ArrayList<Trailer>();


            JSONObject result=new JSONObject(s);
            JSONArray trailerList=result.getJSONArray(RESULT);
            // Create a string array with length equal to the length of the JSONArray


            for (int i=0;i<trailerList.length();i++){
                // Get the individual trailer object
                JSONObject trailer=trailerList.getJSONObject(i);
                // Add the trailer key to the list
                Trailer t=new Trailer();
                t.setKey(trailer.getString(KEY));
                t.setName(trailer.getString(NAME));
                list.add(t);
            }

            return list;

        }
        // Update the movie trailer list with the list obtained from the web
        @Override
        protected void onPostExecute(ArrayList<Trailer> trailer) {
            Log.d(TAG,"ONPOSTEXECUTED");
            trailerList.clear();
            for (Trailer t:trailer) {
             trailerList.add(t);
            }
            mMovie.setTrailer(trailerList);
            updateTrailerListView();
        }
    }
}
