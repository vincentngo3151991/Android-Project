package com.thunderivenstudio.popularmoviesapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vincent Ngo on 6/26/2015.
 */
public class Movies {
    public String mMovieId;
    public String mReleasedDate;
    public String mTitle;
    public String mRating;
    public String mPosterPath;
    public String mSynopsis;
    public ArrayList<Trailer> mTrailer=new ArrayList<Trailer>();

    public String getMovieId() {
        return mMovieId;
    }

    public Movies() {
        // This is a blank constructor
    }

    public void setMovieId(String movieId) {
        mMovieId = movieId;
    }

    public String getReleasedDate() {
        return mReleasedDate;
    }

    public void setReleasedDate(String releasedDate) {
        mReleasedDate = releasedDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getRating() {
        return mRating;
    }

    public void setRating(String rating) {
        mRating = rating;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public void setSynopsis(String synopsis) {
        mSynopsis = synopsis;
    }

    public ArrayList<Trailer> getTrailer() {
        return mTrailer;
    }

    public void setTrailer(ArrayList<Trailer> trailer) {
        mTrailer.clear();
        mTrailer=trailer;

    }

    // This method is used to put the Movies into JSONObject
    private static final String JSON_MOVIE_ID="id";
    private static final String JSON_RELEASE_DATE="releasedate";
    private static final String JSON_TITLE="title";
    private static final String JSON_RATING="rating";
    private static final String JSON_PATH="path";
    private static final String JSON_SYNOPSIS="synopsis";
    private static final String JSON_TRAILER="trailer";
    private static final String JSON_TRAILER_NAME="name";
    private static final String JSON_TRAILER_KEY="trailerkey";

    public JSONObject toJSON() throws JSONException {
        JSONObject json=new JSONObject();
        json.put(JSON_MOVIE_ID,mMovieId);
        json.put(JSON_PATH,mPosterPath);
        json.put(JSON_RATING,mRating);
        json.put(JSON_RELEASE_DATE,mReleasedDate);
        json.put(JSON_SYNOPSIS,mSynopsis);
        json.put(JSON_TITLE,mTitle);

        // Put the list of trailer into JSON Array

        JSONArray jsonArray=new JSONArray();
        for (Trailer t:mTrailer) {
            JSONObject jsonTrailer=new JSONObject();
            jsonTrailer.put(JSON_TRAILER_NAME,t.getName());
            jsonTrailer.put(JSON_TRAILER_KEY,t.getKey());
            jsonArray.put(jsonTrailer);
        }
        json.put(JSON_TRAILER,jsonArray);
        return json;
    }

    public Movies(JSONObject json) throws JSONException {
        mMovieId=json.getString(JSON_MOVIE_ID);
        mPosterPath=json.getString(JSON_PATH);
        mRating=json.getString(JSON_RATING);
        mReleasedDate=json.getString(JSON_RELEASE_DATE);
        mSynopsis=json.getString(JSON_SYNOPSIS);
        mTitle=json.getString(JSON_TITLE);
        mTrailer=new ArrayList<Trailer>();
        JSONArray array=json.getJSONArray(JSON_TRAILER);
        for (int i=0;i<array.length();i++) {
            JSONObject ob=array.getJSONObject(i);
            Trailer t=new Trailer();
            t.setName(ob.getString(JSON_TRAILER_NAME));
            t.setKey(ob.getString(JSON_TRAILER_KEY));
            mTrailer.add(t);
        }
    }
}
