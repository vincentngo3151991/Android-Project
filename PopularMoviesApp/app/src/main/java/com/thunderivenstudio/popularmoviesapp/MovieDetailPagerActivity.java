package com.thunderivenstudio.popularmoviesapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Vincent Ngo on 6/27/2015.
 */
public class MovieDetailPagerActivity extends AppCompatActivity implements MovieDetailFragment.FavoriteCallbacks {
    private ViewPager mViewPager;
    private ArrayList<Movies> mMovies;
    FragmentStatePagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the view pager for the activity
        mViewPager=new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);
        // Get the movie list from singleton class
        // Check the shared preference to see if it is favorite list or not
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);
        String name=pref.getString("Sort", "popularity.desc");
        if (!name.equals("favorite")) {
            mMovies=MovieListSingleton.get(this).getMovies();
        } else {
            mMovies=MovieListSingleton.get(this).getFavorites();
        }

        FragmentManager fm=getSupportFragmentManager();
        adapter=new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Movies movie = mMovies.get(position);

                return MovieDetailFragment.newInstance(movie.getMovieId());
            }



            @Override
            public int getCount() {
                return mMovies.size();
            }
        };
        // Set adapter for the view pager
        mViewPager.setAdapter(adapter);


        // Set the current item
        String id=(String)getIntent().getSerializableExtra(MovieDetailFragment.MOVIE_ID_EXTRA);
        for (int i=0;i<mMovies.size();i++) {
            if (mMovies.get(i).getMovieId().equals(id)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    // This method is used to remove the favorite movie from the favorite movie list
    // It will remove the movie from the list and update the adapter accordingly
    public void onFavoriteRemoved() {
        adapter.notifyDataSetChanged();
    }

}
