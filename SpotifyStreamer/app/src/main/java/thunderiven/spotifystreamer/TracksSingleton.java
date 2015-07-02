package thunderiven.spotifystreamer;

import android.content.Context;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Vincent Ngo on 7/1/2015.
 * This class is a singleton that contains all the tracks for a specific artist
 */
public class TracksSingleton {
    private Context mContext;
    private static TracksSingleton sTracksSingleton;
    private ArrayList<Track> mTracks;

    private TracksSingleton(Context c) {
        mContext=c;
        mTracks=new ArrayList<Track>();
    }

    public static TracksSingleton get(Context c) {
        if (sTracksSingleton==null) {
            sTracksSingleton=new TracksSingleton(c);
        }
        return sTracksSingleton;
    }

    public void updateTracks(ArrayList<Track> tracks) {
        mTracks.clear();
        mTracks=tracks;
    }

    public ArrayList<Track> getTracks() {
        return mTracks;
    }
}
