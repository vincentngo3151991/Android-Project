package com.thunderivenstudio.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class ListMoviesFragment extends Fragment {
    private GridView mGridView;
    ImageAdapter adapter;
    private MovieListSingleton mMovieListSingleton;
    private ArrayList<Movies> mMoviesList;
    private static final String TAG="ListMoviesFragment";


    // override the onCreateView method


    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMovieListSingleton=MovieListSingleton.get(getActivity());
        mMoviesList=mMovieListSingleton.getMovies();
        View v=inflater.inflate(R.layout.pop_movies_fragment,container,false);

        mGridView=(GridView)v.findViewById(R.id.pop_movie_gridView);

        adapter=new ImageAdapter(getActivity());
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create intent and then open new MovieDetailActivity with corresponding class
                Intent i=new Intent(getActivity(),MovieDetailPagerActivity.class);
                // Get the movie
                Movies m=mMoviesList.get(position);
                i.putExtra(MovieDetailFragment.MOVIE_ID_EXTRA,m.getMovieId());
                startActivity(i);

            }
        });
        return v;
    }


    // This method is used to update the movies list
    private void updateMovies() {

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy=prefs.getString("Sort","popularity.desc");
        /* Check if the Shared Preference Sort Option is Favorite List, if it is Favorite List
        * then it will get the favorite list from the MovieListSingleton. Otherwise,
        * it fetches the data from TheMovieDataBase and get the list of most popular movie
        * sorts by Share Preference Sort Option
        */
        if (!sortBy.equals("favorite")) {
            FetchMovieListTask task=new FetchMovieListTask();
            task.execute(sortBy);
        } else {
            mMoviesList=new ArrayList<Movies>();
            mMoviesList=mMovieListSingleton.getFavorites();
            adapter.notifyDataSetChanged();
        }


    }

    // This class is used to create the ImageAdapter for the grid view
    private class ImageAdapter extends BaseAdapter {
        private Context mContext;
        public ImageAdapter(Context c) {
            mContext=c;
        }

        public int getCount() {
            return mMoviesList.size();
        }
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return Long.parseLong(mMoviesList.get(position).getMovieId());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            // URL to the image;
            if (convertView==null) {
                // create new image view
                imageView=new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(0,0,0,0);

            } else {
                imageView=(ImageView)convertView;
            }


            Picasso.with(mContext).load("http://image.tmdb.org/t/p/w342/"+
                             mMoviesList.get(position).getPosterPath()).into(imageView);
            return imageView;
        }
    }



    // this class is used to fetch the movie data using AsyncTask
    class FetchMovieListTask extends AsyncTask<String,Void,ArrayList<Movies>> {
        private static final String TAG="FetchMovieListTask";

        @Override
        protected ArrayList<Movies> doInBackground(String... params) {
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
                        .appendPath("discover")
                        .appendPath("movie")
                        .appendQueryParameter("sort_by",params[0])
                        .appendQueryParameter("api_key", getString(R.string.api_key));
                String stringURL=builder.build().toString();
                Log.d(TAG,stringURL);
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
                return getMoviesFromJson(movieJsonString);
            } catch (JSONException e) {
                Log.e(TAG,e.getMessage(),e);
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(ArrayList<Movies> movies) {
            if (!movies.isEmpty()) {
                // Update the new Movie List with the new movies
                mMovieListSingleton.updateMoviesList(movies);
                // Get the new movies list from movie list singleton
                mMoviesList=mMovieListSingleton.getMovies();
                adapter.notifyDataSetChanged();
            }
        }
    }

    private ArrayList<Movies> getMoviesFromJson(String movieData) throws JSONException {
        final String LIST="results";
        final String TITLE="title";
        final String MOVIE_ID="id";
        final String POSTER_PATH="poster_path";
        final String RATING="vote_average";
        final String SYNOPSIS="overview";
        final String RELEASE_DATE="release_date";

        JSONObject moviesListJSON=new JSONObject(movieData);
        // Create JSONArray from results
        JSONArray movieList=moviesListJSON.getJSONArray(LIST);
        // Create new movies Array
        ArrayList<Movies> moviesArray=new ArrayList<Movies>();
        for (int i=0;i<movieList.length();i++) {
            // Create new movie and add to the movies Array
            JSONObject movieJSON=movieList.getJSONObject(i);
            Movies m=new Movies();
            m.setTitle(movieJSON.getString(TITLE));
            m.setMovieId(movieJSON.getString(MOVIE_ID));
            m.setPosterPath(movieJSON.getString(POSTER_PATH));
            m.setRating(movieJSON.getString(RATING));
            m.setReleasedDate(movieJSON.getString(RELEASE_DATE));
            m.setSynopsis(movieJSON.getString(SYNOPSIS));
            moviesArray.add(m);
        }
        return moviesArray;
    }

}
