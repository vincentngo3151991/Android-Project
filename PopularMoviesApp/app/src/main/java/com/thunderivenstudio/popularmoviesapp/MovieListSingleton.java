package com.thunderivenstudio.popularmoviesapp;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Vincent Ngo on 6/27/2015.
 * This class is a singleton. It is used to create the movie list.
 * Using singleton for to store MovieList because it is easy to retrieve data from different
 * activities and fragment.
 */
public class MovieListSingleton {
    // The application context
    private Context mContext;
    // Create the arrayList
    private ArrayList<Movies> mMovies;

    // Create the Favorite movie list
    private ArrayList<Movies> mFavorites;
    // Create the instance of MovieList
    private static MovieListSingleton sMovieListSingleton;
    // For debug
    private static final String TAG="MovieListSingleton";

    // Private constructor for singleton. When it is call, then the new ArrayList is created
    private MovieListSingleton(Context c) {
        mContext=c;
        mMovies=new ArrayList<Movies>();
        mSerializer=new MoviesJSONSerializer(mContext,FILENAME);

        // try to load the favorite movies from file
        // if there is none then create new list
        try {
            mFavorites=mSerializer.loadMovies();
        } catch (Exception e) {
            mFavorites=new ArrayList<Movies>();
        }
    }

    public static MovieListSingleton get(Context c) {
        if (sMovieListSingleton==null) {
            sMovieListSingleton=new MovieListSingleton(c);
        }
        return sMovieListSingleton;
    }

    public ArrayList<Movies> getMovies() {
        return mMovies;
    }

    public ArrayList<Movies> getFavorites() {
        return mFavorites;
    }

    public boolean checkFavorite(String id) {
        for (Movies m:mFavorites) {
            if (m.getMovieId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public void addFavorite(Movies m) {
        mFavorites.add(m);
    }

    public void deleteFavorite(Movies m) {
        for (Movies movie:mFavorites) {
            if (movie.getMovieId().equals(m.getMovieId())) {
                mFavorites.remove(movie);
                break;
            }
        }

    }
    // Get movie from the list
    public Movies getMovie(String id) {
        for (Movies m:mMovies) {
            if (m.getMovieId().equals(id)) {
                return m;
            }
        }
        return null;
    }

    // Get movie from favorite list
    public Movies getFavoritedMovie(String id) {
        for (Movies m:mFavorites) {
            if (m.getMovieId().equals(id)) {

                return m;
            }
        }
        return null;
    }


    // This method is used to update the movie list everytime Asynctask is executed
    public void updateMoviesList(ArrayList<Movies> m) {
        mMovies.clear();
        mMovies=m;
    }

    // This is for save and load favorite movie
    private static final String FILENAME="Favorite.json";
    private MoviesJSONSerializer mSerializer;
    public boolean saveMovies() {
        try {
            mSerializer.saveMovies(mFavorites);
            Log.d(TAG, "Movies saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG,"Error saving movies: ",e);
            return false;
        }

    }

}
